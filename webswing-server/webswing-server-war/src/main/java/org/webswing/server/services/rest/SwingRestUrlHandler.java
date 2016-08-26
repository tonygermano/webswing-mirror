package org.webswing.server.services.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;
import org.webswing.server.services.swingmanager.SwingInstanceManager;

public class SwingRestUrlHandler extends AbstractUrlHandler {
	private final SwingInstanceHolder instanceHolder;

	public SwingRestUrlHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		super(parent);
		this.instanceHolder = instanceHolder;
	}

	@Override
	protected String getPath() {
		return "/rest";
	}

	@GET
	@Path("/apps")
	public List<ApplicationInfoMsg> getApplicationInfo(HttpServletRequest req) throws WsException {
		checkPermission(WebswingAction.rest_getApps);
		List<ApplicationInfoMsg> result = new ArrayList<>();
		for (SwingInstanceManager mgr : instanceHolder.getApplications()) {
			ApplicationInfoMsg applicationInfoMsg = mgr.getApplicationInfoMsg();
			if (applicationInfoMsg != null) {
				result.add(applicationInfoMsg);
			}
		}
		return result;
	}
}
