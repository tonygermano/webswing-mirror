package org.webswing.server.services.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingUser;

public class WebswingTokenAdapter implements AuthenticationToken, AuthenticationInfo {

	private static final long serialVersionUID = -6426250466722804937L;

	private final WebswingPrincipal user;
	private final WebswingCredentials credentials;

	private PrincipalCollection principalCollection;

	public WebswingTokenAdapter(String securityPath, WebswingUser user, WebswingCredentials credentials) {
		this.user = new WebswingPrincipal(securityPath, user);
		this.credentials = credentials;
	}

	@Override
	public Object getPrincipal() {
		return user;
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

	@Override
	public PrincipalCollection getPrincipals() {
		if (SecurityUtils.getSubject().getPrincipals() == null && credentials != null) {
			principalCollection = new SimplePrincipalCollection(user, WebswingRealmAdapter.WEBSWING_REALM);
		} else {
			List<WebswingPrincipal> principals = new ArrayList<WebswingPrincipal>(SecurityUtils.getSubject().getPrincipals().byType(WebswingPrincipal.class));
			for (Iterator<WebswingPrincipal> i = principals.iterator(); i.hasNext();) {
				WebswingPrincipal p = i.next();
				if (user.getSecuredPath().equals(p.getSecuredPath())) {
					i.remove();
				}
			}
			if (credentials != null) {
				principals.add(user);
			}
			principalCollection = new SimplePrincipalCollection(principals, WebswingRealmAdapter.WEBSWING_REALM);
		}
		return principalCollection;
	}

}
