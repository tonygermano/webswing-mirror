package org.webswing.server.services.rest;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.admin.InstanceManagerStatus;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.common.util.ConfigUtil;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;

public class ConfigRestUrlHandler extends AbstractRestUrlHandler {
	private static final Logger log = LoggerFactory.getLogger(ConfigRestUrlHandler.class);

	private final ConfigurationService configService;
	private SwingInstanceHolder instanceHolder;

	public ConfigRestUrlHandler(UrlHandler parent, ConfigurationService configService, SwingInstanceHolder instanceHolder) {
		super(parent);
		this.configService = configService;
		this.instanceHolder = instanceHolder;
	}

	@GET
	@Path("/config")
	public MetaObject getConfig(@PathParam("") String appPath) throws WsException {
		checkPermission(WebswingAction.rest_getConfig);
		Map<String, SecuredPathConfig> config = configService.getConfiguration();
		if (StringUtils.isEmpty(appPath)) {
			appPath = "/";
		}
		SecuredPathConfig securedPathConfig = config.get(appPath);
		if (securedPathConfig == null) {
			securedPathConfig = ConfigUtil.instantiateConfig(null, SecuredPathConfig.class);
		}
		try {
			return ConfigUtil.getConfigMetadata(securedPathConfig, getClass().getClassLoader());
		} catch (Exception e) {
			log.error("Failed to generate configuration descriptor.", e);
			throw new WsException("Failed to generate configuration descriptor.");
		}
	}

	//	@POST
	//	@Path("/config")
	//	public void setConfig(WebswingConfiguration config) throws Exception {
	//		checkPermission(WebswingAction.rest_setConfig);
	//		configService.setConfiguration(config);
	//	}

	@GET
	@Path("/config/variables")
	public Map<String, String> getVariables() throws WsException {
		checkPermission(WebswingAction.rest_getConfigVariables);

		String userName = getUser() == null ? "<webswing user>" : getUser().getUserId();
		Map<String, String> vars = CommonUtil.getConfigSubstitutorMap(userName, "<webswing client Id>", "<webswing client IP address>", "<webswing client locale>", "<webswing custom args>");
		return vars;
	}

	//	@GET
	//	@Path("/config/default")
	//	public SwingConfiguration getDefault(@PathParam("") String type) throws WsException {
	//		if (type.equals("/application")) {
	//			checkPermission(WebswingAction.rest_getDefaultApplicationConfig);
	//			return new SwingApplicationDescriptor();
	//		} else if (type.equals("/applet")) {
	//			checkPermission(WebswingAction.rest_getDefaultAppletConfig);
	//			return new SwingAppletDescriptor();
	//		}
	//		return null;
	//	}

}
