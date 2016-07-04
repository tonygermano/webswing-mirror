package org.webswing.server.services.security.modules.custom;

import java.util.List;
import java.util.Map;

public interface CustomSecurityModuleConfig {
	String getClassName();

	List<String> getClassPath();

	Map<String, Object> getConfig();
}
