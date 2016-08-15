package org.webswing.server.common.model;

import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldWithVariables;
import org.webswing.server.common.model.meta.ConfigType;

@ConfigType
public interface DesktopLauncherConfig {

	@ConfigField(label = "Main class", description = "")
	@ConfigFieldWithVariables
	public String getMainClass();

	@ConfigField(label = "Args", description = "")
	@ConfigFieldWithVariables
	public String getArgs();
}
