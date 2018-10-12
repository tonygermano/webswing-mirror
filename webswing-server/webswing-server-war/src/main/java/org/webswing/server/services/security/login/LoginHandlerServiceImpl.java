package org.webswing.server.services.security.login;

import org.webswing.server.base.UrlHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.webswing.server.services.websocket.WebSocketService;

@Singleton
public class LoginHandlerServiceImpl implements LoginHandlerService {

	private final WebSocketService webSockets;

	@Inject
	public LoginHandlerServiceImpl(WebSocketService webSockets) {
		this.webSockets = webSockets;
	}

	public LoginHandler createLoginHandler(UrlHandler parent) {
		return new LoginHandlerImpl(parent);
	}

	public LogoutHandler createLogoutHandler(UrlHandler parent) {
		return new LogoutHandlerImpl(webSockets, parent);
	}
}
