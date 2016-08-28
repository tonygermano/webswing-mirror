package org.webswing.server.services.swingmanager;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.model.admin.ApplicationInfo;
import org.webswing.server.common.model.admin.InstanceManagerStatus;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.files.FileTransferHandler;
import org.webswing.server.services.files.FileTransferHandlerService;
import org.webswing.server.services.resources.ResourceHandlerService;
import org.webswing.server.services.rest.RestHandlerService;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.login.LoginHandlerService;
import org.webswing.server.services.security.modules.SecurityModuleService;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swinginstance.SwingInstanceService;
import org.webswing.server.services.websocket.WebSocketConnection;
import org.webswing.server.services.websocket.WebSocketService;

public class SwingInstanceManagerImpl extends PrimaryUrlHandler implements SwingInstanceManager {
	private static final Logger log = LoggerFactory.getLogger(SwingInstanceManagerImpl.class);

	private final String path;
	private final SwingInstanceService instanceFactory;
	private final WebSocketService websocket;
	private final LoginHandlerService loginService;
	private final ResourceHandlerService resourceService;
	private final RestHandlerService restService;
	private FileTransferHandler fileHandler;
	private SwingInstanceSet runningInstances = new SwingInstanceSet();
	private SwingInstanceSet closedInstances = new SwingInstanceSet();

	public SwingInstanceManagerImpl(UrlHandler parent, String path, SwingInstanceService instanceFactory, WebSocketService websocket, FileTransferHandlerService fileService, LoginHandlerService loginService, ResourceHandlerService resourceService, SecurityModuleService securityModuleService, RestHandlerService restService,
			ConfigurationService configService) {
		super(parent, securityModuleService, configService);
		this.path = path;
		this.instanceFactory = instanceFactory;
		this.websocket = websocket;
		this.loginService = loginService;
		this.fileHandler = fileService.create(this);
		this.resourceService = resourceService;
		this.restService = restService;
	}

	@Override
	public void init() {
		registerChildUrlHandler(websocket.createBinaryWebSocketHandler(this, this));
		registerChildUrlHandler(websocket.createJsonWebSocketHandler(this, this));

		registerChildUrlHandler(restService.createAdminRestHandler(this, this));

		registerChildUrlHandler(loginService.createLoginHandler(this));
		registerChildUrlHandler(loginService.createLogoutHandler(this));
		registerChildUrlHandler(fileHandler);
		registerChildUrlHandler(resourceService.create(this, this));
		super.init();
	}

	@Override
	protected String getPath() {
		return path;
	}

	public void startSwingInstance(WebSocketConnection r, ConnectionHandshakeMsgIn h) {
		if (r.hasPermission(WebswingAction.websocket_startSwingApplication)) {
			if (!h.isMirrored()) {
				if (!reachedMaxConnections()) {
					try {
						SwingInstance swingInstance = instanceFactory.create(this, fileHandler, h, getSwingConfig(), r);
						runningInstances.add(swingInstance);
					} catch (Exception e) {
						log.error("Failed to create Swing instance.", e);
					}
				} else {
					r.broadcastMessage(SimpleEventMsgOut.tooManyClientsNotification.buildMsgOut());
				}
			} else {
				r.broadcastMessage(SimpleEventMsgOut.configurationError.buildMsgOut());
			}
		} else {
			log.error("Authorization error: User " + r.getUser() + " is not authorized to connect to application " + getSwingConfig().getName() + (h.isMirrored() ? " [Mirrored view only available for admin role]" : ""));
		}
	}

	public void shutdown(String id, boolean force) {
		SwingInstance si = runningInstances.findByClientId(id);
		si.shutdown(force);
	}

	private boolean reachedMaxConnections() {
		if (getSwingConfig().getMaxClients() < 0) {
			return false;
		} else if (getSwingConfig().getMaxClients() == 0) {
			return true;
		} else {
			return runningInstances.size() >= getSwingConfig().getMaxClients();
		}
	}

	public void notifySwingClose(SwingInstance swingInstance) {
		if (!closedInstances.contains(swingInstance)) {
			closedInstances.add(swingInstance);
		}
		runningInstances.remove(swingInstance.getInstanceId());
	}

