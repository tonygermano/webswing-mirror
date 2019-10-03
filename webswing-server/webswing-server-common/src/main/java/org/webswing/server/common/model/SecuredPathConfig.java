package org.webswing.server.common.model;

import java.util.*;

import org.webswing.server.common.model.meta.*;
import org.webswing.server.common.model.meta.ConfigFieldEditorType.EditorType;

@ConfigFieldOrder({ "enabled", "path", "homeDir", "webFolder","restrictedResources", "langFolder", "icon", "security", "allowedCorsOrigins", "swingConfig" })
public interface SecuredPathConfig extends Config {

	@ConfigField(label = "Enabled", description = "If true, application will be started automatically, when server starts.")
	@ConfigFieldDefaultValueBoolean(true)
	public boolean isEnabled();

	@ConfigField(label = "Context Path", description = "Url context path where the application will be deployed.")
	public String getPath();

	@ConfigField(label = "Home Folder", description = "Application's home directory. Application instances will be executed from this directory. This will also be the base directory of any relative classpath entries specified.")
	@ConfigFieldVariables
	@ConfigFieldDiscriminator
	@ConfigFieldDefaultValueString("${user.dir}")
	public String getHomeDir();

	@ConfigField(label = "Web Folder", description = "Folder to be used to store customized static web files like HTML, CSS or Javascript.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	@ConfigFieldDefaultValueString("")
	public String getWebFolder();

	@ConfigField(label = "Restricted Resources", description = "Defined Path-prefix restricts access to resources only to authenticated users. Applies to static resources inside 'Web Folder' or packaged with Webswing.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	@ConfigFieldDefaultValueObject(ArrayList.class)
	public List<String> getRestrictedResources();

	@ConfigField(label = "Localization Folder", description = "Folder to be used to store customized messages and translations in supported languages. English is available by default.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	@ConfigFieldDefaultValueString("")
	public String getLangFolder();

	@ConfigField(label = "Icon", description = "Path to icon displayed in application selection dialog. Recommended size 256x256.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	public String getIcon();

	@ConfigField(label = "Security")
	@ConfigFieldEditorType(editor = EditorType.Object, className = "org.webswing.server.services.security.api.WebswingSecurityConfig")
	@ConfigFieldDefaultValueObject(HashMap.class)
	public Map<String, Object> getSecurity();

	@ConfigField(label = "CORS Origins", description = "If you are embedding webswing to page on different domain, you have to enable Cross-origin resource sharing (CORS) by adding the domain in this list. Use * to allow all domains.")
	@ConfigFieldDefaultValueGenerator("defaultFromOldProperty")
	public List<String> getAllowedCorsOrigins();

	@ConfigField(label = "Application")
	@ConfigFieldDefaultValueObject
	SwingConfig getSwingConfig();

	public static  List<String> defaultFromOldProperty(SecuredPathConfig config) {
		return config.getSwingConfig()!=null? config.getSwingConfig().getAllowedCorsOrigins(): Collections.emptyList();
	}

}



