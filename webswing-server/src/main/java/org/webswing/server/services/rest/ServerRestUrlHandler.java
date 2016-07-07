package org.webswing.server.services.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.webswing.Constants;
import org.webswing.model.server.admin.ServerProperties;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.util.ServerUtil;

public class ServerRestUrlHandler extends AbstractRestUrlHandler {

	public ServerRestUrlHandler(UrlHandler parent) {
		super(parent);
	}

	@GET
	@Path("/settings")
	public ServerProperties getSetting() throws WsException {
		checkPermission(WebswingAction.rest_getServerSettings);

		ServerProperties result = new ServerProperties();
		result.setTempFolder(System.getProperty(Constants.TEMP_DIR_PATH));
		result.setJmsServerUrl(System.getProperty(Constants.JMS_URL, Constants.JMS_URL_DEFAULT));
		result.setConfigFile(ServerUtil.getConfigFile(false).toURI().toString());
		result.setWarLocation(ServerUtil.getWarFileLocation());
		result.setPort(System.getProperty(Constants.SERVER_PORT));
		result.setHost(System.getProperty(Constants.SERVER_HOST));
		return result;
	}

}
