package org.webswing.server.services.rest.resources;

import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.util.VariableSubstitutor;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.rest.resources.model.*;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.toolkit.util.GitRepositoryState;

import java.util.*;

public class BaseRestUtil {

	private static final String default_version = "unresolved";


	protected static ApplicationInfo getAppInfoImpl(PrimaryUrlHandler h) {
		ApplicationInfo app = new ApplicationInfo();
		app.setPath(h.getPathMapping());
		app.setUrl(h.getFullPathMapping());
		app.setEnabled(h.isEnabled());
		app.setConfig(h.getConfig());
		app.setVariables(h.getVariableMap());
		app.setStatus(h.getStatus());
		return app;
	}


	public static MetaObject getConfig(PrimaryUrlHandler handler, ConfigurationService configService) throws RestException {
		try {
			handler.checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);
			return configService.describeConfiguration(handler.getPathMapping(), null, handler);
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	private static VariableSubstitutor getVariableSubstitutorByType(String type, PrimaryUrlHandler handler) {

		VariableSetName variableType;

		try {
			variableType = VariableSetName.valueOf(type);
		} catch (Exception e) {
			variableType = VariableSetName.BASIC;
		}

		VariableSubstitutor variableSubstitutor;

		switch (variableType) {

		case SWINGINSTANCE:
			String userName = handler.getUser() == null ? "<webswing user>" : handler.getUser().getUserId();
			variableSubstitutor = VariableSubstitutor.forSwingInstance(handler.getConfig(), userName, null, "<webswing client Id>", "<webswing client IP address>", "<webswing client locale>", "<webswing client timezone>", "<webswing custom args>");
			break;

		case SWINGAPP:
			variableSubstitutor = VariableSubstitutor.forSwingApp(handler.getConfig());
			break;

		case BASIC:
		default:
			variableSubstitutor = VariableSubstitutor.basic();
			break;
		}

		return variableSubstitutor;
	}

	public static Map<String, String> searchVariables(String type, String searchSequence, PrimaryUrlHandler handler) throws RestException {
		try {
			handler.checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);
		} catch (WsException e) {
			throw new RestException(e);
		}
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
		SortedMap<String, String> variables = new TreeMap<>(getVariableSubstitutorByType(type,handler).getVariableMap());

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

	public static String resolve(String type, String stringToResolve, PrimaryUrlHandler handler) throws RestException {
		try {
			handler.checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);
			if (stringToResolve == null) {
				return null;
			}
			return getVariableSubstitutorByType(type,handler).replace(stringToResolve);
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	public static MetaObject getMeta(Map<String, Object> json, PrimaryUrlHandler handler, ConfigurationService configService) throws RestException {
		try {
			handler.checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);
			return configService.describeConfiguration(handler.getPathMapping(), json, handler);
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	public static String getVersion() throws RestException {
		String describe = GitRepositoryState.getInstance().getDescribe();
		if (describe == null) {
			return default_version;
		}
		return describe;
	}

	public static boolean isPermited(PrimaryUrlHandler handler,WebswingAction... actions) {
		for (WebswingAction action : actions) {
			boolean local = handler.getUser() != null && handler.getUser().isPermitted(action.name());
			if (!local) {
				boolean master = isMasterPermited(handler, actions);
				if (!master) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isMasterPermited(PrimaryUrlHandler handler, WebswingAction... actions) {
		for (WebswingAction action : actions) {
			boolean master = handler.getMasterUser() != null && handler.getMasterUser().isPermitted(action.name());
			if (!master) {
				return false;
			}
		}
		return true;
	}

	public static Manifest getManifest(PrimaryUrlHandler handler) throws RestException {
		// must be accessible without login
		SecuredPathConfig config = handler.getConfig();

		String color = "#FFFFFF";

		Manifest manifest = new Manifest();
		manifest.setName(config.getSwingConfig().getName());
		manifest.setShortName(manifest.getName());
		ManifestIcons icon = new ManifestIcons();
		icon.setSrc(handler.getFullPathMapping() + "/appicon");
		icon.setSizes("256x256");
		manifest.setIcons(Arrays.asList(icon)); // this is just a hardcoded value, not real size
		manifest.setStartUrl(handler.getFullPathMapping());
		manifest.setScope(handler.getFullPathMapping());
		manifest.setBackgroundColor(color);
		manifest.setDisplay("fullscreen");
		manifest.setThemeColor(color);

		return manifest;
	}

	public  static Permissions getPermissions(PrimaryUrlHandler handler) {
		Permissions permissions = new Permissions();
		permissions.setDashboard( isPermited(handler, WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo));
		permissions.setConfigView( isPermited(handler, WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_getConfig));
		permissions.setConfigSwingEdit( isMasterPermited(handler, WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_getConfig, WebswingAction.rest_setConfig));
		permissions.setSessions( isPermited(handler, WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_getSession));
		return permissions;
	}
}
