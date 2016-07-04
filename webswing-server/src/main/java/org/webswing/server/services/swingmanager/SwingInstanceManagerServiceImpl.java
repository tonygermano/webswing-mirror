package org.webswing.server.services.swingmanager;

import org.webswing.model.server.SwingDescriptor;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.files.FileTransferHandlerService;
import org.webswing.server.services.resources.ResourceHandlerService;
import org.webswing.server.services.security.login.LoginHandlerService;
import org.webswing.server.services.security.modules.SecurityModuleService;
import org.webswing.server.services.swinginstance.SwingInstanceService;
import org.webswing.server.services.websocket.WebSocketService;

import com.google.inject.Inject;

public class SwingInstanceManagerServiceImpl implements SwingInstanceManagerService {

	private final WebSocketService websocket;
	private final SwingInstanceService instanceService;
	private final FileTransferHandlerService fileService;
	private final LoginHandlerService loginService;
	private final ResourceHandlerService resourceHandler;
	private final SecurityModuleService securityModuleService;

	@Inject
	public SwingInstanceManagerServiceImpl(SwingInstanceService instanceFactory, WebSocketService websocket, FileTransferHandlerService fileHandler, LoginHandlerService loginHandler, ResourceHandlerService resourceHandler, SecurityModuleService securityModuleService) {
		this.instanceService = instanceFactory;
		this.websocket = websocket;
		this.fileService = fileHandler;
		this.loginService = loginHandler;
		this.resourceHandler = resourceHandler;
		this.securityModuleService = securityModuleService;
	}

	public SwingInstanceManager createApp(UrlHandler parent, SwingDescriptor config) {
		return new SwingInstanceManagerImpl(instanceService, websocket, fileService, loginService, resourceHandler,securityModuleService, parent, config);
	}

}
