package org.webswing.server.services.swingmanager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.model.admin.ApplicationInfo;
import org.webswing.server.common.model.admin.InstanceManagerStatus;
import org.webswing.server.common.model.admin.InstanceManagerStatus.Status;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.files.FileTransferHandler;
import org.webswing.server.services.files.FileTransferHandlerService;
import org.webswing.server.services.resources.ResourceHandlerService;
import org.webswing.server.services.rest.RestHandlerService;
import org.webswing.server.services.security.api.SecurityContext;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.api.WebswingSecurityConfig;
import org.webswing.server.services.security.login.LoginHandlerService;
import org.webswing.server.services.security.login.WebswingSecurityProvider;
import org.webswing.server.services.security.modules.SecurityModuleService;
import org.webswing.server.services.security.modules.SecurityModuleWrapper;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swinginstance.SwingInstanceService;
import org.webswing.server.services.websocket.WebSocketConnection;
import org.webswing.server.services.websocket.WebSocketService;

public class SwingInstanceManagerImpl extends PrimaryUrlHandler implements SecurityContext, SwingInstanceManager, WebswingSecurityProvider {
	private static final Logger log = LoggerFactory.getLogger(SwingInstanceManagerImpl.class);

	private final String path;
	private final SwingInstanceService instanceFactory;
	private final WebSocketService websocket;
	private final SecurityModuleService securityModuleService;
	private final LoginHandlerService loginService;
	private final ResourceHandlerService resourceService;
	private final RestHandlerService restService;
	private FileTransferHandler fileHandler;
	private SwingInstanceSet swingInstances = new SwingInstanceSet();
	private SwingInstanceSet closedInstances = new SwingInstanceSet();
	private SecurityModuleWrapper securityModule;

	private InstanceManagerStatus status = new InstanceManagerStatus();

	public SwingInstanceManagerImpl(UrlHandler parent, String path, SwingInstanceService instanceFactory, WebSocketService websocket, FileTransferHandlerService fileService, LoginHandlerService loginService, ResourceHandlerService resourceService, SecurityModuleService securityModuleService, RestHandlerService restService,
			ConfigurationService config) {
		super(parent, config);
		this.path = path;
		this.instanceFactory = instanceFactory;
		this.websocket = websocket;
		this.securityModuleService = securityModuleService;
		this.loginService = loginService;
		this.fileHandler = fileService.create(this);
		this.resourceService = resourceService;
		this.restService = restService;
	}

	@Override
	public void init() {
		try {
			status.setStatus(Status.Starting);
			loadSecurityModule();
			registerChildUrlHandler(websocket.createBinaryWebSocketHandler(this, this));
			registerChildUrlHandler(websocket.createJsonWebSocketHandler(this, this));

			registerChildUrlHandler(restService.createSwingRestHandler(this, this));
			registerChildUrlHandler(restService.createServerRestHandler(this));
			registerChildUrlHandler(restService.createSessionRestHandler(this, this));
			registerChildUrlHandler(restService.createOtpRestHandler(this, this));

			registerChildUrlHandler(loginService.createLoginHandler(this, getSecurityProvider()));
			registerChildUrlHandler(loginService.createLogoutHandler(this));
			registerChildUrlHandler(fileHandler);
			registerChildUrlHandler(resourceService.create(this, this));

			super.init();
			status.setStatus(Status.Running);
		} catch (Throwable e) {
			log.error("Failed to start '" + path + "'.", e);
			try {
				destroy();
			} catch (Throwable e1) {
				//do nothing
			}
			status.setStatus(Status.Error);
			status.setError(e.getMessage());
			StringWriter out = new StringWriter();
			PrintWriter stacktrace = new PrintWriter(out);
			e.printStackTrace(stacktrace);
			status.setErrorDetails(out.toString());
		}
	}

	@Override
	public boolean serve(HttpServletRequest req, HttpServletResponse res) throws WsException {
		//CORS headers handling
		if (status.getStatus().equals(Status.Running)) {
			handleCorsHeaders(req, res);
			return super.serve(req, res);
		} else {
			try {
				res.sendRedirect(getFullPathMapping() + "/error.html");
				return true;
			} catch (IOException e) {
				throw new WsException(e);
			}
		}
	}

