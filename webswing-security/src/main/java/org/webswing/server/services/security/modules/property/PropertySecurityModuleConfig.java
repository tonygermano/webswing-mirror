package org.webswing.server.services.security.modules.property;

import org.webswing.server.services.security.otp.impl.WebswingOtpSecurityModuleConfig;

public interface PropertySecurityModuleConfig extends WebswingOtpSecurityModuleConfig {

	String getFile();
}
