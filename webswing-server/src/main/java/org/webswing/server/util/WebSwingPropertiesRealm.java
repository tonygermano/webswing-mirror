package org.webswing.server.util;

import java.io.File;
import java.net.URI;

import org.apache.shiro.realm.text.PropertiesRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.ConfigurationManager;
import org.webswing.server.ConfigurationManager.ConfigurationChangeListener;

public class WebSwingPropertiesRealm extends PropertiesRealm implements ConfigurationChangeListener {
    private static final Logger log = LoggerFactory.getLogger(WebSwingPropertiesRealm.class);

    public WebSwingPropertiesRealm() {
        super();
        String userFile = ServerUtil.getUserPropsFileName();
        File f = new File(URI.create(userFile));
        if (f.exists()) {
            setResourcePath(f.getAbsolutePath());
            ConfigurationManager.getInstance().registerListener(this);
        }else{
            log.error("Users configuration file "+ServerUtil.getUserPropsFileName()+" does not exist.");
        }
    }

    @Override
    public void notifyChange() {
        run();
    }

}
