package org.webswing.server.services.security.api;

public interface WebswingSecurityModuleConfig {
	SecurityContext getContext();

	<T> T getValueAs(String name, Class<T> clazz);
}
