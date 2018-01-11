package org.webswing.server.services.security;

import java.io.Serializable;

import org.apache.shiro.authz.Permission;

public class WebswingPermission implements Permission, Serializable {

	private static final long serialVersionUID = 8721141086374586400L;
	
	private final String securedPath;
	private final String permissionName;

	public WebswingPermission(String securedPath, String permissionName) {
		super();
		this.securedPath = securedPath;
		this.permissionName = permissionName;
	}

	public String getSecuredPath() {
		return securedPath;
	}

	public String getPermissionName() {
		return permissionName;
	}

	@Override
	public boolean implies(Permission p) {
		if (this instanceof WebswingPrincipal) {
			WebswingPrincipal principal = (WebswingPrincipal) p;
			return principal.getSecuredPath().equals(securedPath) && principal.isPermitted(permissionName);
		} else if (p instanceof WebswingPrincipal) {
			return p.implies(this);
		} else {
			return false;
		}
	}

}