	@Override
	public SwingInstance findInstanceBySessionId(String uuid) {
		return runningInstances.findBySessionId(uuid);
	}

	@Override
	public SwingInstance findInstanceByClientId(String clientId) {
		return runningInstances.findByClientId(clientId);
	}

	@Override
	public SwingInstance findByInstanceId(ConnectionHandshakeMsgIn handshake, WebSocketConnection r) {
		return runningInstances.findByInstanceId(handshake, r);
	}

	@Override
	public List<SwingInstance> getAllInstances() {
		return runningInstances.getAllInstances();
	}

	@Override
	public List<SwingInstance> getAllClosedInstances() {
		return closedInstances.getAllInstances();
	}

	@Override
	public List<SwingInstanceManager> getApplications() {
		return Arrays.asList(this);
	}

	@Override
	public ApplicationInfoMsg getApplicationInfoMsg() {
		if (getConfig().getSwingConfig() == null) {
			return null;
		}
		ApplicationInfoMsg app = new ApplicationInfoMsg();
		app.setName(getSwingConfig().getName());
		app.setAlwaysRestart(getSwingConfig().getSwingSessionTimeout() == 0);
		app.setUrl(getFullPathMapping());
		File icon = resolveFile(getConfig().getIcon());
		app.setBase64Icon(CommonUtil.loadImage(icon));
		return app;
	}
	@GET
	@Path("/apps")
	public List<ApplicationInfoMsg> getApplicationInfo(HttpServletRequest req) throws WsException {
		checkPermission(WebswingAction.rest_getApps);
		return Arrays.asList(getApplicationInfoMsg());
	}

	@GET
	@Path("/rest/paths")
	public List<String> getApplications(HttpServletRequest req) throws WsException {
		checkPermissionLocalOrMaster(WebswingAction.rest_getPaths);
		return Arrays.asList(getFullPathMapping());
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
		if (isStarted()) {
			meta.setMessage("Note: Only Swing configuration can be modified while the application is running. Stop the application to edit the Security configuration.");
		}
		return meta;
	}

	@POST
	@Path("/rest/config")
	public void setConfig(Map<String, Object> config) throws Exception {
		super.setConfig(config);
	}

	@POST
	@Path("/rest/swingConfig")
	public void setSwingConfig(Map<String, Object> config) throws Exception {
		super.setSwingConfig(config);
	}

	@GET
	@Path("/rest/permissions")
	public Map<String, Boolean> getPermissions() throws Exception {
		return super.getPermissions();
	}

	@GET
	@Path("/status")
	public String getStatusPage() throws Exception {
		InstanceManagerStatus status = getStatus();
		URL webResource = getWebResource(status.getStatus().name() + ".html");
		if (webResource != null) {
			return IOUtils.toString(webResource);
		}
		return null;
	}

	@GET
	@Path("/start")
	public void start() throws WsException {
		super.start();
	}

	@GET
	@Path("/stop")
	public void stop() throws WsException {
		if (runningInstances.size() > 0) {
			throw new WsException("Can not Stop if swing sessions are running. Please stop sessions first.");
		}
		super.stop();
	}

	@GET
	@Path("/info")
	public ApplicationInfo getApplicationInfo() throws WsException {
		checkPermissionLocalOrMaster(WebswingAction.rest_getAppInfo);
		ApplicationInfo app = new ApplicationInfo();
		app.setPath(getPathMapping());
		app.setUrl(getFullPathMapping());
		app.setName(getSwingConfig().getName());
		File icon = resolveFile(getConfig().getIcon());
		app.setIcon(CommonUtil.loadImage(icon));
		app.setConfig(getConfig());
		app.setRunningInstances(runningInstances.size());

		int connected = 0;
		for (SwingInstance si : runningInstances.getAllInstances()) {
			if (si.getSessionId() != null) {
				connected++;
			}
		}
		app.setConnectedInstances(connected);
		app.setFinishedInstances(closedInstances.size());
		int maxRunningInstances = getSwingConfig().getMaxClients();
		app.setMaxRunningInstances(maxRunningInstances);
		app.setStatus(getStatus());
		return app;
	}

}
