package org.webswing.server.services.security.modules.keycloak;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.modules.AbstractExtendableSecurityModule;
import org.webswing.server.services.security.modules.openidconnect.OpenIdConnectClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeycloakSecurityModule extends AbstractExtendableSecurityModule<KeycloakSecurityModuleConfig> {
	private static final Logger log = LoggerFactory.getLogger(KeycloakSecurityModule.class);
	public static final String REALM_PARAM = "realm";

	private Map<String, OpenIdConnectClient> clients = new HashMap<>();
	private String defaultClient;

	public KeycloakSecurityModule(KeycloakSecurityModuleConfig config) {
		super(config);
	}

	public void init() {
		super.init();
		try {
			URL callback = new URL(replaceVar(getConfig().getCallbackUrl()));

			String clientId = replaceVar(getConfig().getClientId());
			String clientSecret = replaceVar(getConfig().getClientSecret());

			String trustedCertsPem = replaceVar(getConfig().getTrustedPemFile());
			File trustedCert = getConfig().getContext().resolveFile(trustedCertsPem);
			boolean disableCertValidation = "DISABLED".equals(trustedCertsPem);

			String roleAttrName = replaceVar(getConfig().getRolesAttributeName());
			String userAttrName = replaceVar(getConfig().getUsernameAttributeName());

			String keycloakUrl = replaceVar(getConfig().getKeycloakUrl());
			List<RealmEntry> realms = getConfig().getRealms();
			if (realms.size() > 0) {
				this.defaultClient = replaceVar(realms.get(0).getRealm());
				for (RealmEntry realm : realms) {
					String currentRealm = replaceVar(realm.getRealm());
					URL discoveryURL = new URL(String.format("%1s/realms/%2s/.well-known/openid-configuration", keycloakUrl, currentRealm));
					URL realmCallback = new URL(CommonUtil.addParam(callback.toString(), REALM_PARAM + "=" + currentRealm));
					OpenIdConnectClient client = new OpenIdConnectClient(discoveryURL, realmCallback, clientId, clientSecret, disableCertValidation, trustedCert, roleAttrName, userAttrName);
					client.setLogoutUrl(replaceVar(realm.getLogoutUrl()));
					clients.put(currentRealm, client);
				}
			} else {
				throw new RuntimeException("No Keycloak realms defined. At least one has to be defined");
			}
		} catch (Exception e) {
			log.error("Initializing of OpenID Connect client failed.", e);
			throw new RuntimeException("Initializing of OpenID Connect client failed.", e);
		}
	}

	@Override
	protected void serveLoginPage(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException {
		if (exception != null) {
			sendHtml(request, response, "saml2/errorPage.html", exception);
		} else {
			OpenIdConnectClient realmClient = clients.get(resolveRealmName(request));
			String url = realmClient.getOpenIDRedirectUrl();
			sendRedirect(request, response, url);
		}
	}

	private String resolveRealmName(HttpServletRequest request) {
		Map<String, Object> requestData = getLoginRequest(request);
		String r = (String) requestData.get(REALM_PARAM);
		return r == null || clients.get(r) == null ? defaultClient : r;
	}

	@Override
	protected void serveLoginPartial(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException {
		serveLoginPage(request, response, exception);
	}

	@Override
	protected AbstractWebswingUser authenticate(HttpServletRequest request) throws WebswingAuthenticationException {
		String openIdCode = OpenIdConnectClient.getCode(request);
		if (!StringUtils.isEmpty(openIdCode)) {
			try {
				String realmName = resolveRealmName(request);
				OpenIdConnectClient realmClient = clients.get(realmName);
				Map<String, Serializable> extraAttr = new HashMap<>();
				extraAttr.put(REALM_PARAM, realmName);
				AbstractWebswingUser user = realmClient.getUser(openIdCode, extraAttr);
				logSuccess(request, user.getUserId());
				return user;
			} catch (Exception e1) {
				logFailure(request, null, "Failed to authenticate." + e1.getMessage());
				log.error("Failed to authenticate", e1);
				throw new WebswingAuthenticationException("Failed to authenticate. " + e1.getMessage(), e1);
			}
		}
		return null;
	}

	@Override
	public void doLogout(HttpServletRequest request, HttpServletResponse response, AbstractWebswingUser user) throws ServletException, IOException {
		String logoutUrl = null;
		String realm = (String) user.getUserAttributes().get(REALM_PARAM);
		if (realm != null && clients.get(realm) != null) {
			logoutUrl = replaceVar(clients.get(realm).getLogoutUrl());
		}
		logoutRedirect(request, response, logoutUrl);
	}

}
