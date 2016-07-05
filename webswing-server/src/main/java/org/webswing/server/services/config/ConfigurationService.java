package org.webswing.server.services.config;

import org.webswing.model.server.WebswingConfiguration;
import org.webswing.server.base.WebswingService;

public interface ConfigurationService extends WebswingService {

	WebswingConfiguration getConfiguration();

	void setConfiguration(WebswingConfiguration content) throws Exception;

	void registerChangeListener(ConfigurationChangeListener listener);

	void removeChangeListener(ConfigurationChangeListener listener);

}
