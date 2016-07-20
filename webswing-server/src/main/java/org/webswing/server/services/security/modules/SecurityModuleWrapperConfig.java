package org.webswing.server.services.security.modules;

import java.util.List;
import java.util.Map;

import org.webswing.server.services.security.api.WebswingSecurityModuleConfig;

public interface SecurityModuleWrapperConfig extends WebswingSecurityModuleConfig {
	String getClassName();

	List<String> getClassPath();

	Map<String, Object> getConfig();
}
