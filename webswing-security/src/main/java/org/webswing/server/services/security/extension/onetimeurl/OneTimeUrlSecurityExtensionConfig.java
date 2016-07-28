package org.webswing.server.services.security.extension.onetimeurl;

import java.util.Map;

import org.webswing.server.services.security.extension.api.SecurityModuleExtensionConfig;

public interface OneTimeUrlSecurityExtensionConfig extends SecurityModuleExtensionConfig {

	Map<String, OtpAccessConfig> getOtpAccessConfig();
	
}
