package org.webswing.server.services.security.api;

import java.io.Serializable;

/**
 * Provides a logic to map Roles to Permissions for {@link AbstractWebswingUser#isPermitted(String)}'s
 * default implementation. 
 */
public interface RolePermissionResolver extends Serializable{
	/**
	 * @param permission permission name
	 * @return All roles the permission is allowed for
	 */
	String[] getRolesForPermission(String permission);
}