	private void loadSecurityModule() {
		WebswingSecurityConfig securityConfig = getConfig().getValueAs("security", WebswingSecurityConfig.class);
		securityModule = securityModuleService.create(this, securityConfig);
		if (securityModule != null) {
			securityModule.init();
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		if (securityModule != null) {
			securityModule.destroy();
		}
		status.setStatus(Status.Stopped);
	}

	@Override
	protected String getPath() {
		return path;
	}

	private void handleCorsHeaders(HttpServletRequest req, HttpServletResponse res) {
		if (isOriginAllowed(req.getHeader("Origin"))) {
			if (req.getHeader("Origin") != null) {
				res.addHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
				res.addHeader("Access-Control-Allow-Credentials", "true");
				res.addHeader("Access-Control-Expose-Headers", Constants.HTTP_ATTR_ARGS + ", " + Constants.HTTP_ATTR_RECORDING_FLAG + ", X-Cache-Date, X-Atmosphere-tracking-id, X-Requested-With");
			}

			if ("OPTIONS".equals(req.getMethod())) {
				res.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
				res.addHeader("Access-Control-Allow-Headers", Constants.HTTP_ATTR_ARGS + ", " + Constants.HTTP_ATTR_RECORDING_FLAG + ", X-Requested-With, Origin, Content-Type, Content-Range, Content-Disposition, Content-Description, X-Atmosphere-Framework, X-Cache-Date, X-Atmosphere-tracking-id, X-Atmosphere-Transport");
				res.addHeader("Access-Control-Max-Age", "-1");
			}
		}
	}

	private boolean isOriginAllowed(String header) {
		List<String> allowedCorsOrigins = getSwingConfig().getAllowedCorsOrigins();
		if (allowedCorsOrigins == null || allowedCorsOrigins.size() == 0) {
			return false;
		}
		for (String s : allowedCorsOrigins) {
			if (s.trim().equals(header) || s.trim().equals("*")) {
				return true;
			}
		}
		return false;
	}

	public void startSwingInstance(WebSocketConnection r, ConnectionHandshakeMsgIn h) {
		if (r.hasPermission(WebswingAction.websocket_startSwingApplication)) {
			if (!h.isMirrored()) {
				if (!reachedMaxConnections()) {
					try {
						SwingInstance swingInstance = instanceFactory.create(this, fileHandler, h, getSwingConfig(), r);
						swingInstances.add(swingInstance);
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
		SwingInstance si = swingInstances.findByClientId(id);
		si.shutdown(force);
	}

	private boolean reachedMaxConnections() {
		if (getSwingConfig().getMaxClients() < 0) {
			return false;
		} else if (getSwingConfig().getMaxClients() == 0) {
			return true;
		} else {
			return swingInstances.size() >= getSwingConfig().getMaxClients();
		}
	}

	public void notifySwingClose(SwingInstance swingInstance) {
		if (!closedInstances.contains(swingInstance)) {
			closedInstances.add(swingInstance);
		}
		swingInstances.remove(swingInstance.getInstanceId());
	}

	@Override
	public SwingInstance findInstanceBySessionId(String uuid) {
		return swingInstances.findBySessionId(uuid);
	}

	@Override
	public SwingInstance findInstanceByClientId(String clientId) {
		return swingInstances.findByClientId(clientId);
	}

	@Override
	public SwingInstance findByInstanceId(ConnectionHandshakeMsgIn handshake, WebSocketConnection r) {
		return swingInstances.findByInstanceId(handshake, r);
	}

	@Override
	public List<SwingInstance> getAllInstances() {
		return swingInstances.getAllInstances();
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
	public SecurityModuleWrapper get() {
		return securityModule;
	}

	@Override
	public WebswingSecurityProvider getSecurityProviderForApp(String path) {
		if (getConfig().getPath().equals(path)) {
			return getSecurityProvider();
		}
		return null;
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

	@Override
	public ApplicationInfo getApplicationInfo() {
		ApplicationInfo app = new ApplicationInfo();
		app.setPath(getPathMapping());
		app.setUrl(getFullPathMapping());
		app.setName(getSwingConfig().getName());
		File icon = resolveFile(getConfig().getIcon());
		app.setIcon(CommonUtil.loadImage(icon));
		app.setConfig(getConfig());
		app.setRunningInstances(swingInstances.size());

		int connected = 0;
		for (SwingInstance si : swingInstances.getAllInstances()) {
			if (si.getSessionId() != null) {
				connected++;
			}
		}
		app.setConnectedInstances(connected);
		app.setFinishedInstances(closedInstances.size());
		int maxRunningInstances = getSwingConfig().getMaxClients();
		app.setMaxRunningInstances(maxRunningInstances);
		app.setStatus(status);
		return app;
	}

}
