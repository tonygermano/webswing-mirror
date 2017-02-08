package org.webswing.server.services.security.modules.saml2;

import main.Main;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.modules.AbstractExtendableSecurityModule;
import org.webswing.server.services.security.modules.saml2.com.lastpass.saml.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Saml2SecurityModule extends AbstractExtendableSecurityModule<Saml2SecurityModuleConfig> {
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

	private SAMLClient client;

	public Saml2SecurityModule(Saml2SecurityModuleConfig config) {
		super(config);
	}

	public void init() {
		super.init();
		if (!staticInit) {
			throw new RuntimeException("SAML2 module was not initialized correctly. Not possible to configure security module. ");
		}
		try {
			String idpFile = getConfig().getIdentityProviderMetadataFile();
			File file = getMetadataFile(idpFile);
			if (file == null || !file.isFile()) {
				throw new SAMLException("The SAML2 Identity provider metadata file " + idpFile + " does not exist.");
			}
			String consumerUrl = getConfig().getServiceProviderConsumerUrl();
			if (StringUtils.isEmpty(consumerUrl)) {
				throw new SAMLException("The SAML2 serviceProviderConsumerUrl property must not be empty.");
			}
			String entityId = getConfig().getServiceProviderEntityId();
			if (StringUtils.isEmpty(entityId)) {
				throw new RuntimeException("The SAML2 Service provider entityId property must not be empty.");
			}
			IdPConfig idpConfig = new IdPConfig(file);
			try {
				String spTemplate = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("saml2/saml2-sp-template.xml"));
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

	private File getMetadataFile(String idpFile) throws SAMLException {
		File metadata = getConfig().getContext().resolveFile(idpFile);
		if (metadata != null) {
			return metadata;
		} else {
			try {
				File tempFile = new File(Main.getTempDir(), Base64.encodeBase64URLSafeString(idpFile.getBytes()));
				FileUtils.copyURLToFile(new URL(idpFile), tempFile);
				return tempFile;
			} catch (MalformedURLException e) {
				return null;
			} catch (IOException e) {
				throw new SAMLException("Failed to load SAML2 Identity provider metadata.", e);
			}
		}
	}

	@Override
	protected void serveLoginPage(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException {
		if (exception != null) {
			sendHtml(request, response, "saml2/errorPage.html", exception);
		} else {
			String url = getSaml2RedirectUrl(request);
			sendRedirect(request, response, url);
		}
	}

	@Override
	protected void serveLoginPartial(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException {
		serveLoginPage(request, response, exception);
	}

	private String getSaml2RedirectUrl(HttpServletRequest request) throws IOException {
		String requestId = SAMLUtils.generateRequestId();
		String authrequest;
		try {
			authrequest = client.generateAuthnRequest(requestId);
		} catch (SAMLException e1) {
			throw new IOException("Failed to build SAML request.", e1);
		}
		String url = client.getIdPConfig().getLoginUrl();
		String searchQuery = request.getQueryString();
		if (StringUtils.isNotBlank(searchQuery)) {
			url = CommonUtil.addParam(url, searchQuery);
		}
		String param = "SAMLRequest=" + URLEncoder.encode(authrequest, "UTF-8");
		url = CommonUtil.addParam(url, param);
		return url;
	}

	@Override
	protected AbstractWebswingUser authenticate(HttpServletRequest request) throws WebswingAuthenticationException {
		String samlResponse = request.getParameter(SAML_PARAMETER);
		if (!StringUtils.isEmpty(samlResponse)) {
			AttributeSet aset;
			try {
				aset = client.validateResponse(samlResponse);
				String user = aset.getNameId();
				logSuccess(request, user);
				return new Saml2User(samlResponse, user, aset.getAttributes());
			} catch (SAMLException e1) {
				logFailure(request, null, "Failed to authenticate." + e1.getMessage());
				log.error("Failed to authenticate", e1);
				throw new WebswingAuthenticationException("Failed to auhenticate. " + e1.getMessage(), e1);
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
