package org.webswing.server.base;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.SwingConfig;
import org.webswing.server.common.model.admin.InstanceManagerStatus;
import org.webswing.server.common.model.admin.InstanceManagerStatus.Status;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.common.util.ConfigUtil;
import org.webswing.server.common.util.VariableSubstitutor;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.model.exception.WsInitException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.security.api.SecurityContext;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.api.WebswingSecurityConfig;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.login.SecuredPathHandler;
import org.webswing.server.services.security.modules.SecurityModuleService;
import org.webswing.server.services.swingmanager.SwingInstanceManager;
import org.webswing.server.util.SecurityUtil;
import org.webswing.server.util.ServerUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

public abstract class PrimaryUrlHandler extends AbstractUrlHandler implements SecuredPathHandler, SecurityContext {
	private static final Logger log = LoggerFactory.getLogger(PrimaryUrlHandler.class);
	private static final String default_version = "unresolved";
	public static final String JAVASCRIPT_NLS_PREFIX = "/javascript/nls";

	protected final ConfigurationService configService;
	protected final SecurityModuleService securityModuleService;

	private SecuredPathConfig config;
	private WebswingSecurityModule securityModule;
	private boolean enabled = false;
	private InstanceManagerStatus status = new InstanceManagerStatus();
	protected VariableSubstitutor varSubs;

	public PrimaryUrlHandler(UrlHandler parent, SecurityModuleService securityModuleService, ConfigurationService configService) {
		super(parent);
		this.securityModuleService = securityModuleService;
		this.securityModule = securityModuleService.createNoAccess(null, this, null);//no access until real SM is initialized
		this.configService = configService;
		this.varSubs = VariableSubstitutor.basic();
	}

	@Override
	public void init() {
		try {
			super.init();
			if (getConfig().isEnabled()) {
				initConfiguration();
			} else {
				disable();
			}
		} catch (Throwable e) {
			log.error("Failed to start '" + getPathMapping() + "'.", e);
			try {
				destroy();
			} catch (Throwable e1) {
				//do nothing
			}
			setStatusError(e);
		}
	}

	public abstract  List<SwingInstanceManager> getApplications();

 	public synchronized void initConfiguration() {
		status.setStatus(Status.Starting);
		String path = StringUtils.isEmpty(getPathMapping()) ? "/" : getPathMapping();
		config = configService.getConfiguration(path);
		WebswingSecurityConfig securityConfig = getSecurityConfig();
		try {
			varSubs = VariableSubstitutor.forSwingApp(getConfig());
			if (!new File(getHome()).getAbsoluteFile().isDirectory()) {//check if home dir exists
				throw new WsInitException("Home Folder '" + new File(getHome()).getAbsolutePath() + "'does not exist!");
			}
			try {
				if (securityModule != null) {
					securityModule.destroy();
				}
			} catch (Exception e) {
				log.error("Failed to destroy Security module for " + path + ".", e);
			}
			securityModule = securityModuleService.create(this, securityConfig);
			if (securityModule != null) {
				securityModule.init();
			}
			status.setStatus(Status.Running);
			enabled = true;
		} catch (Exception e) {
			securityModule = securityModuleService.createNoAccess(WebswingAuthenticationException.CONFIG_ERROR, this, securityConfig);
			setStatusError(e);
		}
	}

	public synchronized void disable() {
		status.setStatus(Status.Stopping);
		enabled = false;
		try {
			killAll();
			if (securityModule != null) {
				securityModule.destroy();
			}
		} finally {
			this.securityModule = securityModuleService.createNoAccess(null, this, null);//no access until real SM is initialized
		}
		status.setStatus(Status.Stopped);
	}

	protected void killAll() {//to be implemented in subclass
	}

	private void setStatusError(Throwable e) {
		status.setStatus(Status.Error);
		status.setError(e.getMessage());
		StringWriter out = new StringWriter();
		PrintWriter stacktrace = new PrintWriter(out);
		e.printStackTrace(stacktrace);
		status.setErrorDetails(out.toString());
	}

	protected WebswingSecurityConfig getSecurityConfig() {
		return getConfig().getValueAs("security", WebswingSecurityConfig.class);
	}

	@Override
	public void destroy() {
		super.destroy();
		disable();
	}

	@Override
	public boolean serve(HttpServletRequest req, HttpServletResponse res) throws WsException {
		handleCorsHeaders(req, res);
		//redirect to url that is correct case and ends with '/' to ensure browser queries correct resources
		if (isWrongUrlCase(req) || isRootPathWithoutSlash(req)) {
			try {
				String redirectUrl = getFullPathMapping() + (ServerUtil.getContextPath(getServletContext()) + toPath(req.getPathInfo())).substring(getFullPathMapping().length());
				redirectUrl = isRootPathWithoutSlash(req) ? (redirectUrl + "/") : redirectUrl;
				//use apache flag instead: 	ProxyPreserveHost on
				//if (System.getProperty(Constants.REVERSE_PROXY_CONTEXT_PATH) != null) { //reverse proxy will add the context path to redirect url so we need to remove it to avoid duplicate. see ProxyPassReverse apache directive
				//	redirectUrl = redirectUrl.substring(ServerUtil.getContextPath(getServletContext()).length());
				//}
				String queryString = req.getQueryString() == null ? "" : ("?" + req.getQueryString());
				ServerUtil.sendHttpRedirect(req, res, redirectUrl + queryString);
			} catch (IOException e) {
				log.error("Failed to redirect.", e);
			}
			return true;
		} else {
			return super.serve(req, res);
		}
	}

	@Override
	public boolean isSubPath(String subpath, String path) {
		return CommonUtil.isSubPathIgnoreCase(subpath, path);
	}

