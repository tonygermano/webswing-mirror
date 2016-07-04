package org.webswing.server.services.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.security.api.WebswingUser;

public class SecurityUtil {

	public static WebswingUser getUser(UrlHandler urlHandler) {
		Subject subject = SecurityUtils.getSubject();
		PrincipalCollection currentPrincipals = subject.getPrincipals();
		String securedParentPath = urlHandler.getSecuredPath();
		if (currentPrincipals != null && subject.isAuthenticated()) {
			for (WebswingPrincipal webswingPrincipal : currentPrincipals.byType(WebswingPrincipal.class)) {
				if (securedParentPath.equals(webswingPrincipal.getSecuredPath())) {
					return webswingPrincipal.getUser();
				}
			}
		}
		return null;
	}

}
