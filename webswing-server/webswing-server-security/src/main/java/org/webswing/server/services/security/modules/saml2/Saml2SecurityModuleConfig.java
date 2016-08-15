package org.webswing.server.services.security.modules.saml2;

import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.services.security.extension.api.WebswingExtendableSecurityModuleConfig;

public interface Saml2SecurityModuleConfig extends WebswingExtendableSecurityModuleConfig {

	@ConfigField
	public String getIdentityProviderMetadataFile();

	@ConfigField
	public String getServiceProviderConsumerUrl();

	@ConfigField
	public String getServiceProviderEntityId();

	@ConfigField
	public String getLogoutUrl();

}
