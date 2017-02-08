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
            URL callback = new URL(getConfig().getContext().replaceVariables(getConfig().getCallbackUrl()));

            String clientId = getConfig().getContext().replaceVariables(getConfig().getClientId());
            String clientSecret = getConfig().getContext().replaceVariables(getConfig().getClientSecret());

            String trustedCertsPem = getConfig().getContext().replaceVariables(getConfig().getTrustedPemFile());
            File trustedCert = getConfig().getContext().resolveFile(trustedCertsPem);
            boolean disableCertValidation = "DISABLED".equals(trustedCertsPem);

            String roleAttrName = getConfig().getContext().replaceVariables(getConfig().getRolesAttributeName());
            String userAttrName = getConfig().getContext().replaceVariables(getConfig().getUsernameAttributeName());

            String keycloakUrl = getConfig().getContext().replaceVariables(getConfig().getKeyCloakUrl());
            List<String> realms = getConfig().getKeyCloakRealms();
            if (realms.size() > 0) {
                this.defaultClient = realms.get(0);
                for (String realm : realms) {
                    URL discoveryURL = new URL(String.format("%1s/realms/%2s/.well-known/openid-configuration", keycloakUrl, realm));
                    URL realmCallback = new URL(CommonUtil.addParam(callback.toString(), REALM_PARAM + "=" + realm));
                    OpenIdConnectClient client = new OpenIdConnectClient(discoveryURL, realmCallback, clientId, clientSecret, disableCertValidation, trustedCert, roleAttrName, userAttrName);
                    clients.put(realm, client);
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
            OpenIdConnectClient realmClient = resolveClient(request);
            String url = realmClient.getOpenIDRedirectUrl();
            sendRedirect(request, response, url);
        }
    }

    private OpenIdConnectClient resolveClient(HttpServletRequest request) {
        Map<String, Object> requestData = getLoginRequest(request);
        String r = (String) requestData.get(REALM_PARAM);
        return r == null || clients.get(r) == null ? clients.get(defaultClient) : clients.get(r);
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
                OpenIdConnectClient realmClient = resolveClient(request);
                AbstractWebswingUser user = realmClient.getUser(openIdCode);
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
