package org.webswing.server.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.security.SecurityManagerService;
import org.webswing.server.services.security.WebswingPrincipal;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.websocket.WebSocketConnection;

public class SecurityUtil {
	private static final Logger log = LoggerFactory.getLogger(SecurityUtil.class);

	public static AbstractWebswingUser getUser(UrlHandler urlHandler) {
		Subject subject = SecurityUtils.getSubject();
		return resolveUser(subject, urlHandler);
	}

	public static AbstractWebswingUser getUser(WebSocketConnection connection) {
		Subject subject = (Subject) connection.getRequest().getAttribute(SecurityManagerService.SECURITY_SUBJECT);
		UrlHandler urlHandler = connection.getHandler();
		return resolveUser(subject, urlHandler);
	}

	private static AbstractWebswingUser resolveUser(Subject subject, UrlHandler handler) {
		String securedPath = handler.getSecuredPath();
		if (subject != null && securedPath != null) {
			try {
				PrincipalCollection currentPrincipals = subject.getPrincipals();
				if (currentPrincipals != null && subject.isAuthenticated()) {
					AbstractWebswingUser masteradmin = null;
					for (WebswingPrincipal webswingPrincipal : currentPrincipals.byType(WebswingPrincipal.class)) {
						if (handler.getRootHandler().getSecuredPath().equals(webswingPrincipal.getSecuredPath()) && webswingPrincipal.isPermitted(WebswingAction.master_admin_access.name())) {
							masteradmin = webswingPrincipal.getUser();
						}
						if (securedPath.equals(webswingPrincipal.getSecuredPath())) {
							return webswingPrincipal.getUser();
						}
					}
					return masteradmin;
				}
			} catch (UnknownSessionException e) {
				log.info("User already logged out.", e);
				return null;

			}
		}
		return null;
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