	private boolean isWrongUrlCase(HttpServletRequest req) {
		if (req.getPathInfo() == null) {
			return false;
		}
		String relevantUrlPart = toPath(req.getPathInfo()).substring(0, getPathMapping().length());
		boolean isWrongCase = !relevantUrlPart.equals(getPathMapping()) && relevantUrlPart.equalsIgnoreCase(getPathMapping()); // if this handler path is not correct case
		return isWrongCase;
	}

	private boolean isRootPathWithoutSlash(HttpServletRequest req) {
		boolean isRootPathWithoutSlash = (ServerUtil.getContextPath(getServletContext()) + req.getPathInfo()).equals(getFullPathMapping());//path has to end with '/' 
		return req.getPathInfo() == null || isRootPathWithoutSlash;
	}

	private void handleCorsHeaders(HttpServletRequest req, HttpServletResponse res) throws WsException {
		if (isOriginAllowed(req.getHeader("Origin"))) {
			if (req.getHeader("Origin") != null) {
				res.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
				res.setHeader("Access-Control-Allow-Credentials", "true");
				res.setHeader("Access-Control-Expose-Headers", Constants.HTTP_ATTR_ARGS + ", " + Constants.HTTP_ATTR_RECORDING_FLAG + ", X-Cache-Date, X-Atmosphere-tracking-id, X-Requested-With");
			}

			if ("OPTIONS".equals(req.getMethod())) {
				res.setHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST, DELETE");
				res.setHeader("Access-Control-Allow-Headers", Constants.HTTP_ATTR_ARGS + ", " + Constants.HTTP_ATTR_RECORDING_FLAG + ", X-Requested-With, Origin, Content-Type, Content-Range, Content-Disposition, Content-Description, X-Atmosphere-Framework, X-Cache-Date, X-Atmosphere-tracking-id, X-Atmosphere-Transport");
				res.setHeader("Access-Control-Max-Age", "-1");
			}
		}
	}

	public boolean isOriginAllowed(String header) {
		List<String> allowedCorsOrigins = getConfig().getAllowedCorsOrigins();
		if (allowedCorsOrigins == null || allowedCorsOrigins.size() == 0) {
			return false;
		}
		for (String s : allowedCorsOrigins) {
			if (s.trim().equals(header) || s.trim().equals("*")) {
				return true;
			}
		}
		return false;
	}

	public boolean isSameOrigin(HttpServletRequest req) {
		String origin = req.getHeader("Origin");
		String host = req.getHeader("X-Forwarded-Host");
		if(origin==null){
			return true; //IE11 on Win7 does not send Origin header
		}
		if (host == null) {
			host = req.getHeader("Host");
		}
		if (origin != null && host != null) {
			String originHost = origin.indexOf("://") >= 0 ? origin.substring(origin.indexOf("://") + 3) : origin;
			if (StringUtils.equals(originHost, host)) {
				return true;
			}
		}
		return false;
	}

	public SecuredPathConfig getConfig() {
		if (config == null || !enabled) {
			String path = StringUtils.isEmpty(getPathMapping()) ? "/" : getPathMapping();
			config = configService.getConfiguration(path);
		}
		return config;
	}

	public SwingConfig getSwingConfig() {
		if (getConfig().getSwingConfig() == null) {
			return ConfigUtil.instantiateConfig(null, SwingConfig.class);
		} else {
			return getConfig().getSwingConfig();
		}
	}

	public InstanceManagerStatus getStatus() {
		return status;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public WebswingSecurityModule get() {
		return securityModule;
	}

	@Override
	public File resolveFile(String name) {
		String home = getHome();
		return CommonUtil.resolveFile(name, home, varSubs);
	}

	private String getHome() {
		return VariableSubstitutor.basic().replace(getConfig().getHomeDir());
	}

	@Override
	public URL getWebResource(String resource) {
		if (toPath(resource).startsWith(JAVASCRIPT_NLS_PREFIX)) {
			String langs = getConfig().getLangFolder();
			File langsFolder = StringUtils.isEmpty(langs) ? null : resolveFile(langs);
			URL result = ServerUtil.getFileResource(toPath(resource).substring(JAVASCRIPT_NLS_PREFIX.length()), langsFolder);
			if (result != null) {
				return result;
			}
		}
		String webFolderPath = getConfig().getWebFolder();
		File webFolder = StringUtils.isEmpty(webFolderPath) ? null : resolveFile(webFolderPath);
		return ServerUtil.getWebResource(toPath(resource), getServletContext(), webFolder);
	}

	public String generateCsrfToken() {
		String token = (String) getFromSecuritySession(Constants.HTTP_ATTR_CSRF_TOKEN_HEADER);
		if (token == null) {
			SecureRandom random = new SecureRandom();
			byte[] values = new byte[32];
			random.nextBytes(values);
			token = Base64.encodeBase64String(values);
			setToSecuritySession(Constants.HTTP_ATTR_CSRF_TOKEN_HEADER, token);
		}
		return token;
	}

	public boolean validateCsrfToken(HttpServletRequest req) {
		String token = (String) getFromSecuritySession(Constants.HTTP_ATTR_CSRF_TOKEN_HEADER);
		if (token != null) {
			String header = req.getParameter(Constants.HTTP_ATTR_CSRF_TOKEN_HEADER);
			if (StringUtils.equals(header, token)) {
				return true;
			}
		}
		return false;

	}

	@Override
	public String replaceVariables(String string) {
		return varSubs.replace(string);
	}

	public Map<String, String> getVariableMap() {
		return varSubs.getVariableMap();
	}

	@Override
	public Object getFromSecuritySession(String attributeName) {
		return SecurityUtil.getFromSecuritySession(attributeName);
	}

	@Override
	public void setToSecuritySession(String attributeName, Object value) {
		SecurityUtil.setToSecuritySession(attributeName, value);
	}

}
