package org.webswing.server.services.security.modules;

import java.util.Map;

import org.webswing.model.server.SecurityMode;
import org.webswing.server.services.security.api.SecurityContext;

public interface SecurityModuleService {

	SecurityModuleWrapper create(SecurityContext context, SecurityMode mode, Map<String, Object> config);
}
