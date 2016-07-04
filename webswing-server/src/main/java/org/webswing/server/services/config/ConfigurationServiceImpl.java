package org.webswing.server.services.config;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.server.SwingAppletDescriptor;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.model.server.WebswingConfiguration;
import org.webswing.model.server.WebswingConfigurationBackup;
import org.webswing.model.server.admin.ServerProperties;
import org.webswing.model.server.admin.UserConfiguration;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.util.ServerUtil;

import com.google.inject.Singleton;

@Singleton
public class ConfigurationServiceImpl implements ConfigurationService {

	private static final Logger log = LoggerFactory.getLogger(ConfigurationServiceImpl.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final int MAX_BACKUP_HISTORY = 10;

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
		changeListeners.clear();
	}

	public WebswingConfiguration getLiveConfiguration() {
		if (configFileLastModified != getConfigFileModificationTime()) {
			log.info("Webswing configuration file has been modified. Reloadinig active configuration. [" + getConfigFile().getAbsolutePath() + "]");
			reloadConfiguration();
		}
		return liveConfiguration;
	}

	public Map<String, SwingApplicationDescriptor> getApplications() {
		Map<String, SwingApplicationDescriptor> result = new HashMap<String, SwingApplicationDescriptor>();
		for (SwingApplicationDescriptor app : getLiveConfiguration().getApplications()) {
			result.put(app.getName(), app);
		}
		return result;
	}

	public Map<String, SwingAppletDescriptor> getApplets() {
		Map<String, SwingAppletDescriptor> result = new HashMap<String, SwingAppletDescriptor>();
		for (SwingAppletDescriptor app : getLiveConfiguration().getApplets()) {
			result.put(app.getName(), app);
		}
		return result;
	}

	public SwingApplicationDescriptor getApplication(String name) {
		for (SwingApplicationDescriptor app : getLiveConfiguration().getApplications()) {
			if (name != null && name.equals(app.getName())) {
				return app;
			}
		}
		return null;
	}

	public SwingAppletDescriptor getApplet(String name) {
		for (SwingAppletDescriptor app : getLiveConfiguration().getApplets()) {
			if (name != null && name.equals(app.getName())) {
				return app;
			}
		}
		return null;
	}

	public void reloadConfiguration() {
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

	public void saveApplicationConfiguration(WebswingConfiguration content) throws Exception {
		applyApplicationConfiguration(content);
		File config = getConfigFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(config, content);
		configFileLastModified = getConfigFileModificationTime();
	}

	public void registerChangeListener(ConfigurationChangeListener listener) {
		changeListeners.add(listener);
	}

	private void applyApplicationConfiguration(WebswingConfiguration content) throws Exception {
		if (content != null && !EqualsBuilder.reflectionEquals(liveConfiguration, content)) {
			validate(content);
			backupApplicationConfiguration(liveConfiguration);
			liveConfiguration = content;
			notifyChange();
		}
	}

	private void validate(WebswingConfiguration content) throws Exception {
		//validate unique path
		Map<String, SwingDescriptor> uniquePaths = new HashMap<String, SwingDescriptor>();
		uniquePaths.put("admin", null);
		for (SwingDescriptor sd : content.getApplets()) {
			verifyPath(sd, uniquePaths);
		}
		for (SwingDescriptor sd : content.getApplications()) {
			verifyPath(sd, uniquePaths);
		}

	}

	private void verifyPath(SwingDescriptor app, Map<String, SwingDescriptor> uniquePaths) throws Exception {
		String path = app.getPath();
		if (path == null || path.length() == 0) {
			throw new Exception("Configuration of '" + app.getName() + "' invalid. Path must not be empty.");
		}
		String currentPath = path;
		while (currentPath.length() > 0) {
			if (uniquePaths.containsKey(currentPath)) {
				SwingDescriptor desc = uniquePaths.get(currentPath);
				throw new Exception("Configuration of '" + app.getName() + "' invalid. Path '" + path + "' is in conflict with application '" + desc.getName() + "'s path.");
			}
			uniquePaths.put(currentPath, app);
			currentPath = currentPath.substring(0, currentPath.indexOf("/") < 1 ? 0 : currentPath.indexOf("/"));
		}
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
		return getConfigFile(false);
	}

	private File getConfigBackupFile() {
		return getConfigFile(true);
	}

	private File getConfigFile(boolean backup) {
		String configFile = System.getProperty(Constants.CONFIG_FILE_PATH);
		if (configFile == null) {
			String war = ServerUtil.getWarFileLocation();
			configFile = war.substring(0, war.lastIndexOf("/") + 1) + Constants.DEFAULT_CONFIG_FILE_NAME;
			System.setProperty(configFile, Constants.CONFIG_FILE_PATH);
		}
		configFile = backup ? configFile + ".backup" : configFile;
		File config = new File(URI.create(configFile));
		return config;
	}

	private void notifyChange() {
		for (ConfigurationChangeListener listener : changeListeners) {
			if (listener != null) {
				listener.notifyChange();
			}
		}
	}

	//TODO:REMOVE

	public void applyUserProperties(UserConfiguration content) throws Exception {
		File usersFile = new File(URI.create(ServerUtil.getUserPropsFileName()));
		FileUtils.writeByteArrayToFile(usersFile, content.getUsers().getBytes());
		notifyChange();
	}

	public UserConfiguration loadUserProperties() {
		try {
			String result = new String();
			File users = new File(URI.create(ServerUtil.getUserPropsFileName()));
			if (users.exists()) {
				result = FileUtils.readFileToString(users, Charset.forName("UTF-8"));
				return new UserConfiguration(result);
			} else {
				log.warn("User properties file " + users.getPath() + " does not exist.");
				return null;
			}
		} catch (Exception e) {
			log.error("Failed to load user properties file:", e);
			return null;
		}
	}

	public ServerProperties getServerProperties() {
		ServerProperties result = new ServerProperties();
		result.setTempFolder(System.getProperty(Constants.TEMP_DIR_PATH));
		result.setJmsServerUrl(System.getProperty(Constants.JMS_URL, Constants.JMS_URL_DEFAULT));
		result.setConfigFile(getConfigFile().toURI().toString());
		result.setWarLocation(ServerUtil.getWarFileLocation());
		result.setPort(System.getProperty(Constants.SERVER_PORT));
		result.setHost(System.getProperty(Constants.SERVER_HOST));
		result.setUserProps(ServerUtil.getUserPropsFileName());
		return result;
	}

	//TODO: END REMOVE
}
