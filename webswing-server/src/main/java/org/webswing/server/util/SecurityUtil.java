package org.webswing.server.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.security.WebswingPrincipal;
import org.webswing.server.services.security.api.WebswingUser;
import org.webswing.server.services.websocket.WebSocketConnection;

public class SecurityUtil {

	public static WebswingUser getUser(UrlHandler urlHandler) {
		Subject subject = SecurityUtils.getSubject();
		return resolveUser(subject, urlHandler);
	}

	public static WebswingUser getUser(WebSocketConnection connection) {
		Subject subject = (Subject) connection.getSecuritySubject();
		UrlHandler urlHandler = connection.getHandler();
		return resolveUser(subject, urlHandler);
	}

	private static WebswingUser resolveUser(Subject subject, UrlHandler urlHandler) {
		PrincipalCollection currentPrincipals = subject.getPrincipals();
		String securedParentPath = urlHandler.getSecuredPath();
		if (currentPrincipals != null && subject.isAuthenticated()) {
			for (WebswingPrincipal webswingPrincipal : currentPrincipals.byType(WebswingPrincipal.class)) {
				if (securedParentPath.equals(webswingPrincipal.getSecuredPath())) {
					return webswingPrincipal.getUser();
				}
			}
		}
		return WebswingUser.anonym;
	}

}
