package org.webswing.server.base;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.SwingConfig;
import org.webswing.server.common.model.admin.InstanceManagerStatus;
import org.webswing.server.common.model.admin.InstanceManagerStatus.Status;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.common.model.meta.VariableSetName;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.common.util.ConfigUtil;
import org.webswing.server.common.util.VariableSubstitutor;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.model.exception.WsInitException;
import org.webswing.server.services.config.ConfigurationChangeEvent;
import org.webswing.server.services.config.ConfigurationChangeListener;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.security.api.SecurityContext;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.api.WebswingSecurityConfig;
import org.webswing.server.services.security.login.SecuredPathHandler;
import org.webswing.server.services.security.modules.SecurityModuleService;
import org.webswing.server.services.security.modules.SecurityModuleWrapper;
import org.webswing.server.util.SecurityUtil;
import org.webswing.server.util.ServerUtil;
import org.webswing.toolkit.util.GitRepositoryState;

public abstract class PrimaryUrlHandler extends AbstractUrlHandler implements SecuredPathHandler, SecurityContext {
	private static final Logger log = LoggerFactory.getLogger(PrimaryUrlHandler.class);
	private static final String default_version = "unresolved";

	protected final ConfigurationService configService;
	protected final SecurityModuleService securityModuleService;

	private SecuredPathConfig config;
	private SecurityModuleWrapper securityModule;
	private InstanceManagerStatus status = new InstanceManagerStatus();
	protected VariableSubstitutor varSubs;

	public PrimaryUrlHandler(UrlHandler parent, SecurityModuleService securityModuleService, ConfigurationService configService) {
		super(parent);
		this.securityModuleService = securityModuleService;
		this.configService = configService;
		this.varSubs = VariableSubstitutor.basic();

		this.configService.registerChangeListener(new ConfigurationChangeListener() {

			@Override
			public void notifyChange(ConfigurationChangeEvent e) {
				if (StringUtils.isNotEmpty(e.getPath()) && !"/".equals(e.getPath())) {
					if (StringUtils.equals(e.getPath(), getPathMapping())) {
						if (StringUtils.equals(e.getNewConfig().getPath(), getPathMapping())) {
							config = e.getNewConfig();
							varSubs = VariableSubstitutor.forSwingApp(getConfig());
						}
					}
				}
			}
		});
	}

	@Override
	public void init() {
		try {
			status.setStatus(Status.Starting);
			varSubs = VariableSubstitutor.forSwingApp(getConfig());
			if (!new File(getHome()).getAbsoluteFile().isDirectory()) {//check if home dir exists
				throw new WsInitException("Home Folder '" + new File(getHome()).getAbsolutePath() + "'does not exist!");
			}
			loadSecurityModule();
			super.init();
			status.setStatus(Status.Running);
		} catch (Throwable e) {
			log.error("Failed to start '" + getPathMapping() + "'.", e);
			try {
				destroy();
			} catch (Throwable e1) {
				//do nothing
			}
			status.setStatus(Status.Error);
			status.setError(e.getMessage());
			StringWriter out = new StringWriter();
			PrintWriter stacktrace = new PrintWriter(out);
			e.printStackTrace(stacktrace);
			status.setErrorDetails(out.toString());
		}
	}

	protected void loadSecurityModule() {
		WebswingSecurityConfig securityConfig = getSecurityConfig();
		securityModule = securityModuleService.create(this, securityConfig);
		if (securityModule != null) {
			securityModule.init();
		}
	}

	protected WebswingSecurityConfig getSecurityConfig() {
		WebswingSecurityConfig securityConfig = getConfig().getValueAs("security", WebswingSecurityConfig.class);
		return securityConfig;
	}

	@Override
	public void destroy() {
		status.setStatus(Status.Stopping);
		super.destroy();
		if (securityModule != null) {
			try {
				securityModule.destroy();
			} finally {
				securityModule = null;
			}
		}
		status.setStatus(Status.Stopped);
	}

	@Override
	public boolean serve(HttpServletRequest req, HttpServletResponse res) throws WsException {
		handleCorsHeaders(req, res);
		if (status.getStatus().equals(Status.Running)) {
			//redirect to url that ends with '/' to ensure browser queries correct resources 
			if (req.getPathInfo() == null || (req.getContextPath() + req.getPathInfo()).equals(getFullPathMapping())) {
				try {
					String queryString = req.getQueryString() == null ? "" : ("?" + req.getQueryString());
					res.sendRedirect(getFullPathMapping() + "/" + queryString);
				} catch (IOException e) {
					log.error("Failed to redirect.", e);
				}
				return true;
			} else {
				return super.serve(req, res);
			}
		} else {
			try {
				boolean served = serveRestMethod(req, res);
				if (!served) {
					res.sendRedirect(getFullPathMapping() + "/status");
				}
				return true;
			} catch (IOException e) {
				throw new WsException(e);
			}
		}
	}

