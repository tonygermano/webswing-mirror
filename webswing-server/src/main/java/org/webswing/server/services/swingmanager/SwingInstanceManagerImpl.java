package org.webswing.server.services.swingmanager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.files.FileTransferHandler;
import org.webswing.server.services.files.FileTransferHandlerService;
import org.webswing.server.services.resources.ResourceHandlerService;
import org.webswing.server.services.rest.RestHandlerService;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.login.LoginHandlerService;
import org.webswing.server.services.security.login.WebswingSecurityProvider;
import org.webswing.server.services.security.modules.SecurityModuleService;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swinginstance.SwingInstanceService;
import org.webswing.server.services.websocket.WebSocketConnection;
import org.webswing.server.services.websocket.WebSocketService;

public class SwingInstanceManagerImpl extends AbstractUrlHandler implements SwingInstanceManager, WebswingSecurityProvider {
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
	private WebswingSecurityModule<?> securityModule;

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
		securityModule = securityModuleService.create(config.getSecurityMode(), config.getSecurityConfig());
		if (securityModule != null) {
			securityModule.init();
		}
		registerChildUrlHandler(websocket.createBinaryWebSocketHandler(this, this));
		registerChildUrlHandler(websocket.createJsonWebSocketHandler(this, this));

		registerChildUrlHandler(restService.createSwingRestHandler(this, this));
		registerChildUrlHandler(restService.createServerRestHandler(this));
		registerChildUrlHandler(restService.createSessionRestHandler(this, this));

		registerChildUrlHandler(loginService.createLoginHandler(this, getSecurityProvider()));
		registerChildUrlHandler(loginService.createLogoutHandler(this));
		registerChildUrlHandler(fileHandler);
		registerChildUrlHandler(resourceService.create(this, config.getWebFolder()));

		super.init();
	}

	@Override
	public void destroy() {
		super.destroy();
		if (securityModule != null) {
			securityModule.destroy();
		}
	}

	@Override
	public boolean serve(HttpServletRequest req, HttpServletResponse res) throws WsException {
		//redirect to url that ends with '/' to ensure browser queries correct resources 
		if (req.getPathInfo().equals(getFullPathMapping())) {
			try {
				String queryString = req.getQueryString() == null ? "" : ("?" + req.getQueryString());
				res.sendRedirect(getPath() + "/" + queryString);
			} catch (IOException e) {
				log.error("Failed to redirect.", e);
			}
			return true;
		} else {
			return super.serve(req, res);
		}
	}

	public SwingDescriptor getConfiguration() {
		return config;
	}

	@Override
	public void setConfig(SwingDescriptor newConfig) throws WsException {
		this.config = newConfig;
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
	@SuppressWarnings("unchecked")
	public WebswingSecurityModule<?> get() {
		return securityModule;
	}

}
