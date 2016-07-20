package org.webswing.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.server.SecurityMode;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.model.server.WebswingConfiguration;
import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationChangeListener;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.resources.ResourceHandlerService;
import org.webswing.server.services.rest.RestHandlerService;
import org.webswing.server.services.security.api.SecurityContext;
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
import org.webswing.server.util.ServerUtil;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GlobalUrlHandler extends AbstractUrlHandler implements SwingInstanceHolder, WebswingSecurityProvider, SecurityContext {
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

	private ConfigurationChangeListener changeListener;
	private File webFolder;

	@Inject
	public GlobalUrlHandler(WebSocketService websocket, ConfigurationService config, SwingInstanceManagerService appFactory, ResourceHandlerService resourceService, RestHandlerService restService, SecurityModuleService securityService, LoginHandlerService loginService) {
		super(null);
		this.websocket = websocket;
		this.config = config;
		this.appFactory = appFactory;
		this.resourceService = resourceService;
		this.restService = restService;
		this.securitySecurity = securityService;
		this.loginService = loginService;

	}

	public boolean serve(HttpServletRequest req, HttpServletResponse res) {
		try {
			boolean served = super.serve(req, res);
			if (!served) {
				throw new WsException("Not Found.");
			}
		} catch (Exception e) {
			handleException(e, req, res);
		}
		return true;
	}

	public void init() {
		registerChildUrlHandler(websocket.createBinaryWebSocketHandler(this, this));
		registerChildUrlHandler(websocket.createJsonWebSocketHandler(this, this));
		registerChildUrlHandler(restService.createSwingRestHandler(this, this));
		registerChildUrlHandler(restService.createServerRestHandler(this));
		registerChildUrlHandler(restService.createConfigRestHandler(this));
		registerChildUrlHandler(restService.createSessionRestHandler(this, this));
		registerChildUrlHandler(loginService.createLoginHandler(this, getSecurityProvider()));
		registerChildUrlHandler(loginService.createLogoutHandler(this));
		registerChildUrlHandler(resourceService.create(this, this));

		reloadSecurityModule(config.getConfiguration());
		reloadConfiguration(config.getConfiguration());
		config.registerChangeListener(changeListener = new ConfigurationChangeListener() {

			@Override
			public void notifyChange() {
				reloadSecurityModule(config.getConfiguration());
				reloadConfiguration(config.getConfiguration());
			}
		});
		super.init();
	}

	public void destroy() {
		config.removeChangeListener(changeListener);
		if (securityModule != null) {
			securityModule.destroy();
		}
		super.destroy();
	}

	private void reloadSecurityModule(WebswingConfiguration configuration) {
		log.info("Reloading master security module.(" + configuration.getMasterSecurityMode() + ").");
		if (securityModule != null) {
			securityModule.destroy();
		}
		SecurityMode mode = configuration.getMasterSecurityMode();
		if (mode.equals(SecurityMode.INHERITED)) {
			log.error("Master security mode INHERITED is not valid. Falling back to default mode PROPERTY_FILE.");
			mode = SecurityMode.PROPERTY_FILE;
		}
		Map<String, Object> secConfig = configuration.getMasterSecurityConfig();
		if (mode == SecurityMode.PROPERTY_FILE) {
			if (secConfig.get("file") == null) {
				secConfig = new HashMap<>(secConfig);
				secConfig.put("file", "${user.dir}/user.properties");
			}
		}
		securityModule = securitySecurity.create(this, mode, secConfig);
		securityModule.init();
	}

	public void reloadConfiguration(WebswingConfiguration newConfig) {
		log.info("Reloading configured Swing applications.");
		synchronized (instanceManagers) {
			Set<String> installedPathsToRemove = new HashSet<String>(instanceManagers.keySet());
			for (SwingDescriptor swing : ServerUtil.getAllApps(newConfig)) {
				String pathMapping = toPath(swing.getPath());
				SwingInstanceManager childHandler = instanceManagers.get(pathMapping);
				if (childHandler != null) {
					SwingDescriptor oldConfig = childHandler.getConfiguration();
					if (!EqualsBuilder.reflectionEquals(oldConfig, swing)) {
						updateApplication(childHandler, swing);
					}
				} else {
					installApplication(swing);
				}
				installedPathsToRemove.remove(pathMapping);
			}
			for (String pathToRemove : installedPathsToRemove) {
				SwingInstanceManager appToRemove = instanceManagers.get(pathToRemove);
				uninstallApplication(appToRemove);
			}
		}
		webFolder = resolveFile(config.getConfiguration().getMasterWebFolder());
	}

	public void installApplication(SwingDescriptor swing) {
		try {
			log.info("Installing application " + swing);
			validateConfig(swing, true);
			SwingInstanceManager app = appFactory.createApp(this, swing);
			registerFirstChildUrlHandler(app);
		} catch (Exception e) {
			log.error("Failed to install application '" + swing.getName() + "' (" + swing.getPath() + ").", e);
			//TODO: status to admin console
		}
	}

	public void updateApplication(SwingInstanceManager oldApp, SwingDescriptor newConfig) {
		try {
			log.info("Updating application " + newConfig);
			validateConfig(newConfig, false);
			oldApp.setConfig(newConfig);
		} catch (WsException e) {
			log.error("Failed to reload application '" + newConfig.getName() + "' (" + newConfig.getPath() + ").", e);
			//TODO: status to admin console
		}
	}

	public void uninstallApplication(SwingInstanceManager appToRemove) {
		try {
			log.info("Removing application " + appToRemove.getConfiguration());
			appToRemove.destroy();
			removeChildUrlHandler(appToRemove);
		} catch (Exception e) {
			log.error("Failed to uninstall application '" + appToRemove.getConfiguration().getName() + "' (" + appToRemove.getConfiguration().getPath() + ").", e);
			//TODO: if running, add callback to uninstall when stopped
		}
	}

	private void validateConfig(SwingDescriptor config, boolean installing) throws WsException {
		if (StringUtils.isEmpty(config.getPath())) {
			throw new WsException("Application path mapping must not be empty.");
		}
		if (installing && instanceManagers.containsKey(config.getPath())) {
			throw new WsException("Application with the same path already installed.");
		}
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
		return null;
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
	public List<SwingDescriptor> getAllConfiguredApps() {
		return ServerUtil.getAllApps(config.getConfiguration());
	}

	@Override
	public SecurityModuleWrapper get() {
		return securityModule;
	}

	@Override
	public File resolveFile(String name) {
		return ServerUtil.resolveFile(name, ".", null);
	}

	@Override
	public URL getWebResource(String resource) {
		return ServerUtil.getWebResource(toPath(resource), getServletContext(), webFolder);
	}

	@Override
	public String replaceVariables(String string) {
		return ServerUtil.getConfigSubstitutor().replace(string);
	}

}
