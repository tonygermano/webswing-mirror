package org.webswing.server.services.security.modules.property;

import java.util.Collections;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.text.PropertiesRealm;
import org.webswing.server.services.security.api.WebswingUser;

public class ShiroWebswingUser extends WebswingUser {

	private AuthorizingRealm authzrealm;
	private AuthenticationInfo authtInfo;

	public ShiroWebswingUser(PropertiesRealm authzrealm, AuthenticationInfo authtInfo) {
		super();
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
	protected boolean hasRole(String role) {
		return authzrealm.hasRole(this.authtInfo.getPrincipals(), role);
	}

}