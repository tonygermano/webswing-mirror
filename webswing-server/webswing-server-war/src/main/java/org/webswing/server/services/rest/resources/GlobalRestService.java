package org.webswing.server.services.rest.resources;

import org.apache.commons.lang.StringUtils;
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.server.GlobalUrlHandler;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.common.model.admin.ApplicationInfo;
import org.webswing.server.common.model.rest.LogRequest;
import org.webswing.server.common.model.rest.LogResponse;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.swingmanager.SwingInstanceManager;
import org.webswing.server.util.LogReaderUtil;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class GlobalRestService extends BaseRestService {

	@Inject GlobalUrlHandler handler;
	@Inject ConfigurationService configService;

	@Override
	protected List<ApplicationInfoMsg> getAppsImpl() {
		List<ApplicationInfoMsg> result = new ArrayList<>();
		for (SwingInstanceManager mgr : getGlobalHandler().getApplications()) {
			if (mgr.isEnabled() && mgr.isUserAuthorized()) {
				ApplicationInfoMsg applicationInfoMsg = mgr.getApplicationInfoMsg();
				if (applicationInfoMsg != null) {
					result.add(applicationInfoMsg);
				}
			}
		}
		return result;
	}

	@Override
	protected ApplicationInfo getAppInfoImpl() {
		ApplicationInfo app = super.getAppInfoImpl();
		app.setName("Server");
		return app;
	}

	@Override
	protected List<String> getPathsImpl() {
		List<String> result = new ArrayList<>();
		for (SwingInstanceManager appManager : getGlobalHandler().getApplications()) {
			result.add(appManager.getFullPathMapping());
		}
		return result;
	}

	@Override
	protected Map<String, Boolean> getPermissionsImpl() throws Exception {
			Map<String, Boolean> perm = super.getPermissionsImpl();
			boolean multiApplicationMode = configService.isMultiApplicationMode();
			perm.put("start", isMasterPermited(WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_startApp));
			perm.put("stop", isMasterPermited(WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_stopApp));
			perm.put("remove", multiApplicationMode && isMasterPermited(WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_removeApp));
			perm.put("create", multiApplicationMode && isMasterPermited(WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_createApp));
			perm.put("configEdit", isMasterPermited(WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_getConfig, WebswingAction.rest_setConfig));
			perm.put("logsView", isMasterPermited(WebswingAction.rest_viewLogs));
			return perm;
	}

	@Override
	protected void saveConfigImpl(Map<String, Object> config) throws Exception{
		config.put("path", "/");
		configService.setConfiguration("/", config);
	}

	@GET
	@Path("/rest/remove{appPath: .+?}")
	public void removeSwingApp(@PathParam("appPath") String path) throws Exception {
		getHandler().checkMasterPermission(WebswingAction.rest_removeApp);
		if (!StringUtils.isEmpty(path)) {
			SwingInstanceManager swingManager = getGlobalHandler().getApplication(path);
			if (swingManager != null) {
				if (!swingManager.isEnabled()) {
					configService.removeConfiguration(path);
				} else {
					throw new WsException("Unable to Remove App '" + path + "' while running. Stop the app first");
				}
			}
		} else {
			throw new WsException("Unable to remove App '" + path + "'", HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@GET
	@Path("/rest/create{appPath: .+?}")
	public void createSwingApp(@PathParam("appPath") String path) throws Exception {
		getHandler().checkMasterPermission(WebswingAction.rest_createApp);
		if (!StringUtils.isEmpty(path)) {
			SwingInstanceManager swingManager = getGlobalHandler().getApplication(path);
			if (swingManager == null) {
				Map<String, Object> config = new HashMap<>();
				config.put("enabled", false);
				configService.setConfiguration(path, config);//first create with enabled:false to prevent initiation
				configService.setConfiguration(path, null);//once exists,
			} else {
				throw new WsException("Unable to Create App '" + path + "'. Application already exits.");
			}
		} else {
			throw new WsException("Unable to create App '" + path + "'", HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@POST
	@Path("/rest/logs/{type}")
	public LogResponse getLogs(@PathParam("type") String type, LogRequest request) throws WsException {
		getHandler().checkMasterPermission(WebswingAction.rest_viewLogs);
		return LogReaderUtil.readLog(type, request);
	}

	@GET
	@Path("/rest/logs/{type}")
	public Response downloadLog(@PathParam("type") String type) throws WsException {
		getHandler().checkMasterPermission(WebswingAction.rest_viewLogs);
		Response.ResponseBuilder builder = Response.ok(LogReaderUtil.getZippedLog(type), MediaType.APPLICATION_OCTET_STREAM);
		builder.header("content-disposition", "attachment; filename = " + type + ".zip");
		return builder.build();
	}

	@Override
	PrimaryUrlHandler getHandler() {
		return handler;
	}

	GlobalUrlHandler getGlobalHandler() {
		return handler;
	}

	@Override
	ConfigurationService getConfigService() {
		return configService;
	}
}
