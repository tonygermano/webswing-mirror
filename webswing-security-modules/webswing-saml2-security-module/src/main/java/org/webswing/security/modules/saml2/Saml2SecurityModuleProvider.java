package org.webswing.security.modules.saml2;

import org.webswing.server.services.security.api.WebswingSecurityModuleProvider;

import java.util.Arrays;
import java.util.List;

public class Saml2SecurityModuleProvider implements WebswingSecurityModuleProvider {
	@Override
	public List<String> getSecurityModuleClassNames() {
		return Arrays.asList(Saml2SecurityModule.class.getCanonicalName());
	}
}
