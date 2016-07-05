package org.webswing.server.services.security.modules;

import java.util.Map;

import org.webswing.model.server.SecurityMode;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.modules.anonym.AnonymSecurityModule;
import org.webswing.server.services.security.modules.custom.CustomSecurityModule;
import org.webswing.server.services.security.modules.custom.CustomSecurityModuleConfig;
import org.webswing.server.services.security.modules.property.PropertySecurityModule;
import org.webswing.server.services.security.modules.property.PropertySecurityModuleConfig;
import org.webswing.server.util.ServerUtil;

import com.google.inject.Singleton;

@Singleton
public class SecurityModuleServiceImpl implements SecurityModuleService {

	public WebswingSecurityModule<? extends WebswingCredentials> create(SecurityMode mode, Map<String, Object> config) {
		switch (mode) {
		case INHERITED:
			return null;
		case NONE:
			return new AnonymSecurityModule(config);
		case PROPERTY_FILE:
			return new PropertySecurityModule(ServerUtil.instantiateConfig(config, PropertySecurityModuleConfig.class));
		case CUSTOM:
			return new CustomSecurityModule(ServerUtil.instantiateConfig(config, CustomSecurityModuleConfig.class));
		default:
			break;
		}
		return null;
	}
}
