package org.webswing.server.services.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.server.WebswingConfiguration;
import org.webswing.model.server.WebswingConfigurationBackup;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.util.ServerUtil;

import com.google.inject.Singleton;

@Singleton
public class ConfigurationServiceImpl implements ConfigurationService {

	private static final Logger log = LoggerFactory.getLogger(ConfigurationServiceImpl.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final int MAX_BACKUP_HISTORY = 10;
	static{
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		mapper.disable(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS);
	}

	private WebswingConfiguration liveConfiguration = new WebswingConfiguration();
	private List<ConfigurationChangeListener> changeListeners = new ArrayList<ConfigurationChangeListener>();
	private long configFileLastModified = -1;

	@Override
	public void start() {
		reloadConfiguration();
	}

	@Override
	public void stop() {
		liveConfiguration = null;
		synchronized (changeListeners) {
			changeListeners.clear();
		}
	}

	public WebswingConfiguration getConfiguration() {
		if (configFileLastModified != getConfigFileModificationTime()) {
			log.info("Webswing configuration file has been modified. Reloadinig active configuration. [" + getConfigFile().getAbsolutePath() + "]");
			reloadConfiguration();
		}
		return liveConfiguration;
	}

	private void reloadConfiguration() {
		try {
			WebswingConfiguration loaded = loadApplicationConfiguration();
			if (loaded != null) {
				configFileLastModified = getConfigFileModificationTime();
				applyApplicationConfiguration(loaded);
			}
		} catch (Exception e) {
			log.error("Webswing application configuration failed to load:", e);
		}
	}

	public void setConfiguration(WebswingConfiguration content) throws Exception {
		boolean applied = applyApplicationConfiguration(content);
		if (applied) {
			File config = getConfigFile();
			mapper.writerWithDefaultPrettyPrinter().writeValue(config, content);
			configFileLastModified = getConfigFileModificationTime();
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

	private boolean applyApplicationConfiguration(WebswingConfiguration content) throws Exception {
		if (content != null && !EqualsBuilder.reflectionEquals(liveConfiguration, content)) {
			backupApplicationConfiguration(liveConfiguration);
			liveConfiguration = content;
			notifyChange();
			return true;
		}
		return false;
	}

	private void backupApplicationConfiguration(WebswingConfiguration configuration) throws Exception {
		File backupFile = getConfigBackupFile();
		WebswingConfigurationBackup backup = null;
		if (backupFile.exists()) {
			try {
				backup = loadApplicationConfigurationBackup();
			} catch (Exception e) {
				backup = new WebswingConfigurationBackup();
			}
			if (backup.getBackupMap().size() >= MAX_BACKUP_HISTORY) {
				// removeLast
				List<Date> dates = new ArrayList<Date>(backup.getBackupMap().keySet());
				Collections.sort(dates);
				backup.getBackupMap().remove(dates.get(dates.size() - 1));
			}
			backup.getBackupMap().put(new Date(), configuration);
		} else {
			backup = new WebswingConfigurationBackup();
			backup.getBackupMap().put(new Date(), configuration);
		}
		mapper.writerWithDefaultPrettyPrinter().writeValue(backupFile, backup);
	}

	private WebswingConfigurationBackup loadApplicationConfigurationBackup() {
		try {
			WebswingConfigurationBackup result = new WebswingConfigurationBackup();
			File backup = getConfigBackupFile();
			if (backup.exists()) {
				result = mapper.readValue(backup, WebswingConfigurationBackup.class);
				return result;
			} else {
				log.warn("Configuration backup file " + backup.getPath() + " does not exist.");
				return null;
			}
		} catch (Exception e) {
			log.error("Failed to load backup configuration file:", e);
			return null;
		}
	}

	private WebswingConfiguration loadApplicationConfiguration() throws WsException {
		try {
			WebswingConfiguration result = new WebswingConfiguration();
			File config = getConfigFile();
			if (config.exists()) {
				result = mapper.readValue(config, WebswingConfiguration.class);
				return result;
			} else {
				throw new WsException("Configuration file " + config.getPath() + " does not exist!");
			}
		} catch (Exception e) {
			throw new WsException("Failed to load configuration file:", e);
		}
	}

	private long getConfigFileModificationTime() {
		File cf = getConfigFile();
		if (cf.exists() && cf.canRead()) {
			return cf.lastModified();
		}
		return configFileLastModified;
	}

	private File getConfigFile() {
		return ServerUtil.getConfigFile(false);
	}

	private File getConfigBackupFile() {
		return ServerUtil.getConfigFile(true);
	}

	private void notifyChange() {
		synchronized (changeListeners) {
			for (ConfigurationChangeListener listener : changeListeners) {
				if (listener != null) {
					listener.notifyChange();
				}
			}
		}
	}

}
