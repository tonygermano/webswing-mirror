package org.webswing.server.services.security.extension.onetimeurl;

import java.util.Map;

import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.services.security.extension.api.SecurityModuleExtensionConfig;

public interface OneTimeUrlSecurityExtensionConfig extends SecurityModuleExtensionConfig {

	@ConfigField
	Map<String, OtpAccessConfig> getApiKeys();

}
