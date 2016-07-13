package org.webswing.server.services.security.modules;

import java.util.Map;

import org.webswing.model.server.SecurityMode;
import org.webswing.server.services.security.api.SecurityContext;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingSecurityModule;

public interface SecurityModuleService {

	WebswingSecurityModule<? extends WebswingCredentials> create(SecurityContext context, SecurityMode mode, Map<String, Object> config);
}