	private void handleCorsHeaders(HttpServletRequest req, HttpServletResponse res) {
		if (isOriginAllowed(req.getHeader("Origin"))) {
			if (req.getHeader("Origin") != null) {
				res.addHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
				res.addHeader("Access-Control-Allow-Credentials", "true");
				res.addHeader("Access-Control-Expose-Headers", Constants.HTTP_ATTR_ARGS + ", " + Constants.HTTP_ATTR_RECORDING_FLAG + ", X-Cache-Date, X-Atmosphere-tracking-id, X-Requested-With");
			}

			if ("OPTIONS".equals(req.getMethod())) {
				res.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
				res.addHeader("Access-Control-Allow-Headers", Constants.HTTP_ATTR_ARGS + ", " + Constants.HTTP_ATTR_RECORDING_FLAG + ", X-Requested-With, Origin, Content-Type, Content-Range, Content-Disposition, Content-Description, X-Atmosphere-Framework, X-Cache-Date, X-Atmosphere-tracking-id, X-Atmosphere-Transport");
				res.addHeader("Access-Control-Max-Age", "-1");
			}
		}
	}

	private boolean isOriginAllowed(String header) {
		List<String> allowedCorsOrigins = getSwingConfig().getAllowedCorsOrigins();
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

	public SecuredPathConfig getConfig() {
		if (config == null) {
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
	public boolean isStarted() {
		return Status.Running.equals(status.getStatus()) || Status.Starting.equals(status.getStatus());
	}

	public void start() throws WsException {
		checkMasterPermission(WebswingAction.rest_startApp);
		if (!isStarted()) {
			this.init();
		}
	}

	public void stop() throws WsException {
		checkMasterPermission(WebswingAction.rest_stopApp);
		if (this.isStarted()) {
			this.destroy();
		}
	}

	public String getVersion() throws WsException {
		String describe = GitRepositoryState.getInstance().getDescribe();
		if (describe == null) {
			return default_version;
		}
		return describe;
	}

	public MetaObject getMeta(Map<String, Object> json) throws WsException {
		checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);
		return configService.describeConfiguration(getPathMapping(), json, this);
	}

	public MetaObject getConfigMeta() throws WsException {
		checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);
		return configService.describeConfiguration(getPathMapping(), null, this);
	}

	public void setConfig(Map<String, Object> config) throws Exception {
		checkMasterPermission(WebswingAction.rest_setConfig);
		if (!isStarted()) {
			configService.setConfiguration(getPathMapping(), config);
		} else {
			throw new WsException("Can not set configuration to running handler.");
		}
	}

	public void setSwingConfig(Map<String, Object> config) throws Exception {
		checkMasterPermission(WebswingAction.rest_setConfig);
		configService.setSwingConfiguration(getPathMapping(), config);
	}

	protected Map<String, Boolean> getPermissions() throws Exception {
		Map<String, Boolean> permissions = new HashMap<>();
		permissions.put("dashboard", isPermited(WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo));
		permissions.put("configView", isPermited(WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_getConfig));
		permissions.put("configSwingEdit", isMasterPermited(WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_getConfig, WebswingAction.rest_setConfig));
		permissions.put("sessions", isPermited(WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_getSession));
		permissions.put("configEdit", false);
		permissions.put("start", false);
		permissions.put("stop", false);
		permissions.put("remove", false);
		permissions.put("logsView", false);
		return permissions;
	}

	public Map<String, String> getVariables(String type) throws WsException {
		checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);
		VariableSetName key;
		try {
			key = VariableSetName.valueOf(type.substring(1));
		} catch (Exception e) {
			key = VariableSetName.Basic;
		}
		VariableSubstitutor vs;
		switch (key) {
		case SwingInstance:
			String userName = getUser() == null ? "<webswing user>" : getUser().getUserId();
			vs = VariableSubstitutor.forSwingInstance(getConfig(), userName, "<webswing client Id>", "<webswing client IP address>", "<webswing client locale>", "<webswing custom args>");
			return vs.getVariableMap();
		case SwingApp:
			vs = VariableSubstitutor.forSwingApp(getConfig());
			return vs.getVariableMap();
		case Basic:
		default:
			vs = VariableSubstitutor.basic();
			return vs.getVariableMap();
		}
	}

	protected boolean isPermited(WebswingAction... actions) {
		for (WebswingAction action : actions) {
			boolean local = getUser() != null && getUser().isPermitted(action.name());
			if (!local) {
				boolean master = isMasterPermited(actions);
				if (!master) {
					return false;
				}
			}
		}
		return true;
	}

	protected boolean isMasterPermited(WebswingAction... actions) {
		for (WebswingAction action : actions) {
			boolean master = getMasterUser() != null && getMasterUser().isPermitted(action.name());
			if (!master) {
				return false;
			}
		}
		return true;
	}

	@Override
	public SecurityModuleWrapper get() {
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
		String webFolderPath = getConfig().getWebFolder();
		File webFolder = StringUtils.isEmpty(webFolderPath) ? null : resolveFile(webFolderPath);
		return ServerUtil.getWebResource(toPath(resource), getServletContext(), webFolder);
	}

	@Override
	public String replaceVariables(String string) {
		return varSubs.replace(string);
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
