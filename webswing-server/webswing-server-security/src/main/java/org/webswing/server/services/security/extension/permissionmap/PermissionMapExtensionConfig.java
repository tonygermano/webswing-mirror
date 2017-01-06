package org.webswing.server.services.security.extension.permissionmap;

import java.util.ArrayList;
import java.util.List;

import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueObject;
import org.webswing.server.common.model.meta.ConfigFieldEditorType;
import org.webswing.server.common.model.meta.ConfigFieldEditorType.EditorType;
import org.webswing.server.services.security.extension.api.SecurityModuleExtensionConfig;

public interface PermissionMapExtensionConfig extends SecurityModuleExtensionConfig {
	
	@ConfigField(label="User Permissions")
	@ConfigFieldEditorType(editor=EditorType.ObjectListAsTable)
	@ConfigFieldDefaultValueObject(ArrayList.class)
	List<PermissionMapEntry> getUserPermissions();

	@ConfigField(label="Role Permissions")
	@ConfigFieldEditorType(editor=EditorType.ObjectListAsTable)
	@ConfigFieldDefaultValueObject(ArrayList.class)
	List<PermissionMapEntry> getRolePermissions();

}
