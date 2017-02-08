package org.webswing.server.services.security.modules.keycloak;

import org.webswing.server.common.model.meta.*;
import org.webswing.server.services.security.extension.api.WebswingExtendableSecurityModuleConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vikto on 03-Feb-17.
 */
@ConfigFieldOrder({ "keyCloakUrl", "keyCloakRealms", "callbackUrl", "clientId", "clientSecret", "trustedPemFile", "usernameAttributeName", "rolesAttributeName", "logoutUrl" })
public interface KeycloakSecurityModuleConfig extends WebswingExtendableSecurityModuleConfig {
	@ConfigField(label = "KeyCloak URL")
	@ConfigFieldVariables
	String getKeyCloakUrl();

	@ConfigField(label = "Realms", description = "At least one realm is required. First realm is the default one.")
	@ConfigFieldVariables
	@ConfigFieldDefaultValueObject(ArrayList.class)
	List<String> getKeyCloakRealms();

	@ConfigField(label = "Callback URL", description = "URL of the webswing server where auth token will be received. Must end with /login")
	@ConfigFieldDefaultValueString("https://<webswing_host>:<webswing_port>/<swing_path>/login")
	@ConfigFieldVariables
	String getCallbackUrl();

	@ConfigField(label = "Client ID", description = "Client ID / API key")
	@ConfigFieldVariables
	String getClientId();

	@ConfigField(label = "Client Secret", description = "Leave empty if not required")
	@ConfigFieldVariables
	String getClientSecret();

	@ConfigField(label = "Trusted Certs File (PEM)", description = "Trusted cert chains to establish TLS connection with Open Id server. To disable cert validation use 'DISABLED' (only for testing)")
	@ConfigFieldVariables
	String getTrustedPemFile();

	@ConfigField(label = "Username Attribute Name", description = "ID Token claim name to be used as username")
	@ConfigFieldVariables
	@ConfigFieldDefaultValueString("preferred_username")
	String getUsernameAttributeName();

	@ConfigField(label = "Roles Attribute Name", description = "Leave empty if not required")
	@ConfigFieldVariables
	String getRolesAttributeName();

	@ConfigField(label = "Logout URL", description = "Webswing will redirect to this URL after logout.")
	String getLogoutUrl();

}
