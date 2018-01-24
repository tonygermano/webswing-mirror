package org.webswing.server.extension;

import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.rest.RestService;
import org.webswing.server.services.rest.RestUrlHandler;
import org.webswing.server.services.swingprocess.SwingProcessService;

public interface ExtensionDependencies {

	SwingProcessService getProcessService();

	ConfigurationService getConfigService();

	RestUrlHandler createRestHandler(UrlHandler parent, Class... resources);
}
