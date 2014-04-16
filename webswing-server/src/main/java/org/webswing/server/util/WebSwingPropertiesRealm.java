package org.webswing.server.util;

import org.apache.shiro.realm.text.PropertiesRealm;

public class WebSwingPropertiesRealm extends PropertiesRealm {

    public WebSwingPropertiesRealm() {
        super();
        String userFile = ServerUtil.getUserPropsFileName();
        setResourcePath(userFile);
    }
}
