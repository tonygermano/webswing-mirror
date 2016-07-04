package org.webswing.server.services.security.modules.property;

import java.util.Collections;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.text.PropertiesRealm;
import org.webswing.server.services.security.api.WebswingUser;

public class ShiroWebswingUser implements WebswingUser {

	private AuthorizingRealm authzrealm;
	private AuthenticationInfo authtInfo;

	public ShiroWebswingUser(PropertiesRealm authzrealm, AuthenticationInfo authtInfo) {
		this.authzrealm = authzrealm;
		this.authtInfo = authtInfo;
	}

	@Override
	public String getUserId() {
		return (String) this.authtInfo.getPrincipals().getPrimaryPrincipal();
	}

	@Override
	public Map<String, String> getUserAttributes() {
		return Collections.emptyMap();
	}

	@Override
	public boolean isPermitted(String permission) {
		return authzrealm.isPermitted(authtInfo.getPrincipals(), permission);
	}

}