package org.webswing.server.rest;

import java.util.Map;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.server.SwingAppletDescriptor;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.model.server.WebswingConfiguration;
import org.webswing.model.server.admin.ServerProperties;
import org.webswing.model.server.admin.Sessions;
import org.webswing.model.server.admin.SwingSession;
import org.webswing.model.server.admin.UserConfiguration;
import org.webswing.server.ConfigurationManager;
import org.webswing.server.SwingInstanceManager;
import org.webswing.server.util.ServerUtil;

@Path("admin")
public class AdminConsoleRestServiceImpl implements AdminConsoleRestService {

	private static final Logger log = LoggerFactory.getLogger(AdminConsoleRestServiceImpl.class);

	@Override
	public WebswingConfiguration getConfig() {
		checkAuthorization();
		return ConfigurationManager.getInstance().getLiveConfiguration();
	}

	@Override
	public void setConfig(WebswingConfiguration config) {
		checkAuthorization();
		try {
			ConfigurationManager.getInstance().applyApplicationConfiguration(config);
		} catch (Exception e) {
			log.error("Saving of configuration failed. ", e);
			throw new WebApplicationException("Failed to save configuration", 500);
		}
	}

	@Override
	public Map<String, String> getVariables() {
		checkAuthorization();
		Map<String, String> vars = ServerUtil.getConfigSubstitutorMap(getUsername(), "<webswing client Id>", "<webswing client IP address>", "<webswing client locale>", "<webswing custom args>");
		return vars;
	}

	@Override
	public SwingDescriptor getDefault(ApplicationType type) {
		checkAuthorization();
		switch (type) {
		case applet:
			return new SwingAppletDescriptor();
		case application:
			return new SwingApplicationDescriptor();
		default:
			return null;
		}
	}

	@Override
	public Sessions getSessions() {
		checkAuthorization();
		return SwingInstanceManager.getInstance().getSessions();
	}

	@Override
	public SwingSession getSession(String id) {
		checkAuthorization();
		return SwingInstanceManager.getInstance().getSession(id);
	}

	@Override
	public void shutdown(String id, String forceKill) {
		checkAuthorization();
		if(Boolean.parseBoolean(forceKill)){
			SwingInstanceManager.getInstance().shutdown(id,true);
		}else{
			SwingInstanceManager.getInstance().shutdown(id,false);
		}
	}

	@Override
	public ServerProperties getSetting() {
		checkAuthorization();
		return ConfigurationManager.getInstance().getServerProperties();
	}

	@Override
	public UserConfiguration getUsers() {
		checkAuthorization();
		return ConfigurationManager.getInstance().loadUserProperties();
	}

	@Override
	public void setUsers(UserConfiguration users) {
		checkAuthorization();
		try {
			ConfigurationManager.getInstance().applyUserProperties(users);
		} catch (Exception e) {
			log.error("Saving of user configuration failed. ", e);
			throw new WebApplicationException("Failed to save user configuration", 500);
		}
	}

	private String getUsername() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			return subject.getPrincipal() + "";
		}
		return null;
	}

	private void checkAuthorization() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			if (subject.hasRole(Constants.ADMIN_ROLE)) {
				return;
			}
		}
		throw new NotAuthorizedException(Response.status(Status.UNAUTHORIZED).build());

	}

}
