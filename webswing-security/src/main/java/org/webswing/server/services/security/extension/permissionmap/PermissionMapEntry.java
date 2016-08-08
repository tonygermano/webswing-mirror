package org.webswing.server.services.security.extension.permissionmap;

import java.util.List;

public interface PermissionMapEntry {

	List<String> getUsers();

	List<String> getRoles();

}
