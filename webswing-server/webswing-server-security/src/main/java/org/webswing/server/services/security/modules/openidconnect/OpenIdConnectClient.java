package org.webswing.server.services.security.modules.openidconnect;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.auth.openidconnect.IdTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.commons.lang.StringUtils;
import org.webswing.server.services.security.api.AbstractWebswingUser;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

/**
 * Created by vikto on 06-Feb-17.
 */
public class OpenIdConnectClient {

	public static final String CODE = "code";
	public static final String ISSUER = "issuer";
	public static final String AUTHORIZATION_ENDPOINT = "authorization_endpoint";
	public static final String TOKEN_ENDPOINT = "token_endpoint";
	public static final String OPENID_SCOPE = "openid profile";
	public static final String CLIENT_ID = "client_id";

	private final String roleAttrName;
	private final String usernameAttrName;
	private URL callback;
	private AuthorizationCodeFlow flow;

	public OpenIdConnectClient(URL discovery, URL callback, String clientId, String clientSecret, boolean disableCertValidation, File trustedCert, String roleAttrName, String usernameAttrName) throws Exception {
		this.callback = callback;
		this.roleAttrName = roleAttrName;
		this.usernameAttrName = usernameAttrName;

		JacksonFactory jsonFactory = new JacksonFactory();
		NetHttpTransport.Builder transportBuilder = new NetHttpTransport.Builder();
		if (disableCertValidation) {
			transportBuilder.doNotValidateCertificate();
		}
		if (trustedCert != null) {
			transportBuilder.trustCertificatesFromStream(new FileInputStream(trustedCert));
		}

		URL authURI;
		URL tokenURI;
		String issuer;
		if (discovery != null) {
			HttpResponse response = transportBuilder.build().createRequestFactory().buildGetRequest(new GenericUrl(discovery)).execute();
			Map<String, Object> json = jsonFactory.createJsonParser(response.getContent()).parse(Map.class);
			if (json.get(ISSUER) != null) {
				issuer = (String) json.get(ISSUER);
			} else {
				throw new RuntimeException("Discovery json does not define issuer field");
			}
			if (json.get(AUTHORIZATION_ENDPOINT) != null) {
				authURI = new URL((String) json.get(AUTHORIZATION_ENDPOINT));
			} else {
				throw new RuntimeException("Discovery json does not define authorization_endpoint field");
			}
			if (json.get(TOKEN_ENDPOINT) != null) {
				tokenURI = new URL((String) json.get(TOKEN_ENDPOINT));
			} else {
				throw new RuntimeException("Discovery json does not define token_endpoint field");
			}
		} else {
			throw new RuntimeException("OpenID Connect Discovery URL is not defined.");
		}

		ClientParametersAuthentication auth = null;

		if (StringUtils.isNotBlank(clientSecret)) {
			auth = new ClientParametersAuthentication(clientId, clientSecret);
		}
		Credential.AccessMethod method = BearerToken.authorizationHeaderAccessMethod();

		AuthorizationCodeFlow.Builder builder = new AuthorizationCodeFlow.Builder(method, transportBuilder.build(), jsonFactory, new GenericUrl(tokenURI), auth, clientId, authURI.toString());
		builder.setScopes(Collections.singletonList(OPENID_SCOPE));
		flow = builder.build();
	}

	public String getOpenIDRedirectUrl() throws IOException {
		return flow.newAuthorizationUrl().setRedirectUri(callback.toString()).build();
	}

	public static String getCode(HttpServletRequest request) {
		String query = request.getQueryString();
		if (query == null)
			return null;
		String[] params = query.split("&");
		for (String param : params) {
			int eq = param.indexOf('=');
			if (eq == -1)
				continue;
			String name = param.substring(0, eq);
			if (!name.equals(CODE))
				continue;
			return param.substring(eq + 1);
		}
		return null;
	}

	private static IdToken executeIdToken(TokenRequest tokenRequest) throws IOException {
		IdTokenResponse idTokenResponse = IdTokenResponse.execute(tokenRequest);
		return idTokenResponse.parseIdToken();
	}

	public AbstractWebswingUser getUser(String openIdCode) throws IOException {
		AuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(openIdCode).setRedirectUri(callback.toString());
		tokenRequest.set(CLIENT_ID, flow.getClientId());
		IdToken result = executeIdToken(tokenRequest);
		OpenIdWebswingUser user = new OpenIdWebswingUser(result, this.usernameAttrName, this.roleAttrName);
		return user;
	}
}
