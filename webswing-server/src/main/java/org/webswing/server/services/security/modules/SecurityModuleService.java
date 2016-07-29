package org.webswing.server.services.security.modules;

import org.webswing.model.server.WebswingSecurityConfig;
import org.webswing.server.services.security.api.SecurityContext;

public interface SecurityModuleService {

	SecurityModuleWrapper create(SecurityContext context, WebswingSecurityConfig config);
}
