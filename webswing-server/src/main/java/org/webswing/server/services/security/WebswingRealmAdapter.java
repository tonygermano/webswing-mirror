package org.webswing.server.services.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class WebswingRealmAdapter extends AuthorizingRealm {

	public static final String WEBSWING_REALM = "webswingRealm";

	@Override
	public String getName() {
		return WEBSWING_REALM;
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		if (token instanceof WebswingTokenAdapter) {
			return true;
		}
		return false;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		if (token instanceof WebswingTokenAdapter) {
			return (WebswingTokenAdapter) token;
		}
		return null;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
		SimpleAuthorizationInfo sai = new SimpleAuthorizationInfo();
		for (WebswingPrincipal p : principals.byType(WebswingPrincipal.class)) {
			sai.addObjectPermission(p);
		}
		return sai;
	}

}
