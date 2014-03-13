package org.webswing.server.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.server.model.SwingApplicationDescriptor;

public class ServerUtil {
    private static final Logger log = LoggerFactory.getLogger(ServerUtil.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, SwingApplicationDescriptor> loadApplicationConfiguration() throws Exception{
        Map<String, SwingApplicationDescriptor> result = new HashMap<String, SwingApplicationDescriptor>();
        String configFile = System.getProperty(Constants.CONFIG_FILE_PATH);
        if (configFile == null) {
            String war = System.getProperty(Constants.WAR_FILE_LOCATION);
            configFile = war.substring(6, war.lastIndexOf("/")+1) + Constants.DEFAULT_CONFIG_FILE_NAME;
            System.setProperty(configFile, Constants.CONFIG_FILE_PATH);
        }
        File config = new File(configFile);
        if (config.exists()) {
            result=mapper.readValue(config, new TypeReference<Map<String, SwingApplicationDescriptor>>() {});
            return result;
        }else{
            log.error("Configuration file "+configFile+ " does not exist!");
            return null; 
        }
    }
}
