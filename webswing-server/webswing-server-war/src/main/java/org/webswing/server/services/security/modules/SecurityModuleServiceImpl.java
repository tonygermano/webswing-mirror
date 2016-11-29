package org.webswing.server.services.security.modules;

import org.webswing.server.extension.ExtensionClassLoader;
import org.webswing.server.services.security.api.BuiltInModules;
import org.webswing.server.services.security.api.SecurityContext;
import org.webswing.server.services.security.api.WebswingSecurityConfig;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SecurityModuleServiceImpl implements SecurityModuleService {

	private final ExtensionClassLoader extensionLoader;

	@Inject
	public SecurityModuleServiceImpl(ExtensionClassLoader extensionLoader) {
		this.extensionLoader = extensionLoader;
	}

	public SecurityModuleWrapper create(SecurityContext context, WebswingSecurityConfig config) {
		if (BuiltInModules.getSecurityModuleClassName(config.getModule()) != null) {
			return new SecurityModuleWrapper(context, config, extensionLoader);
		} else {
			return null;
		}
	}

}
