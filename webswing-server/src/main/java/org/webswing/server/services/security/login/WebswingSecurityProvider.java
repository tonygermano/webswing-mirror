package org.webswing.server.services.security.login;

import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingSecurityModule;

public interface WebswingSecurityProvider {

	<T extends WebswingCredentials> WebswingSecurityModule<T> get();

}
