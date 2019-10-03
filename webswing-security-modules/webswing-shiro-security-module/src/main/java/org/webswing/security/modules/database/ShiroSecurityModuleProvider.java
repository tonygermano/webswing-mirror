package org.webswing.security.modules.database;

import org.webswing.server.services.security.api.WebswingSecurityModuleProvider;
import org.webswing.security.modules.property.PropertySecurityModule;

import java.util.Arrays;
import java.util.List;

public class ShiroSecurityModuleProvider implements WebswingSecurityModuleProvider {
	@Override
	public List<String> getSecurityModuleClassNames() {
		return Arrays.asList(DatabaseSecurityModule.class.getCanonicalName(), PropertySecurityModule.class.getCanonicalName());
	}
}
