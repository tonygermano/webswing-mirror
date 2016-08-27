package org.webswing.server.services.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.base.WsInitException;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.common.util.ConfigUtil;
import org.webswing.server.common.util.WebswingObjectMapper;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.WebswingSecurityConfig;

import com.google.inject.Singleton;

@Singleton
public class ConfigurationServiceImpl implements ConfigurationService {

	private static final Logger log = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

	private Map<String, SecuredPathConfig> configuration = new HashMap<String, SecuredPathConfig>();
	private List<ConfigurationChangeListener> changeListeners = new ArrayList<ConfigurationChangeListener>();

	@Override
	public void start() throws WsInitException {
		configuration = loadConfiguration();
	}

	@Override
	public void stop() {
		configuration = new HashMap<String, SecuredPathConfig>();
		synchronized (changeListeners) {
			changeListeners.clear();
		}
	}

	@Override
	public Map<String, SecuredPathConfig> getConfiguration() {
		return configuration;
	}

	@Override
	public void setConfiguration(Map<String, Object> securedPathConfig) throws Exception {
		SecuredPathConfig config = ConfigUtil.instantiateConfig(securedPathConfig, SecuredPathConfig.class);
		validateConfiguration(config);
		SecuredPathConfig oldConfig = configuration.get(config.getPath());
		configuration.put(config.getPath(), config);
		saveConfiguration(securedPathConfig);
		notifyChange(config.getPath(), oldConfig, config);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setSwingConfiguration(Map<String, Object> securedPathConfig) throws Exception {
		SecuredPathConfig config = ConfigUtil.instantiateConfig(securedPathConfig, SecuredPathConfig.class);
		validateConfiguration(config);

		SecuredPathConfig oldConfig = configuration.get(config.getPath());
		if (oldConfig != null) {
			Map newValue = config.getSwingConfig().asMap();
			Map oldSwingMap = oldConfig.getSwingConfig().asMap();
			oldSwingMap.clear();
			oldSwingMap.putAll(newValue);
			saveSwingConfiguration(config.getPath(), newValue);
		} else {
			throw new WsException("No Application found for path '" + config.getPath() + "'");
		}
	}

	public void saveMasterConfiguration(Map<String, Object> securedPathConfig) throws Exception {
		SecuredPathConfig config = ConfigUtil.instantiateConfig(securedPathConfig, SecuredPathConfig.class);
		validateConfiguration(config);
		saveConfiguration(securedPathConfig);
	}

	private void validateConfiguration(SecuredPathConfig c) throws Exception {
		validateObject(c);
		validateObject(c.getValueAs("security", WebswingSecurityConfig.class));
	}
	
	private void validateObject(Object o) throws Exception {
		//test getters
		try {
			WebswingObjectMapper.get().writeValueAsString(o);
		} catch (Exception e) {
			throw new WsException("Configuration Json is not valid.", e);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, SecuredPathConfig> loadConfiguration() throws WsInitException {
		try {
			Map<String, SecuredPathConfig> result = new HashMap<String, SecuredPathConfig>();
			File config = getConfigFile();
			if (config.exists()) {
				Map<String, Object> json = WebswingObjectMapper.get().readValue(config, Map.class);
				for (String path : json.keySet()) {
					try {
						SecuredPathConfig pathConfig = loadPath(path, json);
						validateConfiguration(pathConfig);
						result.put(path, pathConfig);
					} catch (WsException e) {
						result.put(path, null);
						log.error("Failed to load configuration for path '" + path + "':", e);
					}
				}
				return result;
			} else {
				throw new WsInitException("Configuration file " + config.getPath() + " does not exist!");
			}
		} catch (Exception e) {
			log.error("Webswing application configuration failed to load:", e);
			throw new WsInitException("Webswing application configuration failed to load:", e);
		}
	}

	@SuppressWarnings("unchecked")
	private void saveConfiguration(Map<String, Object> configuration) throws Exception {
		try {
			File configFile = getConfigFile();
			Map<String, Object> json = WebswingObjectMapper.get().readValue(configFile, Map.class);
			json.put((String) configuration.get("path"), configuration);
			WebswingObjectMapper.get().writerWithDefaultPrettyPrinter().writeValue(configFile, json);
		} catch (Exception e) {
			log.error("Failed to save Webswing configuration :", e);
			throw new Exception("Failed to save Webswing configuration :", e);
		}
	}

	@SuppressWarnings("unchecked")
	private void saveSwingConfiguration(String path, Map<String, Object> newValue) throws Exception {
		try {
			File configFile = getConfigFile();
			Map<String, Object> json = WebswingObjectMapper.get().readValue(configFile, Map.class);
			Map<String, Object> pathJson = (Map<String, Object>) json.get(path);
			pathJson.put("swingConfig", newValue);
			WebswingObjectMapper.get().writerWithDefaultPrettyPrinter().writeValue(configFile, json);
		} catch (Exception e) {
			log.error("Failed to save Swing configuration for '" + path + "':", e);
			throw new Exception("Failed to save Swing configuration for '" + path + "':", e);
		}
	}

	public void registerChangeListener(ConfigurationChangeListener listener) {
		synchronized (changeListeners) {
			changeListeners.add(listener);
		}
	}

	public void removeChangeListener(ConfigurationChangeListener listener) {
		synchronized (changeListeners) {
			changeListeners.remove(listener);
		}
	}

	@SuppressWarnings("unchecked")
	private SecuredPathConfig loadPath(String path, Map<String, Object> swingConfig) throws WsException {
		SecuredPathConfig pathConfig = ConfigUtil.instantiateConfig((Map<String, Object>) swingConfig.get(path), SecuredPathConfig.class);
		String spcPath = pathConfig.getPath();
		if (!path.equals(spcPath)) {
			throw new WsException("Invalid configuration: path '" + path + "' configuration refers to different path ('" + spcPath + "')");
		}
		return pathConfig;
	}

	private File getConfigFile() {
		return CommonUtil.getConfigFile();
	}

	private void notifyChange(String path, SecuredPathConfig oldCfg, SecuredPathConfig newCfg) {
		synchronized (changeListeners) {
			for (ConfigurationChangeListener listener : changeListeners) {
				if (listener != null) {
					listener.notifyChange(new ConfigurationChangeEvent(path, oldCfg, newCfg));
				}
			}
		}
	}

}
