package org.webswing.server.services.security;

import org.apache.shiro.authc.AuthenticationToken;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.otp.api.OneTimeToken;
import org.webswing.server.services.security.api.AbstractWebswingUser;

public class WebswingTokenAdapter implements AuthenticationToken {

	private static final long serialVersionUID = -6426250466722804937L;

	private static final WebswingCredentials emptyCredentials = new WebswingCredentials() {
	};

	private final WebswingPrincipal user;
	private final WebswingCredentials credentials;

	public WebswingTokenAdapter(String securityPath, AbstractWebswingUser user, WebswingCredentials credentials) {
		this.user = new WebswingPrincipal(securityPath, user);
		this.credentials = credentials;
	}

	public WebswingTokenAdapter(String securedPath, OneTimeToken token) {
		this.user = new WebswingPrincipal(securedPath, token.getUser());
		this.credentials= emptyCredentials;
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
