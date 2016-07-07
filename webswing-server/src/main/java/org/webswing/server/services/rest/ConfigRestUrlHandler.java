package org.webswing.server.services.rest;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.webswing.model.server.SwingAppletDescriptor;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.model.server.WebswingConfiguration;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.util.ServerUtil;

public class ConfigRestUrlHandler extends AbstractRestUrlHandler {

	private final ConfigurationService configService;

	public ConfigRestUrlHandler(UrlHandler parent, ConfigurationService configService) {
		super(parent);
		this.configService = configService;
	}

	@GET
	@Path("/config")
	public WebswingConfiguration getConfig() throws WsException {
		checkPermission(WebswingAction.rest_getConfig);
		return configService.getConfiguration();
	}

	@POST
	@Path("/config")
	public void setConfig(WebswingConfiguration config) throws Exception {
		checkPermission(WebswingAction.rest_setConfig);
		configService.setConfiguration(config); 
	}

	@GET
	@Path("/config/variables")
	public Map<String, String> getVariables() throws WsException {
		checkPermission(WebswingAction.rest_getConfigVariables);

		String userName = getUser() == null ? "<webswing user>" : getUser().getUserId();
		Map<String, String> vars = ServerUtil.getConfigSubstitutorMap(userName, "<webswing client Id>", "<webswing client IP address>", "<webswing client locale>", "<webswing custom args>");
		return vars;
	}

	@GET
	@Path("/config/default")
	public SwingDescriptor getDefault(@PathParam("") String type) throws WsException {
		if (type.equals("/application")) {
			checkPermission(WebswingAction.rest_getDefaultApplicationConfig);
			return new SwingApplicationDescriptor();
		} else if (type.equals("/applet")) {
			checkPermission(WebswingAction.rest_getDefaultAppletConfig);
			return new SwingAppletDescriptor();
		}
		return null;
	}

}
