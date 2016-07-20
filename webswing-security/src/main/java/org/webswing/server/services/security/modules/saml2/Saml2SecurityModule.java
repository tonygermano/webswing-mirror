package org.webswing.server.services.security.modules.saml2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.modules.saml2.com.lastpass.saml.AttributeSet;
import org.webswing.server.services.security.modules.saml2.com.lastpass.saml.IdPConfig;
import org.webswing.server.services.security.modules.saml2.com.lastpass.saml.SAMLClient;
import org.webswing.server.services.security.modules.saml2.com.lastpass.saml.SAMLException;
import org.webswing.server.services.security.modules.saml2.com.lastpass.saml.SAMLInit;
import org.webswing.server.services.security.modules.saml2.com.lastpass.saml.SAMLUtils;
import org.webswing.server.services.security.modules.saml2.com.lastpass.saml.SPConfig;

public class Saml2SecurityModule implements WebswingSecurityModule<Saml2Credentials> {
	private static final Logger log = LoggerFactory.getLogger(Saml2SecurityModule.class);
	private static boolean staticInit = false;
	private static final String SAML_PARAMETER = "SAMLResponse";
	static {
		try {
			SAMLInit.initialize();
			staticInit = true;
		} catch (SAMLException e) {
			log.error("Initializing SAML2 client failed.", e);
		}
	}

	private Saml2SecurityModuleConfig config;

	private SAMLClient client;

	public Saml2SecurityModule(Saml2SecurityModuleConfig config) {
		this.config = config;
	}

	public void init() {
		if (!staticInit) {
			throw new RuntimeException("SAML2 module was not initialized correctly. Not possible to configure security module. ");
		}
		try {
			String idpFile = config.getIdentityProviderMetadataFile();
			File file = config.getContext().resolveFile(idpFile);
			if (file == null || !file.isFile()) {
				throw new SAMLException("The SAML2 Identity provider metadata file " + idpFile + " does not exist.");
			}
			String consumerUrl = config.getServiceProviderConsumerUrl();
			if (StringUtils.isEmpty(consumerUrl)) {
				throw new SAMLException("The SAML2 serviceProviderConsumerUrl property must not be empty.");
			}
			String entityId = config.getServiceProviderEntityId();
			if (StringUtils.isEmpty(entityId)) {
				throw new RuntimeException("The SAML2 Service provider entityId property must not be empty.");
			}
			IdPConfig idpConfig = new IdPConfig(file);
			try {
				String spTemplate = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("saml2-sp-template.xml"));
				spTemplate = spTemplate.replace("${entityID}", entityId);
				spTemplate = spTemplate.replace("${consumerUrl}", consumerUrl);
				SPConfig spConfig = new SPConfig(new ByteArrayInputStream(spTemplate.getBytes("UTF-8")));
				client = new SAMLClient(spConfig, idpConfig);
			} catch (IOException e) {
				throw new SAMLException("The SAML2 template file could not be loaded.", e);
			}
		} catch (SAMLException e) {
			throw new RuntimeException("Failed to initialize SAML2 webswing security module. ", e);
		}
	}

	public Saml2Credentials getCredentials(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException e) throws ServletException, IOException {
		String samlResponse = request.getParameter(SAML_PARAMETER);
		if (StringUtils.isEmpty(samlResponse)) {
			String requestId = SAMLUtils.generateRequestId();
			String authrequest;
			try {
				authrequest = client.generateAuthnRequest(requestId);
			} catch (SAMLException e1) {
				throw new IOException("Failed to build SAML request.", e1);
			}
			String url = client.getIdPConfig().getLoginUrl();
			String param = "SAMLRequest=" + URLEncoder.encode(authrequest, "UTF-8");
			if (url.contains("?")) {
				url = url + "&" + param;
			} else {
				url = url + "?" + param;
			}
			response.sendRedirect(url);
			return null;
		} else {
			AttributeSet aset;
			try {
				aset = client.validateResponse(samlResponse);
				String user = aset.getNameId();
				return new Saml2Credentials(samlResponse, user, aset.getAttributes());
			} catch (SAMLException e1) {
				throw new IOException("Failed to auhenticate.", e1);
			}
		}
	}

	public AbstractWebswingUser getUser(Saml2Credentials credentials) throws WebswingAuthenticationException {
		return new Saml2WebswingUser(credentials);
	}

	public void destroy() {
	}

}
