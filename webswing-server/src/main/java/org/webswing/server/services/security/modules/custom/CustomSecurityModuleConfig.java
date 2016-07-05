package org.webswing.server.services.security.modules.custom;

import java.util.List;
import java.util.Map;

import org.webswing.server.services.security.api.WebswingSecurityModuleConfig;

public interface CustomSecurityModuleConfig extends WebswingSecurityModuleConfig {
	String getClassName();

	List<String> getClassPath();

	Map<String, Object> getConfig();
}
