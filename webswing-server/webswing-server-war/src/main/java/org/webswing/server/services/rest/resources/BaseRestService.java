package org.webswing.server.services.rest.resources;

import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.common.model.admin.ApplicationInfo;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.common.model.meta.VariableSetName;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.common.util.VariableSubstitutor;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.toolkit.util.GitRepositoryState;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseRestService {
	private static final String default_version = "unresolved";

	abstract PrimaryUrlHandler getHandler();

	abstract ConfigurationService getConfigService();

	@GET
	@Path("/apps")
	public List<ApplicationInfoMsg> getApps() throws WsException {
		getHandler().checkPermission(WebswingAction.rest_getApps);
		return getAppsImpl();
	}

	protected abstract List<ApplicationInfoMsg> getAppsImpl();

	@GET
	@Path("/info")
	public ApplicationInfo getAppInfo() throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getAppInfo);
		return getAppInfoImpl();
	}

	protected ApplicationInfo getAppInfoImpl() {
		PrimaryUrlHandler h = getHandler();
		ApplicationInfo app = new ApplicationInfo();
		app.setPath(h.getPathMapping());
		app.setEnabled(h.isEnabled());
		app.setUrl(h.getFullPathMapping());
		File icon = h.resolveFile(h.getConfig().getIcon());
		app.setIcon(CommonUtil.loadImage(icon));
		app.setConfig(h.getConfig());
		app.setVariables(h.getVariableMap());
		app.setStatus(h.getStatus());
		return app;
	}

	;

	@GET
	@Path("/rest/paths")
	public List<String> getPaths() throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getPaths);
		return getPathsImpl();
	}

	protected abstract List<String> getPathsImpl();

	@POST
	@Path("/rest/config")
	public void saveConfig(Map<String, Object> config) throws Exception {
		getHandler().checkMasterPermission(WebswingAction.rest_setConfig);
		saveConfigImpl(config);
	}

	protected void saveConfigImpl(Map<String, Object> config) throws Exception {
		getConfigService().setConfiguration(getHandler().getPathMapping(), config);
	}

	@GET
	@Path("/rest/config")
	public MetaObject getConfigMeta() throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);
		return getConfigService().describeConfiguration(getHandler().getPathMapping(), null, getHandler());
	}

	@GET
	@Path("/rest/variables/{type}")
	public Map<String, String> getVariables(@PathParam("type") String type) throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);
		VariableSetName key;
		try {
			key = VariableSetName.valueOf(type);
		} catch (Exception e) {
			key = VariableSetName.Basic;
		}
		VariableSubstitutor vs;
		switch (key) {
		case SwingInstance:
			String userName = getHandler().getUser() == null ? "<webswing user>" : getHandler().getUser().getUserId();
			vs = VariableSubstitutor.forSwingInstance(getHandler().getConfig(), userName, null, "<webswing client Id>", "<webswing client IP address>", "<webswing client locale>", "<webswing custom args>");
			return vs.getVariableMap();
		case SwingApp:
			vs = VariableSubstitutor.forSwingApp(getHandler().getConfig());
			return vs.getVariableMap();
		case Basic:
		default:
			vs = VariableSubstitutor.basic();
			return vs.getVariableMap();
		}
	}

	@POST
	@Path("/rest/metaConfig")
	public MetaObject getMeta(Map<String, Object> json) throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);
		return getConfigService().describeConfiguration(getHandler().getPathMapping(), json, getHandler());
	}

	@GET
	@Path("/rest/permissions")
	public Map<String, Boolean> getPermissions() throws Exception {
		return getPermissionsImpl();
	}

	protected Map<String, Boolean> getPermissionsImpl() throws Exception {
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

	@GET
	@Path("/rest/version")
	@Produces(MediaType.TEXT_PLAIN)
	public String getVersion() throws WsException {
		String describe = GitRepositoryState.getInstance().getDescribe();
		if (describe == null) {
			return default_version;
		}
		return describe;
	}

	@GET
	@Path("/rest/CSRFToken")
	@Produces(MediaType.TEXT_PLAIN)
	public String generateCsrfToken() {
		return getHandler().generateCsrfToken();
	}

	@GET
	@Path("/rest/ping")
	public void ping() {
		//responds with 200 OK status
	}

	protected boolean isPermited(WebswingAction... actions) {
		for (WebswingAction action : actions) {
			boolean local = getHandler().getUser() != null && getHandler().getUser().isPermitted(action.name());
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
			boolean master = getHandler().getMasterUser() != null && getHandler().getMasterUser().isPermitted(action.name());
			if (!master) {
				return false;
			}
		}
		return true;
	}

}
