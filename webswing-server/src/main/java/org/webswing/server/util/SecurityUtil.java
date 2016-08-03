package org.webswing.server.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.security.WebswingPrincipal;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.websocket.WebSocketConnection;

public class SecurityUtil {

	public static AbstractWebswingUser getUser(UrlHandler urlHandler) {
		Subject subject = SecurityUtils.getSubject();
		return resolveUser(subject, urlHandler);
	}

	public static AbstractWebswingUser getUser(WebSocketConnection connection) {
		Subject subject = SecurityUtils.getSubject();
		UrlHandler urlHandler = connection.getHandler();
		return resolveUser(subject, urlHandler);
	}

	private static AbstractWebswingUser resolveUser(Subject subject, UrlHandler urlHandler) {
		if (subject != null && urlHandler != null) {
			PrincipalCollection currentPrincipals = subject.getPrincipals();
			String securedParentPath = urlHandler.getSecuredPath();
			if (currentPrincipals != null && subject.isAuthenticated()) {
				for (WebswingPrincipal webswingPrincipal : currentPrincipals.byType(WebswingPrincipal.class)) {
					if (securedParentPath.equals(webswingPrincipal.getSecuredPath())) {
						return webswingPrincipal.getUser();
					}
				}
			}
		}
		return AbstractWebswingUser.anonymUser;
	}

	public static Object getFromSecuritySession(String attributeName) {
		Subject subject = SecurityUtils.getSubject();
		return subject.getSession().getAttribute(attributeName);
	}

	public static void setToSecuritySession(String attributeName, Object value) {
		Subject subject = SecurityUtils.getSubject();
		subject.getSession().setAttribute(attributeName, value);
	}

}
