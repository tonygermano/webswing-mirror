package org.webswing.server.services.security.modules.property;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.text.PropertiesRealm;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.modules.AbstractUserPasswordSecurityModule;

public class PropertySecurityModule extends AbstractUserPasswordSecurityModule<PropertySecurityModuleConfig> {

	private PropertiesRealm realm;

	public PropertySecurityModule(PropertySecurityModuleConfig config) {
		super(config);
		realm = new PropertiesRealm();
		String fileName = getConfig().getFile();
		realm.setResourcePath(getConfig().getContext().replaceVariables(fileName));
		realm.onInit();
	}

	@Override
	public AbstractWebswingUser verifyUserPassword(String user, String password) throws WebswingAuthenticationException {
		AuthenticationInfo authtInfo;
		try {
			authtInfo = realm.getAuthenticationInfo(new UsernamePasswordToken(user, password));
		} catch (AuthenticationException e) {
			throw new WebswingAuthenticationException("Username or password is not valid!", e);
		}
		if (authtInfo == null) {
			throw new WebswingAuthenticationException("User not found!");
		}
		return new ShiroWebswingUser(realm, authtInfo);
	}

}
