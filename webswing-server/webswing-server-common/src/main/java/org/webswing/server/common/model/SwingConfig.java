package org.webswing.server.common.model;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueBoolean;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueNumber;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueString;
import org.webswing.server.common.model.meta.ConfigFieldDiscriminator;
import org.webswing.server.common.model.meta.ConfigFieldEditorType;
import org.webswing.server.common.model.meta.ConfigFieldPresets;
import org.webswing.server.common.model.meta.ConfigFieldWithVariables;
import org.webswing.server.common.model.meta.ConfigGroup;
import org.webswing.server.common.model.meta.ConfigType;
import org.webswing.server.common.model.meta.MetadataGenerator;
import org.webswing.server.common.model.meta.ConfigFieldEditorType.EditorType;

@ConfigType(metadataGenerator = SwingConfig.SwingConfigurationMetadataGenerator.class)
public interface SwingConfig extends Config {
	public enum SessionMode {
		ALWAYS_NEW_SESSION,
		CONTINUE_FOR_BROWSER,
		CONTINUE_FOR_USER;
	}

	public enum LauncherType {
		Applet,
		Desktop;
	}

	@ConfigField(tab = ConfigGroup.General, label = "Name", description = "Application name")
	public String getName();

	@ConfigField(tab = ConfigGroup.General, label = "Icon", description = "Application icon file")
	@ConfigFieldWithVariables
	public String getIcon();

	@ConfigField(tab = ConfigGroup.General, label = "VM args", description = "")
	@ConfigFieldDefaultValueString("Murrine")
	@ConfigFieldPresets()
	public String getTheme();
	
	@ConfigField(tab = ConfigGroup.Java, label = "Launcher type", description = "Select the application launcher type")
	@ConfigFieldDiscriminator
	public LauncherType getLauncherType();

	@ConfigField(tab = ConfigGroup.Java, label = "Launcher configuration", description = "Launcher specific configuration options")
	@ConfigFieldEditorType(editor = EditorType.Object)
	public Map<String, Object> getLauncherConfig();

	@ConfigField(tab = ConfigGroup.Java, label = "JRE Executable", description = "Path to java executable")
	@ConfigFieldWithVariables
	@ConfigFieldDefaultValueString("${java.home}/bin/java")
	public String getJreExecutable();

	@ConfigField(tab = ConfigGroup.Java, label = "Java Version", description = "Version of java")
	@ConfigFieldWithVariables
	@ConfigFieldDefaultValueString("${java.version}")
	public String getJavaVersion();

	@ConfigField(tab = ConfigGroup.Java, label = "VM args", description = "")
	public String getVmArgs();

	@ConfigField(tab = ConfigGroup.Java, label = "VM args", description = "")
	public List<String> getClassPathEntries();


	@ConfigField(tab = ConfigGroup.General, label = "Fonts", description = "")
	public Map<String, String> getFontConfig();

	@ConfigField(tab = ConfigGroup.Session, label = "Max Connections", description = "")
	@ConfigFieldDefaultValueNumber(1)
	public int getMaxClients();

	@ConfigField(tab = ConfigGroup.Session, label = "Session Mode", description = "")
	@ConfigFieldDefaultValueString("CONTINUE_FOR_BROWSER")
	public SessionMode getSessionMode();

	@ConfigField(tab = ConfigGroup.Session, label = "Session timeout", description = "")
	@ConfigFieldDefaultValueNumber(300)
	public int getSwingSessionTimeout();

	@ConfigField(tab = ConfigGroup.Session, label = "Session stealing", description = "")
	@ConfigFieldDefaultValueBoolean(true)
	public boolean isAllowStealSession();

	@ConfigField(tab = ConfigGroup.General, label = "Text Anti-alias", description = "")
	@ConfigFieldDefaultValueBoolean(true)
	public boolean isAntiAliasText();

	@ConfigField
	@ConfigFieldDefaultValueBoolean(false)
	public boolean isAuthorization();

	@ConfigField(tab = ConfigGroup.Features, label = "Isolated Filesystem", description = "")
	@ConfigFieldDefaultValueBoolean(false)
	public boolean isIsolatedFs();

	@ConfigField(tab = ConfigGroup.Java, label = "Enable Debug Mode", description = "")
	@ConfigFieldDefaultValueBoolean(false)
	public boolean isDebug();

	@ConfigField
	@ConfigFieldDefaultValueBoolean(true)
	public boolean isAuthentication();

	@ConfigField(tab = ConfigGroup.General, label = "DirectDraw Rendering", description = "")
	@ConfigFieldDefaultValueBoolean(true)
	public boolean isDirectdraw();

	@ConfigField(tab = ConfigGroup.Features, label = "Allow Deleting Files ", description = "")
	@ConfigFieldDefaultValueBoolean(true)
	public boolean isAllowDelete();

	@ConfigField(tab = ConfigGroup.Features, label = "Allow Downloading Files ", description = "")
	@ConfigFieldDefaultValueBoolean(true)
	public boolean isAllowDownload();

	@ConfigField(tab = ConfigGroup.Features, label = "Allow Auto-Downloading Files ", description = "")
	@ConfigFieldDefaultValueBoolean(true)
	public boolean isAllowAutoDownload();

	@ConfigField(tab = ConfigGroup.Features, label = "Allow Uploading Files ", description = "")
	@ConfigFieldDefaultValueBoolean(true)
	public boolean isAllowUpload();

	@ConfigField(tab = ConfigGroup.Features, label = "Allow CORS origins", description = "")
	public List<String> getAllowedCorsOrigins();

	@ConfigField(tab = ConfigGroup.Features, label = "Upload size limit ", description = "")
	@ConfigFieldDefaultValueNumber(5)
	public double getUploadMaxSize();

	@ConfigField(tab = ConfigGroup.Features, label = "Allow JsLink", description = "")
	@ConfigFieldDefaultValueBoolean(true)
	public boolean isAllowJsLink();

	public static class SwingConfigurationMetadataGenerator extends MetadataGenerator<SwingConfig> {
		@Override
		public Class<?> getExplicitType(SwingConfig config, ClassLoader cl, String propertyName, Method readMethod, Object value) throws ClassNotFoundException {
			if (propertyName.equals("launcherConfig")) {
				switch (config.getLauncherType()) {
				case Applet:
					return AppletLauncherConfig.class;
				case Desktop:
					return DesktopLauncherConfig.class;
				default:
					return null;
				}
			} else {
				return super.getExplicitType(config, cl, propertyName, readMethod, value);
			}
		}

	}
}
