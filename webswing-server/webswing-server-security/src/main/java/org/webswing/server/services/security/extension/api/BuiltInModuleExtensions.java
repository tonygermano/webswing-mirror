package org.webswing.server.services.security.extension.api;

import org.webswing.server.services.security.extension.onetimeurl.OneTimeUrlSecurityExtension;
import org.webswing.server.services.security.extension.permissionmap.PermissionMapSecurityExtension;

public enum BuiltInModuleExtensions {
	oneTimeUrl(OneTimeUrlSecurityExtension.class.getName()),
	permissionMap(PermissionMapSecurityExtension.class.getName());

	private String className;

	private BuiltInModuleExtensions(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public static String getExtensionClassName(String name) {
		try {
			BuiltInModuleExtensions builtInExtensions = BuiltInModuleExtensions.valueOf(name);
			return builtInExtensions.getClassName();
		} catch (Exception e) {
			return name;
		}
	}
}