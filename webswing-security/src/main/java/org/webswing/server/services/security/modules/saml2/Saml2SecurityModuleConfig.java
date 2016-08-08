package org.webswing.server.services.security.modules.saml2;

import org.webswing.server.services.security.extension.api.WebswingExtendableSecurityModuleConfig;

public interface Saml2SecurityModuleConfig extends WebswingExtendableSecurityModuleConfig {

	public String getIdentityProviderMetadataFile();

	public String getServiceProviderConsumerUrl();

	public String getServiceProviderEntityId();
	
	public String getLogoutUrl();

}
