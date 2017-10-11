package org.webswing.server.extension;

import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.swingprocess.SwingProcessService;

public interface ExtensionDependencies {

	SwingProcessService getProcessService();

	ConfigurationService getConfigService();
}
