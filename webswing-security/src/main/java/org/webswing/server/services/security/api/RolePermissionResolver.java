package org.webswing.server.services.security.api;

public interface RolePermissionResolver {
	String[] getRolesForPermission(String action);
}
