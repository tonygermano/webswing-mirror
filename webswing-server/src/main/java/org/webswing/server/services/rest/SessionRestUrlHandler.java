package org.webswing.server.services.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;

public class SessionRestUrlHandler extends AbstractRestUrlHandler {

	private final SwingInstanceHolder instanceHolder;

	public SessionRestUrlHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		super(parent);
		this.instanceHolder = instanceHolder;
	}

	@GET
	@Path("/sessions")
	public Object getSession(@PathParam("") String id) {
		if (StringUtils.isEmpty(id)) {
			return instanceHolder.getAllInstances();
		} else {
			if (id.startsWith("/")) {
				id = id.substring(1);
			}
			return instanceHolder.findInstanceByClientId(id);
		}
	}

	@DELETE
	@Path("/sessions")
	public void shutdown(@PathParam("") String id, @QueryParam("force") String forceKill) {
		if (id.startsWith("/")) {
			id = id.substring(1);
		}
		boolean force = Boolean.parseBoolean(forceKill);
		SwingInstance instance = instanceHolder.findInstanceByClientId(id);

		if (instance != null) {
			instance.shutdown(force);
		}
	}

}
