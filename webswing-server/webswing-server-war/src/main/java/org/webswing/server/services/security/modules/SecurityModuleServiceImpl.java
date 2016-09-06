package org.webswing.server.services.security.modules;

import org.webswing.server.services.security.api.BuiltInModules;
import org.webswing.server.services.security.api.SecurityContext;
import org.webswing.server.services.security.api.WebswingSecurityConfig;

import com.google.inject.Singleton;

@Singleton
public class SecurityModuleServiceImpl implements SecurityModuleService {

	public SecurityModuleWrapper create(SecurityContext context, WebswingSecurityConfig config) {
		if (BuiltInModules.getSecurityModuleClassName(config.getModule()) != null) {
			return new SecurityModuleWrapper(context, config);
		} else {
			return null;
		}
	}

}
