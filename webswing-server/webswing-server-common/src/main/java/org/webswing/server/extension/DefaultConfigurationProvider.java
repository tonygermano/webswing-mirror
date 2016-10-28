package org.webswing.server.extension;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.meta.ConfigContext;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.common.util.ConfigUtil;
import org.webswing.server.common.util.WebswingObjectMapper;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.model.exception.WsInitException;

public class DefaultConfigurationProvider implements ConfigurationProvider {
	private static final Logger log = LoggerFactory.getLogger(DefaultConfigurationProvider.class);

	protected Map<String, Object> configuration = new HashMap<String, Object>();

	public DefaultConfigurationProvider() throws WsInitException {
		loadConfiguration();
	}

	@Override
	public List<String> getPaths() {
		return new ArrayList<String>(configuration.keySet());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getConfiguration(String path) {
		return (Map<String, Object>) configuration.get(path);
	}

	@Override
	public SecuredPathConfig toSecuredPathConfig(String path, Map<String, Object> configuration) {
		HashMap<String, Object> copy = new HashMap<String, Object>(configuration);
		copy.put("path", path);
		SecuredPathConfig pathConfig = ConfigUtil.instantiateConfig(copy, SecuredPathConfig.class);
		return pathConfig;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveConfiguration(String path, Map<String, Object> configuration) throws Exception {
		try {
			File configFile = getConfigFile();
			Map<String, Object> json = WebswingObjectMapper.get().readValue(configFile, Map.class);
			configuration.put("path", path);
			json.put(path, configuration);
			WebswingObjectMapper.get().writerWithDefaultPrettyPrinter().writeValue(configFile, json);
			loadConfiguration();
		} catch (Exception e) {
			log.error("Failed to save Webswing configuration :", e);
			throw new Exception("Failed to save Webswing configuration :", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveSwingConfiguration(String path, Map<String, Object> configuration) throws Exception {
		try {
			File configFile = getConfigFile();
			Map<String, Object> json = WebswingObjectMapper.get().readValue(configFile, Map.class);
			Map<String, Object> pathJson = (Map<String, Object>) json.get(path);
			SecuredPathConfig newConfig = toSecuredPathConfig(path, configuration);
			Map<String, Object> newValue = newConfig.getSwingConfig().asMap();
			pathJson.put("swingConfig", newValue);
			WebswingObjectMapper.get().writerWithDefaultPrettyPrinter().writeValue(configFile, json);
			loadConfiguration();
		} catch (Exception e) {
			log.error("Failed to save Swing configuration for '" + path + "':", e);
			throw new Exception("Failed to save Swing configuration for '" + path + "':", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void removeConfiguration(String path) throws Exception {
		try {
			configuration.remove(path);
			File configFile = getConfigFile();
			Map<String, Object> json = WebswingObjectMapper.get().readValue(configFile, Map.class);
			json.remove(path);
			WebswingObjectMapper.get().writerWithDefaultPrettyPrinter().writeValue(configFile, json);
			loadConfiguration();
		} catch (Exception e) {
			log.error("Failed to save Webswing configuration :", e);
			throw new Exception("Failed to save Webswing configuration :", e);
		}
	}

	@Override
	public void validateConfiguration(String path, Map<String, Object> configuration) throws Exception {
		SecuredPathConfig c = toSecuredPathConfig(path, configuration);
		String spcPath = c.getPath();
		if (!path.equals(spcPath)) {
			throw new WsException("Invalid configuration: path '" + path + "' configuration refers to different path ('" + spcPath + "')");
		}
		try {
			WebswingObjectMapper.get().writeValueAsString(c);
		} catch (Exception e) {
			throw new WsException("Configuration Json is not valid.", e);
		}
	}

	@Override
	public MetaObject describeConfiguration(String path, Map<String, Object> json, ConfigContext ctx, ClassLoader cl) throws WsException {
		if (json == null) {
			json = getConfiguration(path);
		}
		SecuredPathConfig securedPathConfig = toSecuredPathConfig(path, json);
		try {
			MetaObject result = ConfigUtil.getConfigMetadata(securedPathConfig, cl, ctx);
			result.setData(json);
			if (ctx.isStarted() && !path.equals("/")) {
				result.setMessage("Note: Only Swing configuration can be modified while the application is running. Stop the application to edit the Security configuration.");
			}
			return result;
		} catch (Exception e) {
			log.error("Failed to generate configuration descriptor.", e);
			throw new WsException("Failed to generate configuration descriptor.");
		}
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> loadConfiguration() throws WsInitException {
		try {
			File config = getConfigFile();
			if (config.exists()) {
				Map<String, Object> json = WebswingObjectMapper.get().readValue(config, Map.class);
				configuration = json;
				return json;
			} else {
				throw new WsInitException("Configuration file " + config.getPath() + " does not exist!");
			}
		} catch (Exception e) {
			throw new WsInitException("Webswing application configuration failed to load:", e);
		}
	}

	protected File getConfigFile() throws WsInitException {
		return CommonUtil.getConfigFile();
	}

	@Override
	public Map<String, Object> createDefaultConfiguration(String path) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("path", path);
		return result;
	}

	@Override
	public boolean isMultiApplicationMode() {
		return true;
	}
}
