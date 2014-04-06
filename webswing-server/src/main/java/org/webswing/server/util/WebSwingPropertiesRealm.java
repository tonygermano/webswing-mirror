package org.webswing.server.util;

import org.apache.shiro.realm.text.PropertiesRealm;
import org.webswing.Constants;

public class WebSwingPropertiesRealm extends PropertiesRealm {

    public WebSwingPropertiesRealm() {
        super();
        String userFile = System.getProperty(Constants.USER_FILE_PATH);
        if (userFile == null) {
            String war = System.getProperty(Constants.WAR_FILE_LOCATION);
            userFile = war.substring(6, war.lastIndexOf("/") + 1) + Constants.DEFAULT_USER_FILE_NAME;
            System.setProperty(userFile, Constants.USER_FILE_PATH);
        }
        setResourcePath(userFile);
    }
}
