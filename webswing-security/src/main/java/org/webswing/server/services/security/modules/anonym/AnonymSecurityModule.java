package org.webswing.server.services.security.modules.anonym;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingSecurityModule;

public class AnonymSecurityModule implements WebswingSecurityModule {

	@Override
	public AbstractWebswingUser doLogin(HttpServletRequest request, HttpServletResponse response) {
		return new AbstractWebswingUser.AnonymWebswingUser() {
			@Override
			public boolean isPermitted(String permission) {
				return true;
			}
		};
	}

	@Override
	public void init() {
	}

	@Override
	public void destroy() {
	}

}
