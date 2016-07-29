package org.webswing.server.services.swingmanager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.model.server.WebswingSecurityConfig;
import org.webswing.model.server.WebswingSecurityConfig.BuiltInModules;
import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.files.FileTransferHandler;
import org.webswing.server.services.files.FileTransferHandlerService;
import org.webswing.server.services.resources.ResourceHandlerService;
import org.webswing.server.services.rest.RestHandlerService;
import org.webswing.server.services.security.api.SecurityContext;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.login.LoginHandlerService;
import org.webswing.server.services.security.login.WebswingSecurityProvider;
import org.webswing.server.services.security.modules.SecurityModuleService;
import org.webswing.server.services.security.modules.SecurityModuleWrapper;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swinginstance.SwingInstanceService;
import org.webswing.server.services.websocket.WebSocketConnection;
import org.webswing.server.services.websocket.WebSocketService;
import org.webswing.server.util.SecurityUtil;
import org.webswing.server.util.ServerUtil;

public class SwingInstanceManagerImpl extends AbstractUrlHandler implements SecurityContext, SwingInstanceManager, WebswingSecurityProvider {
	private static final Logger log = LoggerFactory.getLogger(SwingInstanceManagerImpl.class);

	private final SwingInstanceService instanceFactory;
	private final WebSocketService websocket;
	private final SecurityModuleService securityModuleService;
	private final LoginHandlerService loginService;
	private final FileTransferHandlerService fileService;
	private final ResourceHandlerService resourceService;
	private final RestHandlerService restService;
	private SwingDescriptor config;
	private FileTransferHandler fileHandler;
	private SwingInstanceSet swingInstances = new SwingInstanceSet();
	private SwingInstanceSet closedInstances = new SwingInstanceSet();
	private SecurityModuleWrapper securityModule;
	private File webFolder;

	public SwingInstanceManagerImpl(SwingInstanceService instanceFactory, WebSocketService websocket, FileTransferHandlerService fileService, LoginHandlerService loginService, ResourceHandlerService resourceService, SecurityModuleService securityModuleService, RestHandlerService restService, UrlHandler parent, SwingDescriptor config) {
		super(parent);
		this.instanceFactory = instanceFactory;
		this.websocket = websocket;
		this.securityModuleService = securityModuleService;
		this.loginService = loginService;
		this.fileService = fileService;
		this.fileHandler = fileService.create(this);
		this.resourceService = resourceService;
		this.restService = restService;
		this.config = config;
	}

	@Override
	public void init() {
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
	}

	@Override
	public boolean serve(HttpServletRequest req, HttpServletResponse res) throws WsException {
		//CORS headers handling
		handleCorsHeaders(req, res);
		return super.serve(req, res);
	}

	protected boolean isPrimaryHandler() {
		return true;
	}

	private void loadSecurityModule() {
		WebswingSecurityConfig securityConfig = config.getSecurityConfig();
		if (securityConfig == null) {
			securityConfig = new WebswingSecurityConfig(BuiltInModules.INHERITED.name());
		}
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
		List<String> allowedCorsOrigins = config.getAllowedCorsOrigins();
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

	public SwingDescriptor getConfiguration() {
		return config;
	}

	@Override
	public void setConfig(SwingDescriptor newConfig) throws WsException {
		this.config = newConfig;
		if (securityModule != null) {
			securityModule.destroy();
			loadSecurityModule();
		}
		webFolder = resolveFile(config.getWebFolder());
	}

	@Override
	protected String getPath() {
		return config.getPath();
	}

	public void startSwingInstance(WebSocketConnection r, ConnectionHandshakeMsgIn h) {
		if (r.hasPermission(WebswingAction.websocket_startSwingApplication)) {
			if (!h.isMirrored()) {
				if (!reachedMaxConnections()) {
					try {
						SwingInstance swingInstance = instanceFactory.create(this, fileHandler, h, config, r);
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
			log.error("Authorization error: User " + r.getUser() + " is not authorized to connect to application " + config.getName() + (h.isMirrored() ? " [Mirrored view only available for admin role]" : ""));
		}
	}

	public void shutdown(String id, boolean force) {
		SwingInstance si = swingInstances.findByClientId(id);
		si.shutdown(force);
	}

	private boolean reachedMaxConnections() {
		if (config.getMaxClients() < 0) {
			return false;
		} else if (config.getMaxClients() == 0) {
			return true;
		} else {
			return swingInstances.size() >= config.getMaxClients();
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
	public List<SwingDescriptor> getAllConfiguredApps() {
		return Arrays.asList(config);
	}

	@Override
	public SecurityModuleWrapper get() {
		return securityModule;
	}

	@Override
	public File resolveFile(String name) {
		return ServerUtil.resolveFile(name, config.getHomeDir(), null);
	}

	@Override
	public URL getWebResource(String resource) {
		return ServerUtil.getWebResource(toPath(resource), getServletContext(), webFolder);
	}

	@Override
	public String replaceVariables(String string) {
		return ServerUtil.getConfigSubstitutor().replace(string);
	}

	@Override
	public Object getFromSecuritySession(String attributeName) {
		return SecurityUtil.getFromSecuritySession(attributeName);
	}

	@Override
	public void setToSecuritySession(String attributeName, Object value) {
		SecurityUtil.setToSecuritySession(attributeName, value);
	}

	@Override
	public WebswingSecurityProvider getSecurityProviderForApp(String path) {
		if (config.getPath().equals(path)) {
			return getSecurityProvider();
		}
		return null;
	}
}
