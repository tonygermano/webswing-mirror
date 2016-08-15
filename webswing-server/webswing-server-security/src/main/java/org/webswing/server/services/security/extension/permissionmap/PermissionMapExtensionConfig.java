package org.webswing.server.services.security.extension.permissionmap;

import java.util.Map;

import org.webswing.server.services.security.extension.api.SecurityModuleExtensionConfig;

public interface PermissionMapExtensionConfig extends SecurityModuleExtensionConfig {
	Map<String, PermissionMapEntry> getPermissions();

}
