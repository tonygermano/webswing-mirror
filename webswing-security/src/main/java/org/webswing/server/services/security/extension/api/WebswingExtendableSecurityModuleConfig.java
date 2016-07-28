package org.webswing.server.services.security.extension.api;

import java.util.List;

import org.webswing.server.services.security.api.WebswingSecurityModuleConfig;

public interface WebswingExtendableSecurityModuleConfig extends WebswingSecurityModuleConfig{

	List<String> getExtensions();
}
