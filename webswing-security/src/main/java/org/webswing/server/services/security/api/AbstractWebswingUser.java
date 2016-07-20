package org.webswing.server.services.security.api;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import org.webswing.toolkit.api.security.WebswingUser;

public abstract class AbstractWebswingUser implements WebswingUser {
	public static final String anonymUserName = "anonym";
	public static final String authenticated = "authenticated";
	public static AnonymWebswingUser anonymUser = new AnonymWebswingUser();

	private RolePermissionResolver resolver;

	public AbstractWebswingUser() {
		this(new WebswingAction.DefaultRolePermissionResolver());
	}

	public AbstractWebswingUser(RolePermissionResolver resolver) {
		this.resolver = resolver;
	}

	public abstract String getUserId();

	public abstract Map<String, Serializable> getUserAttributes();

	public abstract boolean hasRole(String role);

	public boolean isPermitted(String permission) {
		if (resolver != null) {
			String[] roles = resolver.getRolesForPermission(permission);
			if (roles != null) {
				for (String role : roles) {
					if (anonymUserName.equals(role)) {
						return true;
					} else if (isAuthenticated() && (authenticated.equals(role) || hasRole(role))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public String toString() {
		return getUserId();
	}

	public static class AnonymWebswingUser extends AbstractWebswingUser {

		@Override
		public String getUserId() {
			return anonymUserName;
		}

		@Override
		public Map<String, Serializable> getUserAttributes() {
			return Collections.emptyMap();
		}

		@Override
		public boolean hasRole(String role) {
			return false;
		}

		public boolean isAuthenticated() {
			return false;
		}
	};
}
