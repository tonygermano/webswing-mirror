package org.webswing.server.common.model;

import java.util.HashMap;
import java.util.Map;

import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueObject;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueString;
import org.webswing.server.common.model.meta.ConfigFieldDiscriminator;
import org.webswing.server.common.model.meta.ConfigFieldEditorType;
import org.webswing.server.common.model.meta.ConfigFieldEditorType.EditorType;
import org.webswing.server.common.model.meta.ConfigFieldOrder;
import org.webswing.server.common.model.meta.ConfigFieldVariables;
import org.webswing.server.common.model.meta.VariableSetName;

@ConfigFieldOrder({ "path", "homeDir", "webFolder", "icon", "security", "swingConfig" })
public interface SecuredPathConfig extends Config {

	@ConfigField(label = "Context Path", description = "Url context path where the application will be deployed.")
	public String getPath();

	@ConfigField(label = "Home Folder", description = "Swing application's home directory. Swing application instances will be executed from this directory. This will also be the base directory of any relative classpath entries specified.")
	@ConfigFieldVariables
	@ConfigFieldDiscriminator
	@ConfigFieldDefaultValueString("${user.dir}")
	public String getHomeDir();

	@ConfigField(label = "Web Folder", description = "Folder to be used to store customized static web files like HTML, CSS or Javascript.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	@ConfigFieldDefaultValueString("")
	public String getWebFolder();

	@ConfigField(label = "Icon", description = "Path to icon displayed in application selection dialog.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	public String getIcon();

	@ConfigField(label = "Security")
	@ConfigFieldEditorType(editor = EditorType.Object, className = "org.webswing.server.services.security.api.WebswingSecurityConfig")
	@ConfigFieldDefaultValueObject(HashMap.class)
	public Map<String, Object> getSecurity();

	@ConfigField(label = "Swing App")
	SwingConfig getSwingConfig();
}
