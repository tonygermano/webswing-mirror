package org.webswing.security.modules.openidconnect;

import org.apache.commons.lang3.StringUtils;
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
import java.util.UUID;

public class OpenIDConnectSecurityModule extends AbstractExtendableSecurityModule<OpenIDConnectSecurityModuleConfig> {
	private static final Logger log = LoggerFactory.getLogger(OpenIDConnectSecurityModule.class);
	private static final String OIDC_STATE = "OpenIdConnectSessionState";

	private OpenIdConnectClient client;

	public OpenIDConnectSecurityModule(OpenIDConnectSecurityModuleConfig config) {
		super(config);
	}

	public void init() {
		super.init();
		try {
			URL callback = new URL(replaceVar(getConfig().getCallbackUrl()));

			String trustedCertsPem = replaceVar(getConfig().getTrustedPemFile());
			File trustedCert = getConfig().getContext().resolveFile(trustedCertsPem);
			boolean disableCertValidation = "DISABLED".equals(trustedCertsPem);

			String roleAttrName = replaceVar(getConfig().getRolesAttributeName());
			String usernameAttrName = replaceVar(getConfig().getUsernameAttributeName());
			URL discoveryUrl = new URL(replaceVar(getConfig().getImportDiscoveryJson()));

			String clientSecret = replaceVar(getConfig().getClientSecret());
			String clientId = replaceVar(getConfig().getClientId());

			client = new OpenIdConnectClient(discoveryUrl, callback, clientId, clientSecret, disableCertValidation, trustedCert, roleAttrName, usernameAttrName);
		} catch (Exception e) {
			log.error("Initializing of OpenID Connect client failed.", e);
			throw new RuntimeException("Initializing of OpenID Connect client failed.", e);
		}
	}

	@Override
	protected void serveLoginPartial(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException {
		String state = UUID.randomUUID().toString().substring(0,7);
		getConfig().getContext().setToSecuritySession(OIDC_STATE, state);
		String url = client.getOpenIDRedirectUrl(state);
		if (exception != null || url == null) {
			sendHtml(request, response, "errorPartial.html", exception);
		} else {
			sendRedirect(request, response, url);
		}
	}

	@Override
	protected AbstractWebswingUser authenticate(HttpServletRequest request) throws WebswingAuthenticationException {
		String openIdCode = client.getCode(request);
		if (!StringUtils.isEmpty(openIdCode)) {
			try {
				String expectedState = (String) getConfig().getContext().getFromSecuritySession(OIDC_STATE);
				client.validateCodeRequest(request,expectedState);
				AbstractWebswingUser user = client.getUser(openIdCode, null);
				logSuccess(request, user.getUserId());
				return user;
			} catch (Exception e1) {
				logFailure(request, null, "Failed to authenticate." + e1.getMessage());
				log.error("Failed to authenticate", e1);
				throw new WebswingAuthenticationException("Failed to authenticate. " + e1.getMessage(), WebswingAuthenticationException.FAILED_TO_AUTHENTICATE, e1);
			}finally {
				//reset the state value
				getConfig().getContext().setToSecuritySession(OIDC_STATE,null);
			}
		}
		return null;
	}

	@Override
	public void doLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String logoutUrl = replaceVar(getConfig().getLogoutUrl());
		logoutRedirect(request, response, logoutUrl);
	}

}
