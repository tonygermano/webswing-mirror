package org.webswing.server.services.security.extension.permissionmap;

import java.util.List;

import org.webswing.server.common.model.Config;
import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldOrder;

@ConfigFieldOrder({"users","roles"})
public interface PermissionMapEntry extends Config{

	@ConfigField(label="Users",description="List of Users having this permission.")
	List<String> getUsers();

	@ConfigField(label="Roles",description="List of Roles having this permission.")
	List<String> getRoles();

}
