package org.webswing.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.admin.InstanceManagerStatus;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationChangeEvent;
import org.webswing.server.services.config.ConfigurationChangeListener;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.extension.ExtensionService;
import org.webswing.server.services.resources.ResourceHandlerService;
import org.webswing.server.services.rest.RestService;
import org.webswing.server.services.security.api.BuiltInModules;
import org.webswing.server.services.security.api.WebswingSecurityConfig;
import org.webswing.server.services.security.login.LoginHandlerService;
import org.webswing.server.services.security.login.SecuredPathHandler;
import org.webswing.server.services.security.modules.SecurityModuleService;
import org.webswing.server.services.swingmanager.SwingInstanceManager;
import org.webswing.server.services.swingmanager.SwingInstanceManagerService;
import org.webswing.server.services.websocket.WebSocketService;

@Singleton
public class GlobalUrlHandler extends PrimaryUrlHandler implements SecuredPathHandler{
	private static final Logger log = LoggerFactory.getLogger(GlobalUrlHandler.class);

	private final WebSocketService websocket;
	private final ConfigurationService configService;
	private final SwingInstanceManagerService appFactory;
	private final ResourceHandlerService resourceService;
	private final LoginHandlerService loginService;

	private ServletContext servletContext;
	private final ExtensionService extService;
	private final RestService restService;

	private Map<String, SwingInstanceManager> instanceManagers = new LinkedHashMap<String, SwingInstanceManager>();

	private final ConfigurationChangeListener changeListener = new ConfigurationChangeListener() {
		@Override
		public void onConfigChanged(ConfigurationChangeEvent e) {
			if ("/".equals(e.getPath())) {
				initConfiguration();
			} else {
				SwingInstanceManager manager = instanceManagers.get(e.getPath());
				if (manager == null) {
					installApplication(e.getNewConfig()).init();
				} else if (manager.isEnabled()) {
					manager.initConfiguration();
				}
			}
		}

		@Override
		public void onConfigDeleted(ConfigurationChangeEvent e) {
			if ("/".equals(e.getPath())) {
				initConfiguration();
			} else {
				SwingInstanceManager manager = instanceManagers.get(e.getPath());
				if (manager != null) {
					uninstallApplication(manager);
				}
			}
		}
	};

	@Inject
	public GlobalUrlHandler(WebSocketService websocket, ConfigurationService config, SwingInstanceManagerService appFactory, ResourceHandlerService resourceService, SecurityModuleService securityService, LoginHandlerService loginService, ServletContext servletContext, ExtensionService extService, RestService restService) {
		super(null, securityService, config);
		this.websocket = websocket;
		this.configService = config;
		this.appFactory = appFactory;
		this.resourceService = resourceService;
		this.loginService = loginService;
		this.servletContext = servletContext;
		this.extService = extService;
		this.restService = restService;
	}

	public void init() {
		registerChildUrlHandler(websocket.createPlaybackWebSocketHandler(this));

		registerChildUrlHandler(loginService.createLoginHandler(this));
		registerChildUrlHandler(loginService.createLogoutHandler(this));

		for (UrlHandler handler : extService.createExtHandlers(this)) {
			registerChildUrlHandler(handler);
		}

		registerChildUrlHandler(resourceService.create(this, this));
		registerChildUrlHandler(restService.createGlobalRestHandler(this));

		loadApplications();
		this.configService.registerChangeListener(this.changeListener);
		super.init();
		if (!InstanceManagerStatus.Status.Running.equals(getStatus().getStatus())) {
			throw new RuntimeException("Failed to start primary handler.");
		}
	}

	public void destroy() {
		this.configService.removeChangeListener(this.changeListener);
		instanceManagers.clear();
		super.destroy();
	}

