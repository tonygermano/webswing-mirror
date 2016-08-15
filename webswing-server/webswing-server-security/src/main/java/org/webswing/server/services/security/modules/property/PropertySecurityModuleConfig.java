package org.webswing.server.services.security.modules.property;

import org.webswing.Constants;
import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueString;
import org.webswing.server.services.security.extension.api.WebswingExtendableSecurityModuleConfig;

public interface PropertySecurityModuleConfig extends WebswingExtendableSecurityModuleConfig {

	@ConfigField
	@ConfigFieldDefaultValueString("${" + Constants.ROOT_DIR_PATH + "}/user.properties")
	String getFile();
}
