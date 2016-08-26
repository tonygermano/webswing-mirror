package org.webswing.server.services.rest;

import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.admin.Sessions;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.common.util.ConfigUtil;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;
import org.webswing.server.services.swingmanager.SwingInstanceManager;

public class AdminRestUrlHandler extends AbstractUrlHandler {
	private static final Logger log = LoggerFactory.getLogger(AdminRestUrlHandler.class);

	private final SwingInstanceHolder instanceHolder;

	public AdminRestUrlHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		super(parent);
		this.instanceHolder = instanceHolder;
	}

	@Override
	protected String getPath() {
		return "/rest";
	}

	@POST
	@Path("/admin/metaObject")
	public MetaObject getMeta(Map<String, Object> json) throws WsException {
		checkPermissionLocalOrMaster(WebswingAction.rest_getMeta);
		SecuredPathConfig securedPathConfig = ConfigUtil.instantiateConfig(json, SecuredPathConfig.class);
		try {
			MetaObject result = ConfigUtil.getConfigMetadata(securedPathConfig, getClass().getClassLoader());
			result.setData(json);
			return result;
		} catch (Exception e) {
			log.error("Failed to generate configuration descriptor.", e);
			throw new WsException("Failed to generate configuration descriptor.");
		}
	}

	@GET
	@Path("/admin/variables")
	public Map<String, String> getVariables() throws WsException {
		checkPermissionLocalOrMaster(WebswingAction.rest_getConfigVariables);

		String userName = getUser() == null ? "<webswing user>" : getUser().getUserId();
		Map<String, String> vars = CommonUtil.getConfigSubstitutorMap(userName, "<webswing client Id>", "<webswing client IP address>", "<webswing client locale>", "<webswing custom args>");
		return vars;
	}

	@GET
	@Path("/admin/sessions")
	public Object getSessions(@PathParam("") String path) throws WsException {
		checkPermission(WebswingAction.rest_getSession);

		if (!StringUtils.isEmpty(path)) {
			Sessions result = new Sessions();
			for (SwingInstanceManager sim : instanceHolder.getApplications()) {
				if (sim.getPathMapping().equals(path)) {
					for (SwingInstance si : sim.getAllInstances()) {
						result.getSessions().add(si.toSwingSession());
					}
					for (SwingInstance si : sim.getAllClosedInstances()) {
						result.getClosedSessions().add(si.toSwingSession());
					}
				}
			}
			return result;
		}
		return null;
	}

	@GET
	@Path("/admin/session")
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
	@Path("/admin/session")
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
