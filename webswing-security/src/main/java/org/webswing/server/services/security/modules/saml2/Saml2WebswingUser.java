package org.webswing.server.services.security.modules.saml2;

import java.io.Serializable;
import java.util.Map;

import org.webswing.server.services.security.api.AbstractWebswingUser;

public class Saml2WebswingUser extends AbstractWebswingUser{

	private static final long serialVersionUID = -5236694270082203054L;
	
	private Saml2Credentials creds;

	public Saml2WebswingUser(Saml2Credentials creds) {
		this.creds = creds;
	}
	
	@Override
	public String getUserId() {
		return creds.getName();
	}

	@Override
	public Map<String, Serializable> getUserAttributes() {
		return creds.getAttributes();
	}

	@Override
	public boolean hasRole(String role) {
		return true;
	}

}
