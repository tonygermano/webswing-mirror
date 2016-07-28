package org.webswing.server.services.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.extension.onetimeurl.OneTimeUrlSecurityExtension;
import org.webswing.server.services.security.extension.onetimeurl.OneTimeUrlSecurityExtensionConfig;
import org.webswing.server.services.security.login.WebswingSecurityProvider;
import org.webswing.server.services.security.modules.SecurityModuleWrapper;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;
import org.webswing.server.util.ServerUtil;

public class OtpRestUrlHandler extends AbstractRestUrlHandler {
	private static final Logger log = LoggerFactory.getLogger(OtpRestUrlHandler.class);

	private SwingInstanceHolder instanceHolder;

	public OtpRestUrlHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		super(parent);
		this.instanceHolder = instanceHolder;
	}

	@GET
	@Path("/otp")
	public String getOneTimePassword(HttpServletRequest request, @PathParam("") String appPath, @QueryParam("requestorId") String requestor, @QueryParam("user") String user, @QueryParam("roles") String roles, @QueryParam("permissions") String permissions) throws WsException {
		checkPermission(WebswingAction.rest_getOneTimePassword);
		try {
			List<SwingDescriptor> apps = instanceHolder.getAllConfiguredApps();
			SwingDescriptor app = null;
			if (StringUtils.isEmpty(appPath) && apps.size() == 1) {
				app = apps.get(0);
			} else {
				for (SwingDescriptor sd : apps) {
					if (StringUtils.equals(ServerUtil.toPath(sd.getPath()), appPath)) {
						app = sd;
						break;
					}
				}
			}
			if (app != null) {
				WebswingSecurityProvider secProv = instanceHolder.getSecurityProviderForApp(app.getPath());
				if (secProv != null) {
					SecurityModuleWrapper module = secProv.get();
					OneTimeUrlSecurityExtensionConfig config = module.getConfig().getValueAs(OneTimeUrlSecurityExtension.class.getName(), OneTimeUrlSecurityExtensionConfig.class);
					OneTimeUrlSecurityExtension extension = new OneTimeUrlSecurityExtension(config); 
					String[] rolesArray = roles != null ? roles.split(",") : null;
					String[] permissionsArray = permissions != null ? permissions.split(",") : null;
					try {
						String otp = extension.generateOneTimePassword(app.getPath(), requestor, user, rolesArray, permissionsArray);
						return otp;
					} catch (WebswingAuthenticationException e) {
						throw new WsException(e);
					}
				}
			}
		} catch (Exception e) {
			log.error("Failed to generate OTP.", e);
		}
		throw new WsException("Failed to generate OTP.", HttpServletResponse.SC_BAD_REQUEST);
	}
}
