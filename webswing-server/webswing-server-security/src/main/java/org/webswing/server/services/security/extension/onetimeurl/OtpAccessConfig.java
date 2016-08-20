package org.webswing.server.services.security.extension.onetimeurl;

import java.util.List;

import org.webswing.server.common.model.Config;
import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueNumber;

public interface OtpAccessConfig extends Config {
	@ConfigField
	@ConfigFieldDefaultValueNumber(30)
	Integer getValidForSec();

	//HmacSHA1, HmacSHA256, HmacSHA512
	@ConfigField
	String getHMacAlgo();

	@ConfigField
	String getSecret();

	@ConfigField
	List<String> getAllowedUsers();

	@ConfigField
	List<String> getAllowedRoles();

	@ConfigField
	List<String> getAllowedPermissions();
}
