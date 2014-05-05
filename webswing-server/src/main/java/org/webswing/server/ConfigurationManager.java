package org.webswing.server;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.admin.s2c.JsonServerProperties;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.model.server.WebswingConfiguration;
import org.webswing.model.server.WebswingConfigurationBackup;
import org.webswing.server.util.ServerUtil;

import com.google.common.io.Files;

public class ConfigurationManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationManager.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final int MAX_BACKUP_HISTORY = 10;
    private static ConfigurationManager instance = new ConfigurationManager();

    private WebswingConfiguration liveConfiguration = new WebswingConfiguration();
    private List<ConfigurationChangeListener> changeListeners= new ArrayList<ConfigurationChangeListener>();

    private ConfigurationManager() {
        reloadConfiguration();
    }

    public WebswingConfiguration getLiveConfiguration() {
        return liveConfiguration;
    }

    public JsonServerProperties getServerProperties() {
        JsonServerProperties result = new JsonServerProperties();
        result.setTempFolder(System.getProperty(Constants.TEMP_DIR_PATH));
        result.setJmsServerUrl(Constants.JMS_URL);
        result.setConfigFile(getConfigFile().getAbsolutePath());
        result.setWarLocation(ServerUtil.getWarFileLocation());
        result.setPort(System.getProperty(Constants.SERVER_PORT));
        result.setHost(System.getProperty(Constants.SERVER_HOST));
        result.setUserProps(ServerUtil.getUserPropsFileName());
        return result;
    }

    public Map<String, SwingApplicationDescriptor> getApplications() {
        Map<String, SwingApplicationDescriptor> result = new HashMap<String, SwingApplicationDescriptor>();
        for (SwingApplicationDescriptor app : liveConfiguration.getApplications()) {
            result.put(app.getName(), app);
        }
        return result;
    }

    public SwingApplicationDescriptor getApplication(String name) {
        for (SwingApplicationDescriptor app : liveConfiguration.getApplications()) {
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
                liveConfiguration = loaded;
                notifyChange();
            }
        } catch (Exception e) {
            log.error("Webswing application configuration failed to load:", e);
        }
    }

    public void applyApplicationConfiguration(byte[] content) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        WebswingConfiguration configuration = mapper.readValue(content, WebswingConfiguration.class);
        if (configuration != null && !EqualsBuilder.reflectionEquals(liveConfiguration, configuration)) {
            backupApplicationConfiguration(liveConfiguration);
            File config = getConfigFile();
            mapper.writerWithDefaultPrettyPrinter().writeValue(config, configuration);
            reloadConfiguration();
            notifyChange();
        }
    }

    public void applyUserProperties(byte[] content) throws Exception {
        File usersFile = new File(ServerUtil.getUserPropsFileName());
        Files.write(content, usersFile);

        notifyChange();
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
                //removeLast
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

    public WebswingConfigurationBackup loadApplicationConfigurationBackup() {
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

    public String loadUserProperties() {
        try {
            String result = new String();
            File users = new File(ServerUtil.getUserPropsFileName());
            if (users.exists()) {
                result = Files.toString(users, Charset.forName("UTF-8"));
                return result;
            } else {
                log.warn("User properties file " + users.getPath() + " does not exist.");
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to load user properties file:", e);
            return null;
        }
    }

    public WebswingConfiguration loadApplicationConfiguration() {
        try {
            WebswingConfiguration result = new WebswingConfiguration();
            File config = getConfigFile();
            if (config.exists()) {
                result = mapper.readValue(config, WebswingConfiguration.class);
                return result;
            } else {
                log.error("Configuration file " + config.getPath() + " does not exist!");
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to load configuration file:", e);
            return null;
        }
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
            configFile = war.substring(5, war.lastIndexOf("/") + 1) + Constants.DEFAULT_CONFIG_FILE_NAME;
            System.setProperty(configFile, Constants.CONFIG_FILE_PATH);
        }
        configFile = backup ? configFile + ".backup" : configFile;
        File config = new File(configFile);
        return config;
    }

    public static ConfigurationManager getInstance() {
        return instance;
    }

    public void registerListener(ConfigurationChangeListener listener) {
        this.changeListeners.add(listener);
    }

    private void notifyChange() {
        for (ConfigurationChangeListener listener : changeListeners) {
            if (listener != null) {
                listener.notifyChange();
            }
        }
    }

    public interface ConfigurationChangeListener {

        void notifyChange();
    }

}
