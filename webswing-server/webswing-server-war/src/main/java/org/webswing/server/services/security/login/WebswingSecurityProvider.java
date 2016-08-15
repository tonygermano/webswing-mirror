package org.webswing.server.services.security.login;

import org.webswing.server.services.security.modules.SecurityModuleWrapper;

public interface WebswingSecurityProvider {

	SecurityModuleWrapper get();

}
