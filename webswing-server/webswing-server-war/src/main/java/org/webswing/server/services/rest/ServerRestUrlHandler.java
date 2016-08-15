package org.webswing.server.services.rest;

import org.webswing.server.base.UrlHandler;

public class ServerRestUrlHandler extends AbstractRestUrlHandler {

	public ServerRestUrlHandler(UrlHandler parent) {
		super(parent);
	}
//
//	@GET
//	@Path("/settings")
//	public ServerProperties getSetting() throws WsException {
//		checkPermission(WebswingAction.rest_getServerSettings);
//
//		ServerProperties result = new ServerProperties();
//		result.setTempFolder(System.getProperty(Constants.TEMP_DIR_PATH));
//		result.setJmsServerUrl(System.getProperty(Constants.JMS_URL, Constants.JMS_URL_DEFAULT));
//		result.setConfigFile(CommonUtil.getConfigFile(false).toURI().toString());
//		result.setWarLocation(CommonUtil.getWarFileLocation());
//		result.setPort(System.getProperty(Constants.SERVER_PORT));
//		result.setHost(System.getProperty(Constants.SERVER_HOST));
//		return result;
//	}

}
