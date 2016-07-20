package org.webswing.server.services.security.modules.anonym;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingSecurityModule;

public class AnonymSecurityModule implements WebswingSecurityModule<WebswingCredentials> {

	public AnonymSecurityModule(Map<String, Object> config) {
	}

	@Override
	public void init() {
	}

	@Override
	public WebswingCredentials getCredentials(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException e) {
		return new WebswingCredentials() {

		};
	}

	@Override
	public AbstractWebswingUser getUser(WebswingCredentials token) throws WebswingAuthenticationException {
		return new AbstractWebswingUser.AnonymWebswingUser() {
			@Override
			public boolean isPermitted(String permission) {
				return true;
			}
		};
	}

	@Override
	public void destroy() {
	}

}
