package org.webswing.server.services.security.extension.permissionmap;

import java.util.Map;

import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldEditorType;
import org.webswing.server.common.model.meta.ConfigFieldEditorType.EditorType;
import org.webswing.server.common.model.meta.ConfigFieldPresets;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.extension.api.SecurityModuleExtensionConfig;

public interface PermissionMapExtensionConfig extends SecurityModuleExtensionConfig {
	
	@ConfigField(label="Permission")
	@ConfigFieldPresets(enumClass=WebswingAction.class)
	@ConfigFieldEditorType(editor=EditorType.ObjectMap)
	Map<String, PermissionMapEntry> getPermissions();

}
