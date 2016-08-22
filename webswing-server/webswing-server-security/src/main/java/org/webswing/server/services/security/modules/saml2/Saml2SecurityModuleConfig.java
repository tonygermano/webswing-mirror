package org.webswing.server.services.security.modules.saml2;

import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueGenerator;
import org.webswing.server.common.model.meta.ConfigFieldEditorType;
import org.webswing.server.common.model.meta.ConfigFieldEditorType.EditorType;
import org.webswing.server.common.model.meta.ConfigFieldOrder;
import org.webswing.server.services.security.extension.api.WebswingExtendableSecurityModuleConfig;

@ConfigFieldOrder({ "identityProviderMetadataFile", "serviceProviderConsumerUrl", "serviceProviderEntityId", "logoutUrl" })
public interface Saml2SecurityModuleConfig extends WebswingExtendableSecurityModuleConfig {

	@ConfigField(label = "Identity Provider Metadata File", description = "Metadata file downloaded from Identity provider.")
	public String getIdentityProviderMetadataFile();

	@ConfigField(label = "Service Provider Consumer URL", description = "Url that verifies the SAML2 token. Should be 'https://<webswing_host>:<webswing_port>/<current_app_path>/login'")
	@ConfigFieldDefaultValueGenerator("generateConsumerUrl")
	public String getServiceProviderConsumerUrl();

	@ConfigField(label = "Service Provider Entity ID", description = "Identitficator used when registering Webswing with Idp.")
	public String getServiceProviderEntityId();

	@ConfigField(label = "Logout URL", description = "Webswing will redirect to this URL after logout.")
	public String getLogoutUrl();

	public static String generateConsumerUrl(Saml2SecurityModuleConfig c) {
		return "https://<webswing_host>:<webswing_port>" + c.getContext().getSecuredPath() + "/login";
	}
}
