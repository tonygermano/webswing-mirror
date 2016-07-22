package org.webswing.server.services.security.otp.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.webswing.server.services.security.api.AbstractWebswingUser;

public class OtpWebswingUser extends AbstractWebswingUser {

	private String userId;
	private Set<String> roles;
	private Set<String> permissions;

	public OtpWebswingUser(String userId, Set<String> roles, Set<String> permissions) {
		this.userId = userId;
		this.roles = roles;
		this.permissions = permissions;
	}

	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public Map<String, Serializable> getUserAttributes() {
		return Collections.emptyMap();
	}

	@Override
	public boolean hasRole(String role) {
		return roles != null && roles.contains(role);
	}

	@Override
	public boolean isPermitted(String permission) {
		return super.isPermitted(permission) || (permissions != null && permissions.contains(permission));
	}
}
