package org.webswing.server.services.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.webswing.model.server.admin.Sessions;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;

public class SessionRestUrlHandler extends AbstractRestUrlHandler {

	private final SwingInstanceHolder instanceHolder;

	public SessionRestUrlHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		super(parent);
		this.instanceHolder = instanceHolder;
	}

	@GET
	@Path("/session")
	public Object getSession(@PathParam("") String id) throws WsException {
		checkPermission(WebswingAction.rest_getSession);

		if (StringUtils.isEmpty(id)) {
			Sessions result = new Sessions();
			for (SwingInstance si : instanceHolder.getAllInstances()) {
				result.getSessions().add(si.toSwingSession());
			}
			for (SwingInstance si : instanceHolder.getAllClosedInstances()) {
				result.getClosedSessions().add(si.toSwingSession());
			}
			return result;
		} else {
			if (id.startsWith("/")) {
				id = id.substring(1);
			}
			SwingInstance instance = instanceHolder.findInstanceByClientId(id);
			if (instance != null) {
				return instance.toSwingSession();
			}
			return null;
		}
	}

	@DELETE
	@Path("/session")
	public void shutdown(@PathParam("") String id, @QueryParam("force") String forceKill) throws WsException {
		boolean force = Boolean.parseBoolean(forceKill);
		if (force) {
			checkPermission(WebswingAction.rest_sessionShutdown);
		} else {
			checkPermission(WebswingAction.rest_sessionShutdownForce);
		}

		if (id.startsWith("/")) {
			id = id.substring(1);
		}
		SwingInstance instance = instanceHolder.findInstanceByClientId(id);

		if (instance != null) {
			instance.shutdown(force);
		}
	}

}
