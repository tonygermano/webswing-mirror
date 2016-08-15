package org.webswing.server;

import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.config.ConfigurationServiceImpl;
import org.webswing.server.services.files.FileTransferHandlerService;
import org.webswing.server.services.files.FileTransferHandlerServiceImpl;
import org.webswing.server.services.jms.JmsService;
import org.webswing.server.services.jms.JmsServiceImpl;
import org.webswing.server.services.jvmconnection.JvmConnectionService;
import org.webswing.server.services.jvmconnection.JvmConnectionServiceImpl;
import org.webswing.server.services.resources.ResourceHandlerService;
import org.webswing.server.services.resources.ResourceHandlerServiceImpl;
import org.webswing.server.services.rest.RestHandlerService;
import org.webswing.server.services.rest.RestHandlerServiceImpl;
import org.webswing.server.services.security.SecurityManagerService;
import org.webswing.server.services.security.SecurityManagerServiceImpl;
import org.webswing.server.services.security.login.LoginHandlerService;
import org.webswing.server.services.security.login.LoginHandlerServiceImpl;
import org.webswing.server.services.security.modules.SecurityModuleService;
import org.webswing.server.services.security.modules.SecurityModuleServiceImpl;
import org.webswing.server.services.startup.StartupService;
import org.webswing.server.services.startup.StartupServiceImpl;
import org.webswing.server.services.swinginstance.SwingInstanceService;
import org.webswing.server.services.swinginstance.SwingInstanceServiceImpl;
import org.webswing.server.services.swingmanager.SwingInstanceManagerService;
import org.webswing.server.services.swingmanager.SwingInstanceManagerServiceImpl;
import org.webswing.server.services.swingprocess.SwingProcessService;
import org.webswing.server.services.swingprocess.SwingProcessServiceImpl;
import org.webswing.server.services.websocket.WebSocketService;
import org.webswing.server.services.websocket.WebSocketServiceImpl;

import com.google.inject.AbstractModule;

public class WebswingServerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(GlobalUrlHandler.class);

		bind(StartupService.class).to(StartupServiceImpl.class);
		bind(WebSocketService.class).to(WebSocketServiceImpl.class);
		bind(JmsService.class).to(JmsServiceImpl.class);
		bind(ConfigurationService.class).to(ConfigurationServiceImpl.class);
		bind(SwingInstanceManagerService.class).to(SwingInstanceManagerServiceImpl.class);
		bind(SwingInstanceService.class).to(SwingInstanceServiceImpl.class);
		bind(JvmConnectionService.class).to(JvmConnectionServiceImpl.class);
		bind(SwingProcessService.class).to(SwingProcessServiceImpl.class);
		bind(FileTransferHandlerService.class).to(FileTransferHandlerServiceImpl.class);
		bind(ResourceHandlerService.class).to(ResourceHandlerServiceImpl.class);
		bind(RestHandlerService.class).to(RestHandlerServiceImpl.class);

		bind(SecurityManagerService.class).to(SecurityManagerServiceImpl.class);
		bind(SecurityModuleService.class).to(SecurityModuleServiceImpl.class);
		bind(LoginHandlerService.class).to(LoginHandlerServiceImpl.class);
	}

}
