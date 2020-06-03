package org.webswing.server.services.rest.resources;

import org.apache.commons.lang3.StringUtils;
import org.webswing.server.GlobalUrlHandler;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.rest.resources.api.BasicApi;
import org.webswing.server.services.rest.resources.api.ManageApplicationsApi;
import org.webswing.server.services.rest.resources.api.ManageConfigurationApi;
import org.webswing.server.services.rest.resources.api.ViewLogsApi;
import org.webswing.server.services.rest.resources.model.*;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.stats.StatisticsLoggerService;
import org.webswing.server.services.stats.logger.InstanceStats;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swingmanager.SwingInstanceManager;
import org.webswing.server.util.LogReaderUtil;
import org.webswing.server.util.LoggerStatisticsUtil;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class GlobalRestService implements BasicApi, ManageApplicationsApi, ManageConfigurationApi, ViewLogsApi {

	@Inject GlobalUrlHandler handler;
	@Inject ConfigurationService configService;
	@Inject StatisticsLoggerService loggerService;
	@Context HttpServletResponse response;

	//====================================================
	// Basic API
	//====================================================
	@Override
	public ApplicationInfo getInfo() throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getAppInfo);
			ApplicationInfo app = BaseRestUtil.getAppInfoImpl(getHandler());
			app.setName("Server");
			return app;
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public List<BasicApplicationInfo> getPaths() throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getPaths);
			List<BasicApplicationInfo> result = new ArrayList<>();
			for (SwingInstanceManager appManager : getGlobalHandler().getApplications()) {
				BasicApplicationInfo app = getBasicApplicationInfo(appManager);
				result.add(app);
			}
			return result;
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public Sessions getSessions() throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_getSession);
			Sessions result = new Sessions();
			getGlobalHandler().getApplications().forEach(app -> app.getSwingInstanceHolder().getAllInstances().forEach(si -> result.getSessions().add(si.toSwingSession(false))));
			getGlobalHandler().getApplications().forEach(app -> app.getSwingInstanceHolder().getAllClosedInstances().forEach(si -> result.getClosedSessions().add(si.toSwingSession(false))));
			return result;
		} catch (WsException e) {
			throw new RestException(e);
		}
	}
	
	@Override
	public Integer activeSessionsCount() throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getStats);
			return getGlobalHandler().getApplications().stream().mapToInt(m -> m.getSwingInstanceHolder().getRunningInstacesCount()).sum();
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public Permissions getPermissions() throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.master_basic_access);
			boolean multiApplicationMode = configService.isMultiApplicationMode();
			Permissions perm = BaseRestUtil.getPermissions(handler);
			perm.setStart(BaseRestUtil.isMasterPermited(handler, WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_startApp));
			perm.setStop(BaseRestUtil.isMasterPermited(handler, WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_stopApp));
			perm.setRemove(multiApplicationMode && BaseRestUtil.isMasterPermited(handler, WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_removeApp));
			perm.setCreate(multiApplicationMode && BaseRestUtil.isMasterPermited(handler, WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_createApp));
			perm.setConfigEdit(BaseRestUtil.isMasterPermited(handler, WebswingAction.rest_getPaths, WebswingAction.rest_getAppInfo, WebswingAction.rest_getConfig, WebswingAction.rest_setConfig));
			perm.setLogsView(BaseRestUtil.isMasterPermited(handler, WebswingAction.rest_viewLogs));
			return perm;
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	public List<ApplicationInfoMsg> getApps() throws RestException {
		try {
			getHandler().checkPermission(WebswingAction.rest_getApps);
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
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public String getAdminConsoleUrl() throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getAppInfo);
			return getHandler().getAdminUrl();
		} catch (WsException e) {
			throw new RestException(e);
		}
	}
	
	public String getVersion() throws RestException {
		return BaseRestUtil.getVersion();
	}

	@Override
	public void ping() throws RestException {
	}

	@Override
	public Map<String, Map<String, BigDecimal>> getStats() throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_getStats);

			List<InstanceStats> allStats = getGlobalHandler().getApplications().stream().map(app -> app.getStatsReader().getAllInstanceStats()).flatMap(Collection::stream).collect(Collectors.toList());

			allStats.addAll(loggerService.getServerLogger().getAllInstanceStats()); // merge with server stats (to include server CPU usage)

			return LoggerStatisticsUtil.mergeSummaryInstanceStats(allStats);
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public File getIcon() throws RestException {
		// must be accessible without login for manifest.json
		//		getHandler().checkPermissionLocalOrMaster(WebswingAction.websocket_connect);
		File icon = handler.resolveFile(handler.getConfig().getIcon());
		response.setHeader("Cache-Control", "public, max-age=120");
		return icon;
	}

	@Override
	public Manifest getManifest() throws RestException {
		return BaseRestUtil.getManifest(getHandler());
	}

	//====================================================
	// Manage Applications Api
	//====================================================

	@Override
	public void createApp(String path) throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_createApp);
			if (!StringUtils.isEmpty(path)) {
				SwingInstanceManager swingManager = getGlobalHandler().getApplication(path);
				if (swingManager == null) {
					Map<String, Object> config = new HashMap<>();
					config.put("enabled", false);
					configService.setConfiguration(path, config);//first create with enabled:false to prevent initiation
					configService.setConfiguration(path, null);//once exists,
				} else {
					throw new RestException("Unable to Create App '" + path + "'. Application already exits.");
				}
			} else {
				throw new RestException("Unable to create App '" + path + "'", HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (RestException e) {
			throw e;
		} catch (Exception e) {
			throw new RestException(e);
		}
	}

	@Override
	public void removeApp(String path) throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_removeApp);
			if (!StringUtils.isEmpty(path)) {
				SwingInstanceManager swingManager = getGlobalHandler().getApplication(path);
				if (swingManager != null) {
					if (!swingManager.isEnabled()) {
						configService.removeConfiguration(path);
					} else {
						throw new RestException("Unable to Remove App '" + path + "' while running. Stop the app first");
					}
				}
			} else {
				throw new RestException("Unable to remove App '" + path + "'", HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (RestException e) {
			throw e;
		} catch (Exception e) {
			throw new RestException(e);
		}
	}

	@Override
	public void startApp(String path) throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_startApp);
			SwingInstanceManager appHandler = handler.getApplication(path);
			if (appHandler == null) {
				throw new RestException("Invalid appPath", 400);
			}
			if (!appHandler.isEnabled()) {
				appHandler.initConfiguration();
			}
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public void stopApp(String path) throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_stopApp);
			SwingInstanceManager appHandler = handler.getApplication(path);
			if (appHandler == null) {
				throw new RestException("Invalid appPath", 400);
			}
			if (appHandler.isEnabled()) {
				appHandler.disable();
			}
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	//====================================================
	// Manage Configuration Api
	//====================================================

	@Override
	public MetaObject getConfig() throws RestException {
		return BaseRestUtil.getConfig(getHandler(), getConfigService());
	}

	@Override
	public void saveConfig(Map<String, Object> config) throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_setConfig);
			config.put("path", "/");
			configService.setConfiguration("/", config);
		} catch (Exception e) {
			throw new RestException(e);
		}
	}

	@Override
	public MetaObject getMeta(Map<String, Object> requestBody) throws RestException {
		return BaseRestUtil.getMeta(requestBody, getHandler(), getConfigService());
	}

	@Override
	public String resolve(String type, String resolve) throws RestException {
		return BaseRestUtil.resolve(type, resolve, getHandler());
	}

	@Override
	public Map<String, String> searchVariables(String type, String search) throws RestException {
		return BaseRestUtil.searchVariables(type, search, getHandler());
	}

	//====================================================
	// View Logs Api
	//====================================================

	@Override
	public LogResponse getLogs(String type, LogRequest request) throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_viewLogs);
			return LogReaderUtil.readLog(type, request);
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public File downloadLog(String type) throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_viewLogs);
			File result = LogReaderUtil.getZippedLog(type);
			response.setHeader("content-disposition", "attachment; filename = " + type + ".zip");
			return result;
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public List<BasicApplicationInfo> getAppsForSessionLogView() throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_viewLogs);
			getHandler().checkMasterPermission(WebswingAction.rest_getApps);

			return getGlobalHandler().getApplications().stream().filter(app -> app.getConfig().getSwingConfig().isSessionLogging()).map(app -> getBasicApplicationInfo(app)).collect(Collectors.toList());
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	private BasicApplicationInfo getBasicApplicationInfo(SwingInstanceManager appManager) {
		BasicApplicationInfo app = new BasicApplicationInfo();
		app.setPath(appManager.getPathMapping());
		app.setUrl(appManager.getFullPathMapping());
		app.setEnabled(appManager.isEnabled());
		if (appManager.getConfig() != null && appManager.getConfig().getSwingConfig() != null) {
			app.setName(appManager.getConfig().getSwingConfig().getName());
		}
		List<SwingInstance> allRunning = appManager.getSwingInstanceHolder().getAllInstances();
		app.setRunningInstances(allRunning.size());
		return app;
	}

	private PrimaryUrlHandler getHandler() {
		return handler;
	}

	private GlobalUrlHandler getGlobalHandler() {
		return handler;
	}

	private ConfigurationService getConfigService() {
		return configService;
	}

}
