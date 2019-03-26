package org.webswing.server.services.rest.resources;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.common.model.admin.ApplicationInfo;
import org.webswing.server.common.model.admin.BasicApplicationInfo;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.common.model.meta.VariableSetName;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.common.util.VariableSubstitutor;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.login.SecuredPathHandler;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.toolkit.util.GitRepositoryState;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.*;

public abstract class BaseRestService {
	private static final String default_version = "unresolved";

	protected abstract PrimaryUrlHandler getHandler();

	protected abstract ConfigurationService getConfigService();

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
		app.setUrl(h.getFullPathMapping());
		app.setEnabled(h.isEnabled());
		app.setConfig(h.getConfig());
		app.setVariables(h.getVariableMap());
		app.setStatus(h.getStatus());
		return app;
	}

	@GET
	@Path("/rest/paths")
	public List<BasicApplicationInfo> getPaths() throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getPaths);
		return getPathsImpl();
	}

	protected abstract List<BasicApplicationInfo> getPathsImpl();

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

	private VariableSubstitutor getVariableSubstitutorByType(String type) {

		VariableSetName variableType;

		try {
			variableType = VariableSetName.valueOf(type);
		} catch (Exception e) {
			variableType = VariableSetName.Basic;
		}

		VariableSubstitutor variableSubstitutor;

		switch (variableType) {

		case SwingInstance:
			String userName = getHandler().getUser() == null ? "<webswing user>" : getHandler().getUser().getUserId();
			variableSubstitutor = VariableSubstitutor.forSwingInstance(getHandler().getConfig(), userName, null, "<webswing client Id>", "<webswing client IP address>", "<webswing client locale>", "<webswing custom args>");
			break;

		case SwingApp:
			variableSubstitutor = VariableSubstitutor.forSwingApp(getHandler().getConfig());
			break;

		case Basic:
		default:
			variableSubstitutor = VariableSubstitutor.basic();
			break;
		}

		return variableSubstitutor;
	}


	@GET
	@Path("/rest/variables/search/{type}")
	public Map<String, String> searchVariables(@PathParam("type") String type, @QueryParam("search") String searchSequence) throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);
		final int VARIABLES_RESULT_COUNT = 10;
		if (searchSequence == null) {
			searchSequence = "";
		}

		// transform search sequence - this is case insensitive search
		searchSequence = searchSequence.toLowerCase();

		// alphabetically sorted variables, whose name (key of the map entry) starts with search sequence
		Map<String, String> searchResultStartBy = new TreeMap<>();
		// alphabetically sorted variables, whose name (key of the map entry) contains search sequence
		Map<String, String> searchResultContains = new TreeMap<>();

		// first, sort all existing variables by alphabetically order,
		// because empty search string - getting list of first 10th variables, not first 10th finded un-ordered variables
		SortedMap<String, String> variables = new TreeMap<>(this.getVariableSubstitutorByType(type).getVariableMap());

		for (Map.Entry<String, String> variable : variables.entrySet()) {
			if (searchResultStartBy.size() + searchResultContains.size() == VARIABLES_RESULT_COUNT) {
				break;
			}

			// search in variable names
			//
			String variableLowerCase = variable.getKey().toLowerCase();

			if (variableLowerCase.startsWith(searchSequence)) {
				searchResultStartBy.put(variable.getKey(), variable.getValue());
				continue;
			}
			if (variableLowerCase.contains(searchSequence)) {
				searchResultContains.put(variable.getKey(), variable.getValue());
				continue;
			}

			// search in variable values
			//
			String valueLowerCase = variable.getValue().toLowerCase();

			if (valueLowerCase.contains(searchSequence)) {
				searchResultContains.put(variable.getKey(), variable.getValue());
				//noinspection UnnecessaryContinue
				continue;
			}
		}

		// all results, ordered and sorted per result category
		Map<String, String> allResults = new LinkedHashMap<>();

		allResults.putAll(searchResultStartBy);
		allResults.putAll(searchResultContains);

		return allResults;
	}


	@GET
	@Path("/rest/variables/resolve/{type}")
	@Produces(MediaType.TEXT_PLAIN)
	public String resolve(@PathParam("type") String type, @QueryParam("resolve") String stringToResolve) throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);
		if (stringToResolve == null) {
			return null;
		}
		return this.getVariableSubstitutorByType(type).replace(stringToResolve);
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
		getHandler().checkPermissionLocalOrMaster(WebswingAction.master_basic_access);
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
	public String generateCsrfToken() throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.websocket_connect);
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
