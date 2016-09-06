package org.webswing.server.services.rest;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.common.util.ConfigUtil;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;

public class AdminRestUrlHandler extends AbstractUrlHandler {
	private static final Logger log = LoggerFactory.getLogger(AdminRestUrlHandler.class);

	public AdminRestUrlHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		super(parent);
	}

	@Override
	protected String getPath() {
		return "/rest";
	}

	@POST
	@Path("/admin/metaObject")
	public MetaObject getMeta(Map<String, Object> json) throws WsException {
		checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);
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
		checkPermissionLocalOrMaster(WebswingAction.rest_getConfig);

		String userName = getUser() == null ? "<webswing user>" : getUser().getUserId();
		Map<String, String> vars = CommonUtil.getConfigSubstitutorMap(userName, "<webswing client Id>", "<webswing client IP address>", "<webswing client locale>", "<webswing custom args>");
		return vars;
	}

}
