package org.webswing.server.services.security.modules.saml2;

import org.webswing.server.services.security.api.WebswingSecurityModuleConfig;

public interface Saml2SecurityModuleConfig extends WebswingSecurityModuleConfig {

	public String getIdentityProviderMetadataFile();

	public String getServiceProviderConsumerUrl();

	public String getServiceProviderEntityId();

}
