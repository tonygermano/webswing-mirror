package org.webswing.server.services.security.login;

import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.security.SecurityManagerService;

import com.google.inject.Inject;
import com.google.inject.Singleton;
@Singleton
public class LoginHandlerServiceImpl implements LoginHandlerService {

	private final SecurityManagerService securityManger;

	@Inject
	public LoginHandlerServiceImpl(SecurityManagerService securityManger) {
		this.securityManger = securityManger;
	}

	public LoginHandler createLoginHandler(UrlHandler parent, WebswingSecurityProvider config) {
		return new LoginHandlerImpl(parent, securityManger, config);
	}
	
	public LogoutHandler createLogoutHandler(UrlHandler parent) {
		return new LogoutHandlerImpl(parent);
	}
}
