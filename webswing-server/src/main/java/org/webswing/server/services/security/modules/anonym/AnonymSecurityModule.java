package org.webswing.server.services.security.modules.anonym;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.api.WebswingUser;

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
	public WebswingUser getUser(WebswingCredentials token) throws WebswingAuthenticationException {
		return new WebswingUser.AnonymWebswingUser() {
			@Override
			public boolean isPermitted(WebswingAction permission) {
				return true;
			}
		};
	}

	@Override
	public void destroy() {
	}

}
