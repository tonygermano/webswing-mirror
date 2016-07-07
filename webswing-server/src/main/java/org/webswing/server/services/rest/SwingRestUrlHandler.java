package org.webswing.server.services.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.commons.lang.text.StrSubstitutor;
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;
import org.webswing.server.util.GitRepositoryState;
import org.webswing.server.util.ServerUtil;

public class SwingRestUrlHandler extends AbstractRestUrlHandler {
	private static final String default_version = "unresolved";
	private final SwingInstanceHolder instanceHolder;

	public SwingRestUrlHandler(UrlHandler parent,SwingInstanceHolder instanceHolder) {
		super(parent);
		this.instanceHolder = instanceHolder;
	}

	@GET
	@Path("/version")
	public String getVersion() throws WsException {
		checkPermission(WebswingAction.rest_getVersion);
		String describe = GitRepositoryState.getInstance().getDescribe();
		if (describe == null) {
			return default_version;
		}
		return describe;
	}
	
	@GET
	@Path("/apps")
	public List<ApplicationInfoMsg> getApplicationInfo(HttpServletRequest req) throws WsException{
		checkPermission(WebswingAction.rest_getApps);
		List<ApplicationInfoMsg> result = new ArrayList<>();
		StrSubstitutor subs = ServerUtil.getConfigSubstitutor(getUser().getUserId(), null, null, null, null);
		for(SwingDescriptor sd : instanceHolder.getAllConfiguredApps()){
			result.add(ServerUtil.toApplicationInfoMsg(sd, subs));
		}
		return result;
	}

}
