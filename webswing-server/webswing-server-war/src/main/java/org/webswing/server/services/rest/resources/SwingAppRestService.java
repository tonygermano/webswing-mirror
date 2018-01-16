package org.webswing.server.services.rest.resources;

import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.common.model.admin.ApplicationInfo;
import org.webswing.server.common.model.admin.Sessions;
import org.webswing.server.common.model.admin.SwingSession;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swingmanager.SwingInstanceManager;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class SwingAppRestService extends BaseRestService {

	@Inject SwingInstanceManager manager;
	@Inject PrimaryUrlHandler handler;
	@Inject ConfigurationService configService;

	@Override
	protected List<ApplicationInfoMsg> getAppsImpl() {
		return Arrays.asList(manager.getApplicationInfoMsg());
	}

	@Override
	protected ApplicationInfo getAppInfoImpl() {
		ApplicationInfo app = super.getAppInfoImpl();
		app.setName(handler.getSwingConfig().getName());
		List<SwingInstance> allRunning = manager.getSwingInstanceHolder().getAllInstances();
		app.setRunningInstances(allRunning.size());
		int connected = 0;
		for (SwingInstance si : allRunning) {
			if (si.getSessionId() != null) {
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
	}

	@Override
	protected List<String> getPathsImpl() {
		return Arrays.asList(handler.getFullPathMapping());
	}

	@GET
	@Path("/start")
	public Response start() throws WsException {
		getHandler().checkMasterPermission(WebswingAction.rest_startApp);
		if (!getHandler().isEnabled()) {
			getHandler().initConfiguration();
		}
		return Response.ok().build();
	}

	@GET
	@Path("/stop")
	public Response stop() throws WsException {
		getHandler().checkMasterPermission(WebswingAction.rest_stopApp);
		if (getHandler().isEnabled()) {
			getHandler().disable();
		}
		return Response.ok().build();
	}

	@GET
	@Path("/rest/sessions")
	public Sessions getSessions() throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getSession);
		Sessions result = new Sessions();
		for (SwingInstance si : manager.getSwingInstanceHolder().getAllInstances()) {
			result.getSessions().add(si.toSwingSession(false));
		}
		for (SwingInstance si : manager.getSwingInstanceHolder().getAllClosedInstances()) {
			result.getClosedSessions().add(si.toSwingSession(false));
		}
		return result;
	}

	@GET
	@Path("/rest/session/{id}")
	public SwingSession getSession(@PathParam("id") String id) throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getSession);
		SwingInstance instance = manager.getSwingInstanceHolder().findInstanceByInstanceId(id);
		if (instance != null) {
			return instance.toSwingSession(true);
		}
		return null;
	}

	@GET
	@Path("/rest/record/{id}")
	public SwingSession startRecording(@PathParam("id") String id) throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_startRecording);
		SwingInstance instance = manager.getSwingInstanceHolder().findInstanceByInstanceId(id);
		if (instance != null) {
			instance.startRecording();
			return instance.toSwingSession(true);
		}
		return null;
	}

	@GET
	@Path("/rest/threadDump/{path}")
	public String getThreadDump(@PathParam("path") String id, @QueryParam("id") String timestamp) throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_getThreadDump);
		SwingInstance instance = manager.getSwingInstanceHolder().findInstanceByInstanceId(id);
		if (instance != null) {
			return instance.getThreadDump(timestamp);
		} else {
			List<SwingInstance> instaces = manager.getSwingInstanceHolder().getAllClosedInstances();//closed instances can have multiple instances for same id, need to manually check all
			for (SwingInstance i : instaces) {
				if (id.equals(i.getInstanceId())) {
					String td = i.getThreadDump(timestamp);
					if (td != null) {
						return td;
					}
				}
			}
			throw new WsException("Not found", 404);
		}
	}

	@POST
	@Path("/rest/threadDump/{path}")
	public void requestThreadDump(@PathParam("path") String id) throws WsException {
		getHandler().checkPermissionLocalOrMaster(WebswingAction.rest_requestThreadDump);
		SwingInstance instance = manager.getSwingInstanceHolder().findInstanceByInstanceId(id);
		if (instance != null) {
			instance.requestThreadDump();
		}
	}

	@DELETE
	@Path("/rest/session/{id}")
	public void shutdown(@PathParam("id") String id, @QueryParam("force") String forceKill) throws WsException {
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
	}

	@Override
	protected PrimaryUrlHandler getHandler() {
		return handler;
	}

	@Override
	protected ConfigurationService getConfigService() {
		return configService;
	}

}
