package org.webswing.server.services.security.login;

import org.webswing.server.base.UrlHandler;

public interface LoginHandlerService {

	LoginHandler createLoginHandler(UrlHandler parent, WebswingSecurityProvider config);

	LogoutHandler createLogoutHandler(UrlHandler parent);

}
