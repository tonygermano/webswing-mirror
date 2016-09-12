package org.webswing.server.services.security;

import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

public class WebswingWebSessionManager extends DefaultWebSessionManager {

	private static final String WEBSWING_SESSION_ID = "WebswingSessionId";

	public WebswingWebSessionManager() {
		Cookie cookie = new SimpleCookie(WEBSWING_SESSION_ID);
		cookie.setHttpOnly(true); //more secure, protects against XSS attacks
		setSessionIdCookie(cookie);
	}
}
