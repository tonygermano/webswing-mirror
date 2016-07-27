package org.webswing.server.services.security.modules.anonym;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingSecurityModuleConfig;
import org.webswing.server.services.security.modules.AbstractSecurityModule;

public class AnonymSecurityModule extends AbstractSecurityModule<WebswingSecurityModuleConfig> {

	public AnonymSecurityModule(WebswingSecurityModuleConfig config) {
		super(config);
	}

	@Override
	public AbstractWebswingUser getUser(HttpServletRequest request, HttpServletResponse response) {
		return new AbstractWebswingUser.AnonymWebswingUser() {
			@Override
			public boolean isPermitted(String permission) {
				return true;
			}
		};
	}

}
