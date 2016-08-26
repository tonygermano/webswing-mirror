package org.webswing.server.services.config;

import java.util.Map;

import org.webswing.server.base.WebswingService;
import org.webswing.server.common.model.SecuredPathConfig;

public interface ConfigurationService extends WebswingService {

	Map<String, SecuredPathConfig> getConfiguration();

	void setConfiguration(Map<String, Object> securedPathConfig) throws Exception;

	void setSwingConfiguration(Map<String, Object> securedPathConfig) throws Exception;

	void saveMasterConfiguration(Map<String, Object> securedPathConfig) throws Exception;

	void registerChangeListener(ConfigurationChangeListener listener);

	void removeChangeListener(ConfigurationChangeListener listener);

}
