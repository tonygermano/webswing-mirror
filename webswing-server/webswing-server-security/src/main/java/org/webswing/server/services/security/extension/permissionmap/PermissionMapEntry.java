package org.webswing.server.services.security.extension.permissionmap;

import org.webswing.server.common.model.Config;
import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldOrder;

@ConfigFieldOrder({ "name", "permissions" })
public interface PermissionMapEntry extends Config {

	@ConfigField(label = "Name", description = "Id of user or role.")
	String getName();

	@ConfigField(label = "Permissions", description = "List of comma separated permissions.")
	String getPermissions();

}
