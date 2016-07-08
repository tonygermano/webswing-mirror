package org.webswing.server.services.security.api;

public interface RolePermissionResolver {
	String[] getRolesForPermission(WebswingAction action);
}
