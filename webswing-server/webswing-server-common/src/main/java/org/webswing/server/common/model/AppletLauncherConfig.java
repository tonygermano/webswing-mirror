package org.webswing.server.common.model;

import java.util.Map;

import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldOrder;
import org.webswing.server.common.model.meta.ConfigFieldVariables;
import org.webswing.server.common.model.meta.ConfigType;

@ConfigType
@ConfigFieldOrder({"appletClass","parameters"})
public interface AppletLauncherConfig {

	@ConfigField(label = "Applet Class", description = "Applet's fully qualiffied main class name. Must implement the java.applet.Applet interface. (ie. 'com.mypackage.MyApplet')")
	@ConfigFieldVariables
	public String getAppletClass();

	@ConfigField(label = "Applet Parameters", description = "Applets start parameters. Set of key-value pairs accessible to applet at startup.")
	@ConfigFieldVariables
	public Map<String, String> getParameters();
}
