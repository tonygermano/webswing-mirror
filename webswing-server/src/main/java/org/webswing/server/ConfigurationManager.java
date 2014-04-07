package org.webswing.server;

import java.io.File;
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
import org.webswing.server.model.SwingApplicationDescriptor;
import org.webswing.server.model.WebswingConfiguration;
import org.webswing.server.model.WebswingConfigurationBackup;

public class ConfigurationManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationManager.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final int MAX_BACKUP_HISTORY = 10;
    private static ConfigurationManager instance = new ConfigurationManager();

    private WebswingConfiguration liveConfiguration = new WebswingConfiguration();

    private ConfigurationManager() {
        reloadConfiguration();
    }

    public WebswingConfiguration getLiveConfiguration() {
        return liveConfiguration;
    }

    public Map<String, SwingApplicationDescriptor> getApplications() {
        Map<String, SwingApplicationDescriptor> result = new HashMap<String, SwingApplicationDescriptor>();
        for (SwingApplicationDescriptor app : liveConfiguration.getApplications()) {
            result.put(app.getName(), app);
        }
        return result;
    }

    public SwingApplicationDescriptor getApplication(String name) {
        for(SwingApplicationDescriptor app: liveConfiguration.getApplications()){
            if(name!=null && name.equals(app.getName())){
                return app;
            }
        }
        return null;
    }

    public void reloadConfiguration() {
        try {
            WebswingConfiguration loaded = ConfigurationManager.loadApplicationConfiguration();
            if (loaded != null) {
                liveConfiguration = loaded;
            }
        } catch (Exception e) {
            log.error("Webswing application configuration failed to load:", e);
        }
    }

    public void saveApplicationConfiguration(WebswingConfiguration configuration) throws Exception {
        if (configuration != null &&  !EqualsBuilder.reflectionEquals(liveConfiguration, configuration)) {
            backupApplicationConfiguration(liveConfiguration);
            File config = getConfigFile();
            mapper.writerWithDefaultPrettyPrinter().writeValue(config, configuration);
        }
    }

    public static void backupApplicationConfiguration(WebswingConfiguration configuration) throws Exception {
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

    public static WebswingConfigurationBackup loadApplicationConfigurationBackup() throws Exception {
        WebswingConfigurationBackup result = new WebswingConfigurationBackup();
        File backup = getConfigBackupFile();
        if (backup.exists()) {
            result = mapper.readValue(backup, WebswingConfigurationBackup.class);
            return result;
        } else {
            log.error("Configuration backup file " + backup.getPath() + " does not exist!");
            return null;
        }
    }

    public static WebswingConfiguration loadApplicationConfiguration() throws Exception {
        WebswingConfiguration result = new WebswingConfiguration();
        File config = getConfigFile();
        if (config.exists()) {
            result = mapper.readValue(config, WebswingConfiguration.class);
            return result;
        } else {
            log.error("Configuration file " + config.getPath() + " does not exist!");
            return null;
        }
    }

    private static File getConfigFile() {
        return getConfigFile(false);
    }

    private static File getConfigBackupFile() {
        return getConfigFile(true);
    }

    private static File getConfigFile(boolean backup) {
        String configFile = System.getProperty(Constants.CONFIG_FILE_PATH);
        if (configFile == null) {
            String war = System.getProperty(Constants.WAR_FILE_LOCATION);
            configFile = war.substring(6, war.lastIndexOf("/") + 1) + Constants.DEFAULT_CONFIG_FILE_NAME;
            System.setProperty(configFile, Constants.CONFIG_FILE_PATH);
        }
        configFile = backup ? configFile + ".backup" : configFile;
        File config = new File(configFile);
        return config;
    }

    public static ConfigurationManager getInsatnce() {
        return instance;
    }
}
