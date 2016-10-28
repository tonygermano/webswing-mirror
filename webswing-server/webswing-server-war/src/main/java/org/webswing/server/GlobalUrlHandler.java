package org.webswing.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.resources.ResourceHandlerService;
import org.webswing.server.services.security.api.BuiltInModules;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.api.WebswingSecurityConfig;
import org.webswing.server.services.security.login.LoginHandlerService;
import org.webswing.server.services.security.login.SecuredPathHandler;
import org.webswing.server.services.security.modules.SecurityModuleService;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;
import org.webswing.server.services.swingmanager.SwingInstanceManager;
import org.webswing.server.services.swingmanager.SwingInstanceManagerService;
import org.webswing.server.services.websocket.WebSocketService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GlobalUrlHandler extends PrimaryUrlHandler implements SwingInstanceHolder, SecuredPathHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalUrlHandler.class);

	private final WebSocketService websocket;
	private final ConfigurationService configService;
	private final SwingInstanceManagerService appFactory;
	private final ResourceHandlerService resourceService;
	private final LoginHandlerService loginService;

	private ServletContext servletContext;

	private Map<String, SwingInstanceManager> instanceManagers = new LinkedHashMap<String, SwingInstanceManager>();
	private boolean restartNeeded;

	@Inject
	public GlobalUrlHandler(WebSocketService websocket, ConfigurationService config, SwingInstanceManagerService appFactory, ResourceHandlerService resourceService, SecurityModuleService securityService, LoginHandlerService loginService, ServletContext servletContext) {
		super(null, securityService, config);
		this.websocket = websocket;
		this.configService = config;
		this.appFactory = appFactory;
		this.resourceService = resourceService;
		this.loginService = loginService;
		this.servletContext = servletContext;
	}

	public void init() {
		registerChildUrlHandler(websocket.createPlaybackWebSocketHandler(this));

		registerChildUrlHandler(loginService.createLoginHandler(this));
		registerChildUrlHandler(loginService.createLogoutHandler(this));

		registerChildUrlHandler(resourceService.create(this, this));

		loadApplications();
		super.init();
	}

	public void destroy() {
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
			getConfig().getSecurity().put("module", BuiltInModules.PROPERTY_FILE.name());
			secConfig = getConfig().getValueAs("security", WebswingSecurityConfig.class);
		}
		return secConfig;
	}

	public void loadApplications() {
		log.info("Loading configured Swing applications.");
		synchronized (instanceManagers) {
			for (String path : configService.getPaths()) {
				SecuredPathConfig configPath = configService.getConfiguration(path);
				String pathMapping = toPath(path);
				if (!toPath("/").equals(pathMapping)) {
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
		return "";
	}

	@Override
	public String getSecuredPath() {
		return getPath();
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ServletContext getServletContext() {
		return servletContext;
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
	public List<SwingInstanceManager> getApplications() {
		ArrayList<SwingInstanceManager> result = new ArrayList<>();
		synchronized (instanceManagers) {
			result.addAll(instanceManagers.values());
		}
		return result;
	}

	@Override
	public URL getWebResource(String resource) {
		if (StringUtils.isBlank(getConfig().getWebFolder()) && StringUtils.equals("/index.html", toPath(resource))) {
			resource = "/selector/index.html";
		}
		return super.getWebResource(resource);
	}

	@GET
	@Path("/apps")
	public List<ApplicationInfoMsg> getApplicationInfo(HttpServletRequest req) throws WsException {
		checkMasterPermission(WebswingAction.rest_getApps);
		List<ApplicationInfoMsg> result = new ArrayList<>();
		for (SwingInstanceManager mgr : getApplications()) {
			ApplicationInfoMsg applicationInfoMsg = mgr.getApplicationInfoMsg();
			if (applicationInfoMsg != null) {
				result.add(applicationInfoMsg);
			}
		}
		return result;
	}

	@GET
	@Path("/rest/paths")
	public List<String> getPaths(HttpServletRequest req) throws WsException {
		checkPermission(WebswingAction.rest_getPaths);
		List<String> result = new ArrayList<>();
		for (SwingInstanceManager appManager : getApplications()) {
			result.add(appManager.getFullPathMapping());
		}
		return result;
	}

	@GET
	@Path("/rest/version")
	public String getVersion() throws WsException {
		return super.getVersion();
	}

	@GET
	@Path("/rest/config")
	public MetaObject getConfigMeta() throws WsException {
		MetaObject meta = super.getConfigMeta();
		if (restartNeeded) {
			meta.setMessage("Configuration has been changed. Please restart the server to apply the changes.");
		}
		return meta;
	}

	@GET
	@Path("/rest/remove")
	public void getRemoveSwingApp(@PathParam("") String path) throws Exception {
		checkMasterPermission(WebswingAction.rest_removeApp);
		if (!StringUtils.isEmpty(path)) {
			SwingInstanceManager swingManager = instanceManagers.get(path);
			if (swingManager != null) {
				if (!swingManager.isStarted()) {
					configService.removeConfiguration(path);
					uninstallApplication(swingManager);
					return;
				} else {
					throw new WsException("Unable to Remove Swing app '" + path + "' while running. Stop the app first");
				}
			}
		}
		throw new WsException("Unable to remove Swing app '" + path + "'", HttpServletResponse.SC_BAD_REQUEST);
	}

	@GET
	@Path("/rest/create")
	public void getCreateSwingApp(@PathParam("") String path) throws Exception {
		checkMasterPermission(WebswingAction.rest_createApp);
		if (!StringUtils.isEmpty(path)) {
			SwingInstanceManager swingManager = instanceManagers.get(path);
			if (swingManager == null) {
				configService.setConfiguration(path, null);
				installApplication(configService.getConfiguration(path));
				return;
			} else {
				throw new WsException("Unable to Create Swing app '" + path + "'. Application already exits.");

			}
		}
		throw new WsException("Unable to create Swing app '" + path + "'", HttpServletResponse.SC_BAD_REQUEST);
	}

	@POST
	@Path("/rest/config")
	public void setConfig(Map<String, Object> config) throws Exception {
		checkMasterPermission(WebswingAction.rest_setConfig);
		config.put("path", "/");
		configService.setConfiguration("/", config);
		restartNeeded = true;
	}

	@GET
	@Path("/rest/permissions")
	public Map<String, Boolean> getPermissions() throws Exception {
		Map<String, Boolean> perm = super.getPermissions();
		boolean multiApplicationMode= configService.isMultiApplicationMode();
		perm.put("remove", multiApplicationMode && isMasterPermited(WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_removeApp));
		perm.put("create", multiApplicationMode && isMasterPermited(WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_createApp));
		return perm;
	}

	@POST
	@Path("/rest/metaConfig")
	public MetaObject getMeta(Map<String, Object> json) throws WsException {
		return super.getMeta(json);
	}

	@GET
	@Path("/rest/variables")
	public Map<String, String> getVariables(@PathParam("") String type) throws WsException {
		return super.getVariables(type);
	}
}
