package org.webswing.server.services.security.otp.impl;

import java.util.List;

public interface OtpAccessConfig {
	Integer getValidForSec();

	//HmacSHA1, HmacSHA256, HmacSHA512
	String getHMacAlgo();

	String getSecret();

	List<String> getAllowedUsers();

	List<String> getAllowedRoles();

	List<String> getAllowedPermissions();
}
