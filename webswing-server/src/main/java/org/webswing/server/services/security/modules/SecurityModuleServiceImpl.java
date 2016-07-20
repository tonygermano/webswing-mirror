package org.webswing.server.services.security.modules;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.webswing.model.server.SecurityMode;
import org.webswing.server.services.security.api.SecurityContext;
import org.webswing.server.services.security.modules.anonym.AnonymSecurityModule;
import org.webswing.server.services.security.modules.property.PropertySecurityModule;
import org.webswing.server.services.security.modules.saml2.Saml2SecurityModule;
import org.webswing.server.util.ServerUtil;

import com.google.inject.Singleton;

@Singleton
public class SecurityModuleServiceImpl implements SecurityModuleService {

	public SecurityModuleWrapper create(SecurityContext context, SecurityMode mode, Map<String, Object> config) {
		switch (mode) {
		case INHERITED:
			return null;
		case NONE:
			return new SecurityModuleWrapper(createBuilInConfig(AnonymSecurityModule.class, config, context));
		case PROPERTY_FILE:
			return new SecurityModuleWrapper(createBuilInConfig(PropertySecurityModule.class, config, context));
		case SAML2:
			return new SecurityModuleWrapper(createBuilInConfig(Saml2SecurityModule.class, config, context));
		case CUSTOM:
			return new SecurityModuleWrapper(ServerUtil.instantiateConfig(config, SecurityModuleWrapperConfig.class, context));

		default:
			break;
		}
		return null;
	}

	private SecurityModuleWrapperConfig createBuilInConfig(final Class<?> securityModule, final Map<String, Object> config, final SecurityContext context) {
		SecurityModuleWrapperConfig wrapperConfig = new SecurityModuleWrapperConfig() {

			@Override
			public SecurityContext getContext() {
				return context;
			}

			@Override
			public Map<String, Object> getConfig() {
				return config;
			}

			@Override
			public List<String> getClassPath() {
				return Collections.emptyList();
			}

			@Override
			public String getClassName() {
				return securityModule.getName();
			}
		};

		return wrapperConfig;
	}

}
