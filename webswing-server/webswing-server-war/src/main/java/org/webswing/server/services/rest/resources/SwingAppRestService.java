package org.webswing.server.services.rest.resources;

import org.apache.commons.lang3.StringUtils;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.common.util.VariableSubstitutor;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.rest.resources.api.BasicApi;
import org.webswing.server.services.rest.resources.api.ManageConfigurationApi;
import org.webswing.server.services.rest.resources.api.ManageSessionsApi;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class SwingAppRestService implements BasicApi, ManageConfigurationApi, ManageSessionsApi {

	@Inject SwingInstanceManager manager;
	@Inject PrimaryUrlHandler handler;
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
			app.setName(handler.getSwingConfig().getName());
			List<SwingInstance> allRunning = manager.getSwingInstanceHolder().getAllInstances();
			app.setRunningInstances(allRunning.size());
			int connected = 0;
			for (SwingInstance si : allRunning) {
				if (si.getConnectionId() != null) {
					connected++;
				}
			}
			app.setConnectedInstances(connected);
			app.setFinishedInstances(manager.getSwingInstanceHolder().getAllClosedInstances().size());
			int maxRunningInstances = handler.getSwingConfig().getMaxClients();
			app.setMaxRunningInstances(maxRunningInstances);
			app.setStats(manager.getStatsReader().getSummaryStats());
			app.setWarnings(manager.getStatsReader().getSummaryWarnings());
			return app;
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public List<BasicApplicationInfo> getPaths() throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getPaths);
			BasicApplicationInfo app = new BasicApplicationInfo();
			app.setPath(manager.getPathMapping());
			app.setUrl(manager.getFullPathMapping());
			app.setEnabled(manager.isEnabled());
			app.setName(handler.getSwingConfig().getName());
			List<SwingInstance> allRunning = manager.getSwingInstanceHolder().getAllInstances();
			app.setRunningInstances(allRunning.size());
			return Arrays.asList(app);
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public Sessions getSessions() throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getSession);
			Set<String> localRecordingFiles = new HashSet<>();
			Sessions result = new Sessions();
			manager.getSwingInstanceHolder().getAllInstances().stream().map(si -> si.toSwingSession(false)).forEach(session -> {
				result.getSessions().add(session);
				if (session.getRecordingFile() != null) {
					localRecordingFiles.add(session.getRecordingFile());
				}
			});
			manager.getSwingInstanceHolder().getAllClosedInstances().stream().map(si -> si.toSwingSession(false)).forEach(session -> {
				result.getClosedSessions().add(session);
				if (session.getRecordingFile() != null) {
					localRecordingFiles.add(session.getRecordingFile());
				}
			});

			List<String> externalRecordings = new ArrayList<>();
			if (manager.getRecordingsDirPath() != null) {
				File recordingsDir = new File(URI.create(manager.getRecordingsDirPath()));
				if (recordingsDir.exists() && recordingsDir.isDirectory() && recordingsDir.canRead())
					for (File rFile : recordingsDir.listFiles()) {
						if (!localRecordingFiles.contains(rFile.getName())) {
							externalRecordings.add(rFile.getName());
						}

					}
				result.setRecordings(externalRecordings);
			}
			return result;
		} catch (WsException e) {
			throw new RestException(e);
		}
	}
	
	@Override
	public Integer activeSessionsCount() throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getStats);
			return manager.getSwingInstanceHolder().getRunningInstacesCount();
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public Permissions getPermissions() throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.master_basic_access);
			return BaseRestUtil.getPermissions(handler);
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public List<ApplicationInfoMsg> getApps() throws RestException {
		try {
			getHandler().checkPermission(WebswingAction.rest_getApps);
			List<ApplicationInfoMsg> result = new ArrayList<>();
			if (manager.isEnabled() && manager.isUserAuthorized()) {
				ApplicationInfoMsg applicationInfoMsg = manager.getApplicationInfoMsg();
				if (applicationInfoMsg != null) {
					result.add(applicationInfoMsg);
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

	@Override
	public String getVersion() throws RestException {
		return BaseRestUtil.getVersion();
	}

	@Override
	public void ping() throws RestException {
	}

	@Override
	public Map<String, Map<String, BigDecimal>> getStats() throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getStats);

			List<InstanceStats> allStats = new ArrayList<InstanceStats>(manager.getStatsReader().getAllInstanceStats());
			allStats.addAll(loggerService.getServerLogger().getAllInstanceStats()); // merge with server stats (to include server CPU usage)

			return LoggerStatisticsUtil.mergeSummaryInstanceStats(allStats);
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
	public MetaObject getMeta(Map<String, Object> requestBody) throws RestException {
		return BaseRestUtil.getMeta(requestBody, getHandler(), getConfigService());
	}

	@Override
	public void saveConfig(Map<String, Object> config) throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_setConfig);
			getConfigService().setConfiguration(getHandler().getPathMapping(), config);
		} catch (Exception e) {
			throw new RestException(e);
		}
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
	// Manage Sessions Api
	//====================================================

	@Override
	public SwingSession getSession(String id) throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getSession);
			SwingInstance instance = manager.getSwingInstanceHolder().findInstanceByInstanceId(id);
			if (instance != null) {
				return instance.toSwingSession(true);
			}
			return null;
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public SwingSession getMetrics(String uuid) throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.websocket_connect);
			SwingInstance instance = manager.getSwingInstanceHolder().findInstanceByConnectionId(uuid);
			if (instance != null && getHandler().getUser().getUserId().equals(instance.getUserId())) {
				return instance.toSwingSession(true);
			}
			return null;
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public SwingSession startRecording(String id) throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_startRecording);
			SwingInstance instance = manager.getSwingInstanceHolder().findInstanceByInstanceId(id);
			if (instance != null) {
				if (instance.isRecording()) {
					instance.stopRecording();
				} else {
					instance.startRecording();
				}
				return instance.toSwingSession(true);
			}
			return null;
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public String getThreadDump(String instanceId, String timestamp) throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getThreadDump);
			SwingInstance instance = manager.getSwingInstanceHolder().findInstanceByInstanceId(instanceId);
			if (instance != null) {
				return instance.getThreadDump(timestamp);
			} else {
				List<SwingInstance> instaces = manager.getSwingInstanceHolder().getAllClosedInstances();//closed instances can have multiple instances for same id, need to manually check all
				for (SwingInstance i : instaces) {
					if (instanceId.equals(i.getInstanceId())) {
						String td = i.getThreadDump(timestamp);
						if (td != null) {
							return td;
						}
					}
				}
				throw new RestException("Not found", 404);
			}
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public void requestThreadDump(String id) throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_requestThreadDump);
			SwingInstance instance = manager.getSwingInstanceHolder().findInstanceByInstanceId(id);
			if (instance != null) {
				instance.requestThreadDump();
			}

		} catch (WsException e) {
			throw new RestException(e);
		}
	}
	
	@Override
	public void toggleStatisticsLogging(String instanceId, Boolean enabled) throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_toggleStatisticsLogging);
			SwingInstance instance = manager.getSwingInstanceHolder().findInstanceByInstanceId(instanceId);
			if (instance != null) {
				instance.toggleStatisticsLogging(enabled);
			}
			
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public void shutdown(String id, String forceKill) throws RestException {
		try {
			boolean force = Boolean.parseBoolean(forceKill);
			if (force) {
				getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_sessionShutdown);
			} else {
				getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_sessionShutdownForce);
			}
			SwingInstance instance = manager.getSwingInstanceHolder().findInstanceByInstanceId(id);
			if (instance != null) {
				instance.shutdown(force);
			} else {
				throw new WsException("Instance with id " + id + " not found.");
			}
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public LogResponse getSessionLogs(LogRequest request) throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_viewLogs);

			if (StringUtils.isBlank(request.getInstanceId())) {
				return null;
			}

			return LogReaderUtil.readSessionLog(getInfo().getUrl(), getSessionLogsDir(), request);
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public List<String> getLogInstanceIds() throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_viewLogs);
			return LogReaderUtil.readSessionLogInstanceIds(getSessionLogsDir(), getInfo().getUrl());
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	private String getSessionLogsDir() {
		return LogReaderUtil.getSessionLogDir(VariableSubstitutor.forSwingApp(manager.getConfig()), manager.getConfig().getSwingConfig());
	}

	@Override
	public File downloadSessionsLog() throws RestException {
		try {
			getHandler().checkMasterPermission(WebswingAction.rest_viewLogs);
			File result = LogReaderUtil.getZippedSessionLog(getSessionLogsDir(), getInfo().getUrl());
			response.setHeader("content-disposition", "attachment; filename = " + LogReaderUtil.normalizeForFileName(getInfo().getName()) + "_session_logs.zip");
			return result;
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public String generateCsrfToken() throws RestException {
		try {
			getHandler().checkPermissionLocalOrMaster(WebswingAction.websocket_connect);
			return getHandler().generateCsrfToken();
		} catch (WsException e) {
			throw new RestException(e);
		}
	}

	@Override
	public Manifest getManifest() throws RestException {
		return BaseRestUtil.getManifest(getHandler());
	}

	@Override
	public File getIcon() throws RestException {
		// must be accessible without login for manifest.json
		//		getHandler().checkPermissionLocalOrMaster(WebswingAction.websocket_connect);
		File icon = handler.resolveFile(handler.getConfig().getIcon());
		if (icon == null) {
			try {
				icon = new File(SwingAppRestService.class.getClassLoader().getResource("images/java.png").toURI());
			} catch (URISyntaxException e) {
				// ignore
			}
		}
		response.setHeader("Cache-Control", "public, max-age=120");
		return icon;
	}

	private PrimaryUrlHandler getHandler() {
		return handler;
	}

	private ConfigurationService getConfigService() {
		return configService;
	}

}
