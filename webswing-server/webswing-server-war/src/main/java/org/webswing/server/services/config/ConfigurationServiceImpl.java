package org.webswing.server.services.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.base.WsInitException;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.common.util.ConfigUtil;
import org.webswing.server.model.exception.WsException;

import com.google.inject.Singleton;

@Singleton
public class ConfigurationServiceImpl implements ConfigurationService {

	private static final Logger log = LoggerFactory.getLogger(ConfigurationServiceImpl.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		mapper.disable(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS);
	}

	private Map<String, SecuredPathConfig> configuration = new HashMap<String, SecuredPathConfig>();
	private List<ConfigurationChangeListener> changeListeners = new ArrayList<ConfigurationChangeListener>();

	@Override
	public void start() throws WsInitException {
		loadConfiguration();
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
	public void setConfiguration(Map<String, Object> content) throws Exception {
		//TODO: validate, notify, save
	}

	@Override
	public void setSwingConfiguration(String path, Map<String, Object> content) throws Exception {
		//TODO: set, save

	}

	@SuppressWarnings("unchecked")
	private void loadConfiguration() throws WsInitException {
		try {
			File config = getConfigFile();
			Map<String, Object> json = mapper.readValue(config, Map.class);
			Map<String, SecuredPathConfig> result = new HashMap<String, SecuredPathConfig>();
			if (config.exists()) {
				for (String path : json.keySet()) {
					try {
						SecuredPathConfig pathConfig = loadPath(path, json);
						result.put(path, pathConfig);
					} catch (WsException e) {
						result.put(path, null);
						log.error("Failed to load configuration for path '" + path + "':", e);
					}
				}
				configuration = result;
			} else {
				throw new WsInitException("Configuration file " + config.getPath() + " does not exist!");
			}
		} catch (Exception e) {
			log.error("Webswing application configuration failed to load:", e);
			throw new WsInitException("Webswing application configuration failed to load:", e);
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
