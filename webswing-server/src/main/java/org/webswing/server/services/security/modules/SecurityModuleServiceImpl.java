package org.webswing.server.services.security.modules;

import org.webswing.model.server.WebswingSecurityConfig;
import org.webswing.server.services.security.api.SecurityContext;

import com.google.inject.Singleton;

@Singleton
public class SecurityModuleServiceImpl implements SecurityModuleService {

	public SecurityModuleWrapper create(SecurityContext context, WebswingSecurityConfig config) {
		if (SecurityModuleWrapper.getSecurityModuleClassName(config) != null) {
			return new SecurityModuleWrapper(context, config);
		} else {
			return null;
		}
	}

}
