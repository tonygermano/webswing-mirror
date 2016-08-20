package org.webswing.server.common.model;

import java.util.HashMap;
import java.util.Map;

import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueObject;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueString;
import org.webswing.server.common.model.meta.ConfigFieldEditorType;
import org.webswing.server.common.model.meta.ConfigFieldWithVariables;
import org.webswing.server.common.model.meta.ConfigGroup;
import org.webswing.server.common.model.meta.ConfigFieldEditorType.EditorType;
import org.webswing.server.common.model.meta.ConfigFieldOrder;

@ConfigFieldOrder({ "path", "homeDir", "webFolder", "security", "swingConfig" })
public interface SecuredPathConfig extends Config {

	@ConfigField(tab = ConfigGroup.General, label = "Path", description = "Application path")
	public String getPath();

	@ConfigField(tab = ConfigGroup.General, label = "Home", description = "Application home folder")
	@ConfigFieldWithVariables
	@ConfigFieldDefaultValueString("${user.dir}")
	public String getHomeDir();

	@ConfigField(tab = ConfigGroup.General, label = "Web Folder", description = "Folder to be used to store customized static web files like HTML, CSS or Javascritp.")
	@ConfigFieldDefaultValueString("www")
	public String getWebFolder();

	@ConfigField(tab = ConfigGroup.Security)
	@ConfigFieldEditorType(editor = EditorType.Object, className = "org.webswing.server.services.security.api.WebswingSecurityConfig")
	@ConfigFieldDefaultValueObject(HashMap.class)
	public Map<String, Object> getSecurity();

	@ConfigField(tab = ConfigGroup.Swing)
	SwingConfig getSwingConfig();
}
