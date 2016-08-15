package org.webswing.server.services.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;
import org.webswing.server.util.GitRepositoryState;

public class SwingRestUrlHandler extends AbstractRestUrlHandler {
	private static final String default_version = "unresolved";
	private final SwingInstanceHolder instanceHolder;

	public SwingRestUrlHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		super(parent);
		this.instanceHolder = instanceHolder;
	}

	@GET
	@Path("/version")
	public String getVersion() throws WsException {
		String describe = GitRepositoryState.getInstance().getDescribe();
		if (describe == null) {
			return default_version;
		}
		return describe;
	}

	@GET
	@Path("/apps")
	public List<ApplicationInfoMsg> getApplicationInfo(HttpServletRequest req) throws WsException {
		checkPermission(WebswingAction.rest_getApps);
		List<ApplicationInfoMsg> result = new ArrayList<>();
		String pathPrefix = getServletContext().getContextPath() == null ? "" : getServletContext().getContextPath();
		String userId = getUser() != null ? getUser().getUserId() : null;
		StrSubstitutor subs = CommonUtil.getConfigSubstitutor(userId, null, null, null, null);
		for (SecuredPathConfig sd : instanceHolder.getAllConfiguredApps()) {
			ApplicationInfoMsg applicationInfoMsg = CommonUtil.toApplicationInfoMsg(pathPrefix, sd, subs);
			if (applicationInfoMsg != null) {
				result.add(applicationInfoMsg);
			}
		}
		return result;
	}

}
