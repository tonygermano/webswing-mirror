package org.webswing.server.services.security.modules.openidconnect;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.modules.AbstractExtendableSecurityModule;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class OpenIDConnectSecurityModule extends AbstractExtendableSecurityModule<OpenIDConnectSecurityModuleConfig> {
	private static final Logger log = LoggerFactory.getLogger(OpenIDConnectSecurityModule.class);

	private OpenIdConnectClient client;

	public OpenIDConnectSecurityModule(OpenIDConnectSecurityModuleConfig config) {
		super(config);
	}

	public void init() {
		super.init();
		try {
			URL callback = new URL(getConfig().getContext().replaceVariables(getConfig().getCallbackUrl()));

			String trustedCertsPem = getConfig().getContext().replaceVariables(getConfig().getTrustedPemFile());
			File trustedCert = getConfig().getContext().resolveFile(trustedCertsPem);
			boolean disableCertValidation = "DISABLED".equals(trustedCertsPem);

			String roleAttrName = getConfig().getContext().replaceVariables(getConfig().getRolesAttributeName());
			String usernameAttrName = getConfig().getContext().replaceVariables(getConfig().getUsernameAttributeName());
			URL discoveryUrl = new URL(getConfig().getContext().replaceVariables(getConfig().getImportDiscoveryJson()));

			String clientSecret = getConfig().getContext().replaceVariables(getConfig().getClientSecret());
			String clientId = getConfig().getContext().replaceVariables(getConfig().getClientId());

			client = new OpenIdConnectClient(discoveryUrl, callback, clientId, clientSecret, disableCertValidation, trustedCert, roleAttrName, usernameAttrName);
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
			String url = client.getOpenIDRedirectUrl();
			sendRedirect(request, response, url);
		}
	}

	@Override
	protected void serveLoginPartial(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException {
		serveLoginPage(request, response, exception);
	}

	@Override
	protected AbstractWebswingUser authenticate(HttpServletRequest request) throws WebswingAuthenticationException {
		String openIdCode = client.getCode(request);
		if (!StringUtils.isEmpty(openIdCode)) {
			try {
				AbstractWebswingUser user = client.getUser(openIdCode);
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
	public void doLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String logoutUrl = getConfig().getLogoutUrl();
		logoutRedirect(request, response, logoutUrl);
	}

}
