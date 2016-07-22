package org.webswing.server.services.security.modules.saml2;

import org.webswing.server.services.security.otp.impl.WebswingOtpSecurityModuleConfig;

public interface Saml2SecurityModuleConfig extends WebswingOtpSecurityModuleConfig {

	public String getIdentityProviderMetadataFile();

	public String getServiceProviderConsumerUrl();

	public String getServiceProviderEntityId();

}
