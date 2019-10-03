package org.webswing.security.modules.saml2;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.webswing.server.common.model.meta.*;
import org.webswing.server.services.security.extension.api.WebswingExtendableSecurityModuleConfig;

@ConfigFieldOrder({ "identityProviderMetadataFile", "serviceProviderConsumerUrl", "serviceProviderEntityId", "authnRequestSigned", "nameIdPolicyFormat", "userAttributeName", "rolesAttributeName", "keyStore", "decryptionKeyAlias", "keyStorePwd", "keyPwd", "singleLogout", "logoutUrl" })
public interface Saml2SecurityModuleConfig extends WebswingExtendableSecurityModuleConfig {

	@ConfigField(label = "Identity Provider Metadata URI", description = "Identity provide Metadata xml file local or remote URI.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	String getIdentityProviderMetadataFile();

	@ConfigField(label = "Service Provider Consumer URL", description = "Url that verifies the SAML2 token. Should be 'https://<webswing_host>:<webswing_port>/<current_app_path>/login'. SP metadata xml will be available on same URL with '?metadata' query param.")
	@ConfigFieldDefaultValueString("https://<webswing_host>:<webswing_port>/<context_path>${webswing.appPath}/login")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	String getServiceProviderConsumerUrl();

	@ConfigField(label = "Service Provider Entity ID", description = "Identitficator used when registering Webswing with Idp.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	String getServiceProviderEntityId();

	@ConfigField(label = "AuthnRequests Signed", description = "Indicates whether the Idp expects signed AuthnRequests. Idp needs to public key stored in Key store configured below to validate this signature.")
	@ConfigFieldDefaultValueBoolean(false)
	boolean isAuthnRequestSigned();

	@ConfigField(label = "NameId Policy Format", description = "The name Id format to use for the subject.")
	@ConfigFieldPresets({ "urn:oasis:names:tc:SAML:2.0:nameid-format:transient", "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent", "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress", "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified" })
	String getNameIdPolicyFormat();

	@ConfigField(label = "Username Attribute Name", description = "Name of SAML2 attribute defining the username. If empty, NameId value will be used.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	String getUserAttributeName();

	@ConfigField(label = "Roles Attribute Name", description = "Name of SAML2 attribute that contains list of roles. Leave empty if not required")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	@ConfigFieldDefaultValueString("roles")
	String getRolesAttributeName();

	@ConfigField(label = "Use Single Logout", description = "Webswing will trigger Idp log-out")
	@ConfigFieldDefaultValueBoolean(true)
	boolean isSingleLogout();

	@ConfigField(label = "Logout URL", description = "Webswing will redirect to this URL after logout.")
	String getLogoutUrl();

	@ConfigField(label = "Key Store", description = "PKCS#12 or JKS Key Store file containing the private key used to decrypt the assertions returned by server. If file does not exits it will be generated.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	@ConfigFieldDefaultValueString("${webswing.homeFolder}/saml2-generatedKeystore.jks")
	String getKeyStore();

	@ConfigField(label = "Key Store Alias", description = "Key alias the private key is stored under.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	String getDecryptionKeyAlias();

	@ConfigField(label = "Key Store Password", description = "Password to access the key store.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	@ConfigFieldDefaultValueString("Change-Me!")
	String getKeyStorePwd();

	@ConfigField(label = "Private Key Password", description = "Password to access the private key.")
	@ConfigFieldVariables(VariableSetName.SwingApp)
	@ConfigFieldDefaultValueString("Change-Me!")
	String getKeyPwd();

}
