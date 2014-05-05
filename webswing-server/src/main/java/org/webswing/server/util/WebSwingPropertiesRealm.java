package org.webswing.server.util;

import java.io.File;

import org.apache.shiro.realm.text.PropertiesRealm;
import org.webswing.server.ConfigurationManager;
import org.webswing.server.ConfigurationManager.ConfigurationChangeListener;

public class WebSwingPropertiesRealm extends PropertiesRealm implements ConfigurationChangeListener {

    public WebSwingPropertiesRealm() {
        super();
        String userFile = ServerUtil.getUserPropsFileName();
        File f = new File(userFile);
        if (f.exists()) {
            setResourcePath(f.toURI().toString());
            ConfigurationManager.getInstance().registerListener(this);
        }
    }

    @Override
    public void notifyChange() {
        run();
    }

}
