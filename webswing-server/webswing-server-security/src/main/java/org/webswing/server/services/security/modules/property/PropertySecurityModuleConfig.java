package org.webswing.server.services.security.modules.property;

import org.webswing.Constants;
import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueString;
import org.webswing.server.common.model.meta.ConfigFieldOrder;
import org.webswing.server.common.model.meta.ConfigFieldVariables;
import org.webswing.server.services.security.extension.api.WebswingExtendableSecurityModuleConfig;

@ConfigFieldOrder({"file"})
public interface PropertySecurityModuleConfig extends WebswingExtendableSecurityModuleConfig {

	@ConfigField(label="File", description="Path pointing to users properties file.")
	@ConfigFieldDefaultValueString("${" + Constants.ROOT_DIR_PATH + "}/user.properties")
	@ConfigFieldVariables
	String getFile();
}
