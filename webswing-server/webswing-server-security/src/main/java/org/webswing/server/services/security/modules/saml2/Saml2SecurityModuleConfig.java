package org.webswing.server.services.security.modules.saml2;

import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueString;
import org.webswing.server.common.model.meta.ConfigFieldOrder;
import org.webswing.server.common.model.meta.ConfigFieldVariables;
import org.webswing.server.common.model.meta.VariableSetName;
import org.webswing.server.services.security.extension.api.WebswingExtendableSecurityModuleConfig;

@ConfigFieldOrder({ "identityProviderMetadataFile", "serviceProviderConsumerUrl", "serviceProviderEntityId", "logoutUrl" })
public interface Saml2SecurityModuleConfig extends WebswingExtendableSecurityModuleConfig {

	@ConfigField(label = "Identity Provider Metadata File", description = "Metadata file downloaded from Identity provider.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	public String getIdentityProviderMetadataFile();

	@ConfigField(label = "Service Provider Consumer URL", description = "Url that verifies the SAML2 token. Should be 'https://<webswing_host>:<webswing_port>/<current_app_path>/login'")
	@ConfigFieldDefaultValueString("https://<webswing_host>:<webswing_port>/<swing_path>/login")
	public String getServiceProviderConsumerUrl();

	@ConfigField(label = "Service Provider Entity ID", description = "Identitficator used when registering Webswing with Idp.")
	public String getServiceProviderEntityId();

	@ConfigField(label = "Logout URL", description = "Webswing will redirect to this URL after logout.")
	public String getLogoutUrl();

}
