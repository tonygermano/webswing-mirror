package org.webswing.server.services.security.extension.permissionmap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.extension.api.SecurityModuleExtension;
import org.webswing.server.services.security.extension.api.WebswingUserDecorator;

public class PermissionMapSecurityExtension extends SecurityModuleExtension<PermissionMapExtensionConfig> {

	public PermissionMapSecurityExtension(PermissionMapExtensionConfig config) {
		super(config);
	}

	@Override
	public AbstractWebswingUser decorateUser(AbstractWebswingUser user, HttpServletRequest request, HttpServletResponse response) {
		return new WebswingUserDecorator(user) {
			@Override
			public boolean isPermitted(String permission) {
				if (getConfig().getPermissions() != null && getConfig().getPermissions().containsKey(permission)) {
					PermissionMapEntry permissionMapEntry = getConfig().getPermissions().get(permission);
					if (permissionMapEntry.getUsers() != null) {
						return permissionMapEntry.getUsers().contains(getUserId());
					}
					if (permissionMapEntry.getRoles() != null) {
						for (String role : permissionMapEntry.getRoles()) {
							if (hasRole(role)) {
								return true;
							}
						}
					}
					return false;
				} else {
					return super.isPermitted(permission);
				}
			}
		};
	}
}
