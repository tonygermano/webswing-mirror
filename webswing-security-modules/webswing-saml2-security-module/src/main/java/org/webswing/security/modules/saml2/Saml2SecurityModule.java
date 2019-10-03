package org.webswing.security.modules.saml2;

import main.Main;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.credentials.authenticator.SAML2Authenticator;
import org.pac4j.saml.profile.SAML2Profile;
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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class Saml2SecurityModule extends AbstractExtendableSecurityModule<Saml2SecurityModuleConfig> {
	private static final Logger log = LoggerFactory.getLogger(Saml2SecurityModule.class);
	private static final String SP_METADATA = "metadata";
	private static final String SAML_PARAMETER = "SAMLResponse";


	private SAML2Client client;
	private SessionStore store;
	private String userAttributeName;
	private String rolesAttributeName;

	public Saml2SecurityModule(Saml2SecurityModuleConfig config) {
		super(config);
	}

	public void init() {
		super.init();
		try {
			SAML2Configuration cfg = new SAML2Configuration();

			String idpFile = getConfig().getIdentityProviderMetadataFile();
			File file = getFile(idpFile);
			if (file == null || !file.isFile()) {
				throw new SAMLException("The SAML2 Identity provider metadata file " + idpFile + " does not exist.");
			}
			cfg.setIdentityProviderMetadataResourceFilepath(file.getAbsolutePath());

			String consumerUrl = getConfig().getContext().replaceVariables(getConfig().getServiceProviderConsumerUrl());
			if (StringUtils.isEmpty(consumerUrl)) {
				throw new SAMLException("The SAML2 serviceProviderConsumerUrl property must not be empty.");
			}

			String entityId = getConfig().getContext().replaceVariables(getConfig().getServiceProviderEntityId());
			if (StringUtils.isEmpty(entityId)) {
				throw new RuntimeException("The SAML2 Service provider entityId property must not be empty.");
			}
			cfg.setServiceProviderEntityId(entityId);

			String keyStoreAlias = getConfig().getContext().replaceVariables(getConfig().getDecryptionKeyAlias());
			String keyStorePwd = getConfig().getContext().replaceVariables(getConfig().getKeyStorePwd());
			String keyPwd = getConfig().getContext().replaceVariables(getConfig().getKeyPwd());
			cfg.setKeystorePassword(keyStorePwd);
			cfg.setKeystoreAlias(keyStoreAlias);
			cfg.setPrivateKeyPassword(keyPwd);

			String keyStore = getConfig().getContext().replaceVariables(getConfig().getKeyStore());
			cfg.setKeystoreResourceFilepath(keyStore);

			cfg.setAuthnRequestSigned(getConfig().isAuthnRequestSigned());

			cfg.setNameIdPolicyFormat(getConfig().getNameIdPolicyFormat());

			cfg.setAuthnRequestBindingType(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
			cfg.setSpLogoutRequestBindingType(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
			cfg.setSpLogoutResponseBindingType(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
			cfg.setLogoutHandler(new LogoutHandler<WebContext>() {
				public void destroySessionFront(WebContext context, String key) {
					// do nothing by default
				}

				public void destroySessionBack(WebContext context, String key) {
					// do nothing by default
				}
			});
			cfg.setMaximumAuthenticationLifetime(28800);//8hours -ADFS compatible value
			cfg.setUseNameQualifier(false);//ADFS compatibility setting

			store = new Saml2SessionStore(getConfig().getContext());

			try {
				client = new SAML2Client(cfg);

				//allow merging attributes (Keycloak sending roles in separate saml attributes)
				SAML2Authenticator authenticator = new SAML2Authenticator(cfg.getAttributeAsId());
				authenticator.setProfileDefinition(new CommonProfileDefinition<>(p -> {
					SAML2Profile defaultProfile = new SAML2Profile(true);
					return defaultProfile;

				}));
				client.setAuthenticator(authenticator);

				client.setCallbackUrl(consumerUrl);
				client.init();

				this.userAttributeName = getConfig().getContext().replaceVariables(getConfig().getUserAttributeName());
				this.rolesAttributeName = getConfig().getContext().replaceVariables(getConfig().getRolesAttributeName());
			} catch (Exception e) {
				throw new SAMLException("The SAML2 client initialization failed.", e);
			}
		} catch (SAMLException e) {
			throw new RuntimeException("Failed to initialize SAML2 webswing security module. ", e);
		}
	}

	private File getFile(String fileName) throws SAMLException {
		File file = getConfig().getContext().resolveFile(fileName);
		if (file != null) {
			return file;
		} else {
			try {
				File tempFile = new File(Main.getTempDir(), Base64.encodeBase64URLSafeString(fileName.getBytes()));
				FileUtils.copyURLToFile(new URL(fileName), tempFile);
				return tempFile;
			} catch (MalformedURLException e) {
				return null;
			} catch (IOException e) {
				throw new SAMLException("Invalid SAML2 configuration. Failed to load file '" + fileName + "'", e);
			}
		}
	}

	@Override
	protected void serveLoginPartial(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException {
		if (exception != null) {
			sendPartialHtml(request, response, "errorPartial.html", exception);
		} else {
			WebContext context = new Saml2WebContext(request, response, store);
			String url = client.getRedirectActionBuilder().redirect(context).getLocation();
			URIBuilder urlBuilder = null;
			try {
				urlBuilder = new URIBuilder(url);
				List<NameValuePair> originalParams = urlBuilder.getQueryParams();
				urlBuilder.clearParameters();
				for(NameValuePair param:originalParams) {
					if (!getConfig().isAuthnRequestSigned() &&
							("SigAlg".equals(param.getName())||"Signature".equals(param.getName()))) {
						continue;
					}else {
						urlBuilder.addParameter(param.getName(),param.getValue());
					}
				}
				sendRedirect(request, response, urlBuilder.build().toString());
			} catch (URISyntaxException e) {
				log.error("failed to parse SAML2 redirect url:"+url,e);
				sendRedirect(request, response, url);
			}
		}
	}

	@Override
	protected AbstractWebswingUser authenticate(HttpServletRequest request) throws WebswingAuthenticationException {
		String samlResponse = request.getParameter(SAML_PARAMETER);
		if (!StringUtils.isEmpty(samlResponse)) {
			try {
				WebContext webCtx = new Saml2WebContext(request, store);
				SAML2Credentials cred = client.getCredentials(webCtx);
				SAML2Profile profile = client.getUserProfile(cred, webCtx);
				logSuccess(request, profile.getId());
				return new Saml2User(profile, profile.getId(), profile.getAttributes(), userAttributeName, rolesAttributeName);
			} catch (Exception e1) {
				if(e1 instanceof HttpAction && ((HttpAction) e1).getCode()==200){
					return null;
				}
				logFailure(request, null, "Failed to authenticate." + e1.getMessage());
				log.error("Failed to authenticate", e1);
				throw new WebswingAuthenticationException("Failed to auhenticate. " + e1.getMessage(), WebswingAuthenticationException.FAILED_TO_AUTHENTICATE, e1);
			}
		}
		return null;
	}

	@Override
	public AbstractWebswingUser doLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String metadata = request.getParameter(SP_METADATA);
		if (metadata != null) {
			serveSpMetadata(request, response);
			return null;
		}
		return super.doLogin(request, response);
	}

	@Override
	public void doServeAuthenticated(AbstractWebswingUser user, String path, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String metadata = request.getParameter(SP_METADATA);
		if (metadata != null) {
			serveSpMetadata(request, response);
		}
		super.doServeAuthenticated(user, path, request, response);
	}

	private void serveSpMetadata(HttpServletRequest request, HttpServletResponse response) {
		J2EContext ctx = new J2EContext(request, response);
		try {
			String content = client.getServiceProviderMetadataResolver().getMetadata();
			ctx.setResponseStatus(HttpStatus.SC_OK);
			ctx.setResponseContentType("application/xml");
			ctx.writeResponseContent(content);
		} catch (IOException e) {
			ctx.setResponseStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			ctx.writeResponseContent("Failed to generate SP metadata xml");
			log.error("Failed to generate SP metadata xml", e);
		}
	}

	@Override
	public void doLogout(HttpServletRequest request, HttpServletResponse response, AbstractWebswingUser user) throws ServletException, IOException {
		if (getConfig().isSingleLogout() && user instanceof Saml2User) {
			WebContext context = new Saml2WebContext(request, response, store);
			String url = client.getLogoutActionBuilder().getLogoutAction(context, ((Saml2User) user).getProfile(), null).getLocation();
			sendRedirect(request, response, url);
		} else {
			String logoutUrl = replaceVar(getConfig().getLogoutUrl());
			logoutRedirect(request, response, logoutUrl);
		}
	}
}
