package org.webswing.server.services.config;

import java.util.Map;

import org.webswing.model.server.SwingAppletDescriptor;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.model.server.WebswingConfiguration;
import org.webswing.model.server.admin.ServerProperties;
import org.webswing.model.server.admin.UserConfiguration;
import org.webswing.server.base.WebswingService;

public interface ConfigurationService extends WebswingService{
	void applyUserProperties(UserConfiguration users) throws Exception;

	UserConfiguration loadUserProperties();

	WebswingConfiguration getLiveConfiguration();

	ServerProperties getServerProperties();

	Map<String, SwingApplicationDescriptor> getApplications();

	Map<String, SwingAppletDescriptor> getApplets();

	SwingApplicationDescriptor getApplication(String name);

	void registerChangeListener(ConfigurationChangeListener listener);

	void saveApplicationConfiguration(WebswingConfiguration content) throws Exception;

}
