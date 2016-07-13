package org.webswing.server.services.security.modules.saml2;

import org.webswing.server.services.security.api.WebswingCredentials;

public class Saml2Credentials implements WebswingCredentials {

	private String samlToken = null;
	private String name = null;

	public Saml2Credentials(String samlToken, String name) {
		this.samlToken = samlToken;
		this.name = name;
	}

	public Object getCredentials() {
		return samlToken;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
