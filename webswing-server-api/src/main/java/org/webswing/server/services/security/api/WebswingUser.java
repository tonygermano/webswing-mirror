package org.webswing.server.services.security.api;

import java.util.Collections;
import java.util.Map;

public abstract class WebswingUser {
	public static final String anonymUserName = "anonym";
	public static final String authenticated = "authenticated";
	public static AnonymWebswingUser anonymUser = new AnonymWebswingUser();

	private RolePermissionResolver resolver;

	public WebswingUser() {
		this(new WebswingAction.DefaultRolePermissionResolver());
	}

	public WebswingUser(RolePermissionResolver resolver) {
		this.resolver = resolver;
	}

	public abstract String getUserId();

	public abstract Map<String, String> getUserAttributes();

	protected abstract boolean hasRole(String role);

	public boolean isPermitted(WebswingAction permission) {
		if (resolver != null) {
			String[] roles = resolver.getRolesForPermission(permission);
			if (roles != null) {
				for (String role : roles) {
					if (anonymUserName.equals(role) || (authenticated.equals(role) && isAuthenticated()) || hasRole(role))
						return true;
				}
			}
		}
		return false;
	}

	boolean isAuthenticated() {
		return true;
	}

	@Override
	public String toString() {
		return getUserId();
	}

	public static class AnonymWebswingUser extends WebswingUser {

		@Override
		public String getUserId() {
			return anonymUserName;
		}

		@Override
		public Map<String, String> getUserAttributes() {
			return Collections.emptyMap();
		}

		@Override
		public boolean hasRole(String role) {
			return false;
		}

		boolean isAuthenticated() {
			return false;
		}
	};
}
