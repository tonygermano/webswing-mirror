package org.webswing.server;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.resources.ResourceHandlerService;
import org.webswing.server.services.rest.RestHandlerService;
import org.webswing.server.services.security.api.BuiltInModules;
import org.webswing.server.services.security.api.SecurityContext;
import org.webswing.server.services.security.api.WebswingSecurityConfig;
import org.webswing.server.services.security.login.LoginHandlerService;
import org.webswing.server.services.security.login.WebswingSecurityProvider;
import org.webswing.server.services.security.modules.SecurityModuleService;
import org.webswing.server.services.security.modules.SecurityModuleWrapper;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;
import org.webswing.server.services.swingmanager.SwingInstanceManager;
import org.webswing.server.services.swingmanager.SwingInstanceManagerService;
import org.webswing.server.services.websocket.WebSocketConnection;
import org.webswing.server.services.websocket.WebSocketService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GlobalUrlHandler extends PrimaryUrlHandler implements SwingInstanceHolder, WebswingSecurityProvider, SecurityContext {
	private static final Logger log = LoggerFactory.getLogger(GlobalUrlHandler.class);

	private final WebSocketService websocket;
	private final ConfigurationService config;
	private final SwingInstanceManagerService appFactory;
	private final ResourceHandlerService resourceService;
	private final RestHandlerService restService;
	private final LoginHandlerService loginService;
	private final SecurityModuleService securitySecurity;

	private ServletContext servletContext;
	private SecurityModuleWrapper securityModule;

	private Map<String, SwingInstanceManager> instanceManagers = new LinkedHashMap<String, SwingInstanceManager>();

	@Inject
	public GlobalUrlHandler(WebSocketService websocket, ConfigurationService config, SwingInstanceManagerService appFactory, ResourceHandlerService resourceService, RestHandlerService restService, SecurityModuleService securityService, LoginHandlerService loginService, ServletContext servletContext) {
		super(null, config);
		this.websocket = websocket;
		this.config = config;
		this.appFactory = appFactory;
		this.resourceService = resourceService;
		this.restService = restService;
		this.securitySecurity = securityService;
		this.loginService = loginService;
		this.servletContext = servletContext;
	}

	public void init() {
		registerChildUrlHandler(websocket.createBinaryWebSocketHandler(this, this));
		registerChildUrlHandler(websocket.createJsonWebSocketHandler(this, this));
		registerChildUrlHandler(websocket.createPlaybackWebSocketHandler(this, this));

		registerChildUrlHandler(restService.createSwingRestHandler(this, this));
		registerChildUrlHandler(restService.createServerRestHandler(this));
		registerChildUrlHandler(restService.createConfigRestHandler(this));
		registerChildUrlHandler(restService.createSessionRestHandler(this, this));
		registerChildUrlHandler(restService.createOtpRestHandler(this, this));

		registerChildUrlHandler(loginService.createLoginHandler(this, getSecurityProvider()));
		registerChildUrlHandler(loginService.createLogoutHandler(this));

		registerChildUrlHandler(resourceService.create(this, this));

		loadSecurityModule();
		loadConfiguration(config.getConfiguration());
		super.init();
	}

	public void destroy() {
		if (securityModule != null) {
			securityModule.destroy();
		}
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

	private void loadSecurityModule() {
		log.info("Reloading master security module.(" + getConfig().getSecurity() + ").");
		if (securityModule != null) {
			securityModule.destroy();
		}
		WebswingSecurityConfig config = getConfig().getValueAs("security", WebswingSecurityConfig.class);
		if (BuiltInModules.INHERITED.name().equals(config.getModule())) {
			log.error("Master security module INHERITED is not valid. Falling back to default module PROPERTY_FILE.");
			getConfig().getSecurity().put("module", BuiltInModules.PROPERTY_FILE.name());
			config = getConfig().getValueAs("security", WebswingSecurityConfig.class);
		}
		securityModule = securitySecurity.create(this, config);
		securityModule.init();
	}

	public void loadConfiguration(Map<String, SecuredPathConfig> newConfig) {
		log.info("Loading configured Swing applications.");
		synchronized (instanceManagers) {
			for (SecuredPathConfig configPath : newConfig.values()) {
				String pathMapping = toPath(configPath.getPath());
				if (!getPathMapping().equals(pathMapping)) {
					SwingInstanceManager childHandler = instanceManagers.get(pathMapping);
					if (childHandler == null) {
						installApplication(configPath);
					} else {
						log.error("Application with path '" + pathMapping + "' already exists! Application skipped.", new IllegalStateException("Invalid Swing configuration."));
					}
				}
			}
		}
	}

	public void installApplication(SecuredPathConfig swing) {
		log.info("Installing application " + swing.getPath());
		SwingInstanceManager app = appFactory.createApp(this, swing.getPath());
		registerFirstChildUrlHandler(app);
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
		return "/";
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public SwingInstance findByInstanceId(ConnectionHandshakeMsgIn handshake, WebSocketConnection r) {
		synchronized (instanceManagers) {
			for (SwingInstanceManager im : instanceManagers.values()) {
				SwingInstance instance;
				if ((instance = im.findByInstanceId(handshake, r)) != null) {
					return instance;
				}
			}
		}
		return null;
	}

	@Override
	public SwingInstance findInstanceBySessionId(String uuid) {
		synchronized (instanceManagers) {
			for (SwingInstanceManager im : instanceManagers.values()) {
				SwingInstance instance;
				if ((instance = im.findInstanceBySessionId(uuid)) != null) {
					return instance;
				}
			}
		}
		return null;
	}

	@Override
	public SwingInstance findInstanceByClientId(String clientId) {
		synchronized (instanceManagers) {
			for (SwingInstanceManager im : instanceManagers.values()) {
				SwingInstance instance;
				if ((instance = im.findInstanceByClientId(clientId)) != null) {
					return instance;
				}
			}
		}
		return null;
	}

	@Override
	public List<SwingInstance> getAllInstances() {
		ArrayList<SwingInstance> result = new ArrayList<>();
		synchronized (instanceManagers) {
			for (SwingInstanceManager im : instanceManagers.values()) {
				result.addAll(im.getAllInstances());
			}
		}
		return result;
	}

	@Override
	public List<SwingInstance> getAllClosedInstances() {
		ArrayList<SwingInstance> result = new ArrayList<>();
		synchronized (instanceManagers) {
			for (SwingInstanceManager im : instanceManagers.values()) {
				result.addAll(im.getAllClosedInstances());
			}
		}
		return result;
	}

	@Override
	public List<SecuredPathConfig> getAllConfiguredApps() {
		return new ArrayList<SecuredPathConfig>(config.getConfiguration().values());
	}

	@Override
	public SecurityModuleWrapper get() {
		return securityModule;
	}

	@Override
	public WebswingSecurityProvider getSecurityProviderForApp(String path) {
		SwingInstanceManager im = instanceManagers.get(toPath(path));
		if (im != null) {
			return im.getSecurityProvider();
		}
		return null;
	}

}
