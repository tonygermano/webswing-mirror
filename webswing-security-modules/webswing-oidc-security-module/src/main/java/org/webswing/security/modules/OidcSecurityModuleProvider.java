package org.webswing.security.modules;

import org.webswing.security.modules.keycloak.KeycloakSecurityModule;
import org.webswing.security.modules.openidconnect.OpenIDConnectSecurityModule;
import org.webswing.server.services.security.api.WebswingSecurityModuleProvider;

import java.util.Arrays;
import java.util.List;

public class OidcSecurityModuleProvider implements WebswingSecurityModuleProvider {
	@Override
	public List<String> getSecurityModuleClassNames() {
		return Arrays.asList(KeycloakSecurityModule.class.getCanonicalName(), OpenIDConnectSecurityModule.class.getCanonicalName());
	}
}
