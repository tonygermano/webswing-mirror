package org.webswing.server.services.security;

import org.apache.shiro.authc.AuthenticationToken;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingUser;

public class WebswingTokenAdapter implements AuthenticationToken {

	private static final long serialVersionUID = -6426250466722804937L;

	private final WebswingPrincipal user;
	private final WebswingCredentials credentials;

	public WebswingTokenAdapter(String securityPath, WebswingUser user, WebswingCredentials credentials) {
		this.user = new WebswingPrincipal(securityPath, user);
		this.credentials = credentials;
	}

	@Override
	public Object getPrincipal() {
		return user;
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

}
