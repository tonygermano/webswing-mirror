package org.webswing.server.services.security.modules.saml2;

import java.util.Collections;
import java.util.Map;

import org.webswing.server.services.security.api.WebswingUser;

public class Saml2WebswingUser extends WebswingUser{

	private Saml2Credentials creds;

	public Saml2WebswingUser(Saml2Credentials creds) {
		this.creds = creds;
	}
	
	@Override
	public String getUserId() {
		return creds.getName();
	}

	@Override
	public Map<String, String> getUserAttributes() {
		return Collections.emptyMap();
	}

	@Override
	protected boolean hasRole(String role) {
		return true;
	}

}
