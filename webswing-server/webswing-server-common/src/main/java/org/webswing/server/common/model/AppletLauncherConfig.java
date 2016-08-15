package org.webswing.server.common.model;

import java.util.Map;

import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldWithVariables;
import org.webswing.server.common.model.meta.ConfigType;

@ConfigType
public interface AppletLauncherConfig {

	@ConfigField(label = "Applet class", description = "")
	@ConfigFieldWithVariables
	public String getAppletClass();

	@ConfigField(label = "Applet parameters", description = "")
	@ConfigFieldWithVariables
	public Map<String, String> getParameters();
}