	public boolean serve(HttpServletRequest req, HttpServletResponse res) {
		try {
			boolean served = super.serve(req, res);
			if (!served) {
				throw new WsException("Not Found.", HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			handleException(e, req, res);
		}
		return true;
	}

	@Override
	protected WebswingSecurityConfig getSecurityConfig() {
		log.info("Loading master security module.(" + getConfig().getSecurity() + ").");
		WebswingSecurityConfig secConfig = super.getSecurityConfig();
		if (BuiltInModules.INHERITED.name().equals(secConfig.getModule())) {
			log.error("Master security module INHERITED is not valid. Falling back to default module PROPERTY_FILE.");
			SecuredPathConfig newconfig = getConfig();
			newconfig.getSecurity().put("module", BuiltInModules.PROPERTY_FILE.name());
			secConfig = newconfig.getValueAs("security", WebswingSecurityConfig.class);
		}
		return secConfig;
	}

	public void loadApplications() {
		log.info("Loading configured Applications.");
		synchronized (instanceManagers) {
			for (String path : configService.getPaths()) {
				SecuredPathConfig configPath = configService.getConfiguration(path);
				String pathMapping = toPath(path);
				if (!toPath("/").equals(pathMapping)) {
					SwingInstanceManager childHandler = instanceManagers.get(pathMapping);
					if (childHandler == null) {
						installApplication(configPath);
					} else {
						log.error("Application with path '" + pathMapping + "' already exists! Application skipped.", new IllegalStateException("Invalid Application configuration."));
					}
				}
			}
		}
	}

	public SwingInstanceManager installApplication(SecuredPathConfig swing) {
		log.info("Installing application " + swing.getPath());
		SwingInstanceManager app = appFactory.createApp(this, swing.getPath());
		registerFirstChildUrlHandler(app);
		return app;
	}

	public void uninstallApplication(SwingInstanceManager appToRemove) {
		log.info("Removing application " + appToRemove.getPathMapping());
		appToRemove.destroy();
		removeChildUrlHandler(appToRemove);
	}

	private void handleException(Exception e, HttpServletRequest req, HttpServletResponse res) {
		log.debug("Failed to process request. " + req.getPathInfo(), e);
		try {
			if (e instanceof WsException) {
				if (!res.isCommitted()) {
					WsException wse = (WsException) e;
					res.sendError(wse.getReponseCode(), wse.getLocalizedMessage());
				}
			} else {
				log.error("Failed to process request. " + req.getPathInfo(), e);
				if (!res.isCommitted()) {
					res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					e.printStackTrace(new PrintStream(res.getOutputStream()));
				}
			}
		} catch (IOException e1) {
			log.error("Failed send error response to client. ");
		}

	}

	@Override
	public void registerFirstChildUrlHandler(UrlHandler handler) {
		super.registerFirstChildUrlHandler(handler);
		if (handler instanceof SwingInstanceManager) {
			synchronized (instanceManagers) {
				SwingInstanceManager manager = (SwingInstanceManager) handler;
				instanceManagers.put(manager.getPathMapping(), manager);
			}
		}
	}

	@Override
	public void registerChildUrlHandler(UrlHandler handler) {
		super.registerChildUrlHandler(handler);
		if (handler instanceof SwingInstanceManager) {
			synchronized (instanceManagers) {
				SwingInstanceManager manager = (SwingInstanceManager) handler;
				instanceManagers.put(manager.getPathMapping(), manager);
			}
		}
	}

	@Override
	public void removeChildUrlHandler(UrlHandler handler) {
		super.removeChildUrlHandler(handler);
		if (handler instanceof SwingInstanceManager) {
			synchronized (instanceManagers) {
				SwingInstanceManager manager = (SwingInstanceManager) handler;
				instanceManagers.remove(manager.getPathMapping(), manager);
			}
		}
	}

	protected String getPath() {
		return "";
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public List<SwingInstanceManager> getApplications() {
		ArrayList<SwingInstanceManager> result = new ArrayList<>();
		synchronized (instanceManagers) {
			result.addAll(instanceManagers.values());
		}
		return result;
	}

	public SwingInstanceManager getApplication(String path) {
		synchronized (instanceManagers) {
			return instanceManagers.get(path);
		}
	}

	@Override
	public URL getWebResource(String resource) {
		if (!isCustomIndexPage() && StringUtils.equals("/index.html", toPath(resource))) {
			resource = System.getProperty(Constants.DEFAULT_WELCOME_PAGE, "/selector/index.html");
		}
		return super.getWebResource(resource);
	}

	private boolean isCustomIndexPage() {
		String customFolder = getConfig().getWebFolder();
		if (StringUtils.isBlank(customFolder)) {
			return false;
		}
		File customFolderFile = resolveFile(customFolder);
		if (customFolderFile.isDirectory() && new File(customFolderFile, "index.html").isFile()) {
			return true;
		}
		return false;
	}

}
