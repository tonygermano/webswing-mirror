package org.webswing.server.services.security.modules;

import org.webswing.server.services.security.api.SecurityContext;
import org.webswing.server.services.security.api.WebswingSecurityConfig;

public interface SecurityModuleService {

	SecurityModuleWrapper create(SecurityContext context, WebswingSecurityConfig config);
}
