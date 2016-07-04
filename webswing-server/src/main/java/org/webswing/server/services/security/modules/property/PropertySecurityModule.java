package org.webswing.server.services.security.modules.property;

import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.text.PropertiesRealm;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.api.WebswingUser;
import org.webswing.server.services.security.modules.AbstractSecurityModule;
import org.webswing.server.util.ServerUtil;

public class PropertySecurityModule extends AbstractSecurityModule {

	private PropertiesRealm realm;

	public PropertySecurityModule(PropertySecurityModuleConfig config) {
		realm = new PropertiesRealm();
		StrSubstitutor configSubstitutor = ServerUtil.getConfigSubstitutor();
		String fileName = config.getFile();

		realm.setResourcePath(fileName == null ? "users.properties" : configSubstitutor.replace(fileName));
		realm.onInit();
	}

	@Override
	public WebswingUser verifyUserPassword(String user, String password) throws WebswingAuthenticationException {
		AuthenticationInfo authtInfo = realm.getAuthenticationInfo(new UsernamePasswordToken(user, password));
		if (authtInfo == null) {
			throw new WebswingAuthenticationException("User not found!");
		}
		return new ShiroWebswingUser(realm, authtInfo);
	}

}
