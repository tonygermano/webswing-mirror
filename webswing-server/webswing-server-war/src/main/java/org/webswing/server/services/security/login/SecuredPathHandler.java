package org.webswing.server.services.security.login;

import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.services.security.modules.SecurityModuleWrapper;

public interface SecuredPathHandler {

	SecurityModuleWrapper get();

	SecuredPathConfig getConfig();

	String getPathMapping();

	boolean isStarted();
}
