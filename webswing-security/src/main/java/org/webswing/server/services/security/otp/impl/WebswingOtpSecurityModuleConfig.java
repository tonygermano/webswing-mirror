package org.webswing.server.services.security.otp.impl;

import java.util.Map;

import org.webswing.server.services.security.api.WebswingSecurityModuleConfig;

public interface WebswingOtpSecurityModuleConfig extends WebswingSecurityModuleConfig {

	Map<String, OtpAccessConfig> getOtpAccessConfig();
	
}
