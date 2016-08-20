package org.webswing.server.services.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.model.admin.ApplicationInfo;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;
import org.webswing.server.services.swingmanager.SwingInstanceManager;

public class AdminRestUrlHandler extends AbstractRestUrlHandler {

	private final SwingInstanceHolder instanceHolder;

	public AdminRestUrlHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		super(parent);
		this.instanceHolder = instanceHolder;
	}

	@GET
	@Path("/admin/apps")
	public List<ApplicationInfo> getApplications(HttpServletRequest req) throws WsException {
		checkPermission(WebswingAction.rest_admin_getApplications);
		List<ApplicationInfo> result = new ArrayList<>();
		for (SwingInstanceManager appManager : instanceHolder.getApplications()) {
			result.add(appManager.getApplicationInfo());
		}
		return result;
	}

}
