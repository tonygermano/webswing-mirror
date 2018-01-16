package org.webswing.server.services.swingmanager.instance;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.extension.ExtensionService;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.files.FileTransferHandlerService;
import org.webswing.server.services.resources.ResourceHandlerService;
import org.webswing.server.services.rest.RestService;
import org.webswing.server.services.security.login.LoginHandlerService;
import org.webswing.server.services.security.modules.SecurityModuleService;
import org.webswing.server.services.stats.StatisticsLoggerService;
import org.webswing.server.services.swinginstance.SwingInstanceService;
import org.webswing.server.services.swingmanager.SwingInstanceManager;
import org.webswing.server.services.swingmanager.SwingInstanceManagerImpl;
import org.webswing.server.services.websocket.WebSocketService;

@Singleton
public class SwingInstanceHolderServiceImpl implements SwingInstanceHolderService {

	@Inject
	public SwingInstanceHolderServiceImpl() {
	}

	@Override
	public SwingInstanceHolder createInstanceHolder() {
		return new SwingInstanceHolderImpl();
	}
}
