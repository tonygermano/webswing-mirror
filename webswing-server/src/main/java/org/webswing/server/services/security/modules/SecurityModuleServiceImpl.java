package org.webswing.server.services.security.modules;

import java.util.Map;

import org.webswing.model.server.SecurityMode;
import org.webswing.server.services.security.api.SecurityContext;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.modules.anonym.AnonymSecurityModule;
import org.webswing.server.services.security.modules.custom.CustomSecurityModule;
import org.webswing.server.services.security.modules.custom.CustomSecurityModuleConfig;
import org.webswing.server.services.security.modules.property.PropertySecurityModule;
import org.webswing.server.services.security.modules.property.PropertySecurityModuleConfig;
import org.webswing.server.services.security.modules.saml2.Saml2SecurityModule;
import org.webswing.server.services.security.modules.saml2.Saml2SecurityModuleConfig;
import org.webswing.server.util.ServerUtil;

import com.google.inject.Singleton;

@Singleton
public class SecurityModuleServiceImpl implements SecurityModuleService {

	public WebswingSecurityModule<? extends WebswingCredentials> create(SecurityContext context, SecurityMode mode, Map<String, Object> config) {
		switch (mode) {
		case INHERITED:
			return null;
		case NONE:
			return new AnonymSecurityModule(config);
		case PROPERTY_FILE:
			return new PropertySecurityModule(ServerUtil.instantiateConfig(config, PropertySecurityModuleConfig.class, context));
		case CUSTOM:
			return new CustomSecurityModule(ServerUtil.instantiateConfig(config, CustomSecurityModuleConfig.class, context));
		case SAML2:
			return new Saml2SecurityModule(ServerUtil.instantiateConfig(config, Saml2SecurityModuleConfig.class, context));
		default:
			break;
		}
		return null;
	}
}
