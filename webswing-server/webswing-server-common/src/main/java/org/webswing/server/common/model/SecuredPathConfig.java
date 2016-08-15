package org.webswing.server.common.model;

import java.util.Map;

import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueString;
import org.webswing.server.common.model.meta.ConfigFieldEditorType;
import org.webswing.server.common.model.meta.ConfigGroup;
import org.webswing.server.common.model.meta.ConfigFieldEditorType.EditorType;

public interface SecuredPathConfig extends Config {

	@ConfigField(tab = ConfigGroup.General, label = "Path", description = "Application path")
	public String getPath();

	@ConfigField
	@ConfigFieldDefaultValueString("www")
	public String getWebFolder();

	@ConfigField
	@ConfigFieldEditorType(editor = EditorType.Object, className = "org.webswing.server.services.security.WebswingSecurityConfig")
	public Map<String, Object> getSecurity();

	@ConfigField
	SwingConfig getSwingConfig();
}
