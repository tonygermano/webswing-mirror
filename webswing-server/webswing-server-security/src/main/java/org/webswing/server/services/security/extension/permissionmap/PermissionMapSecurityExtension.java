package org.webswing.server.services.security.extension.permissionmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.extension.api.SecurityModuleExtension;
import org.webswing.server.services.security.extension.api.WebswingUserDecorator;

public class PermissionMapSecurityExtension extends SecurityModuleExtension<PermissionMapExtensionConfig> {

	private Map<String, List<String>> userPerm = new HashMap<>();//perm -> list of users
	private Map<String, List<String>> rolePerm = new HashMap<>();//perm -> list of roles

	public PermissionMapSecurityExtension(PermissionMapExtensionConfig config) {
		super(config);
		rolePerm.clear();
		for (PermissionMapEntry roleRule : getConfig().getRolePermissions()) {
			processRuleMapping(roleRule, rolePerm);
		}
		userPerm.clear();
		for (PermissionMapEntry userRule : getConfig().getUserPermissions()) {
			processRuleMapping(userRule, userPerm);
		}
	}

	private void processRuleMapping(PermissionMapEntry entry, Map<String, List<String>> map) {
		if (entry.getPermissions() != null) {
			for (String permission : entry.getPermissions().split(",")) {
				permission = permission.trim();
				if (!map.containsKey(permission)) {
					map.put(permission, new ArrayList<>());
				}
				map.get(permission).add(entry.getName());
			}
		}
	}

	@Override
	public AbstractWebswingUser decorateUser(AbstractWebswingUser user, HttpServletRequest request, HttpServletResponse response) {
		return new WebswingUserDecorator(user) {
			@Override
			public boolean isPermitted(String permission) {
				if (userPerm.get(permission) != null && userPerm.get(permission).contains(user.getUserId())) {
					return true;
				}
				if (rolePerm.get(permission) != null) {
					for (String role : rolePerm.get(permission)) {
						if (user.hasRole(role)) {
							return true;
						}
					}
				}
				return super.isPermitted(permission);
			}
		};
	}
}
