package org.webswing.admin.mock;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Path;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.webswing.admin.AdminConsoleRest;
import org.webswing.model.admin.ServerProperties;
import org.webswing.model.admin.Sessions;
import org.webswing.model.admin.SwingSession;
import org.webswing.model.admin.UserConfiguration;
import org.webswing.model.server.SwingAppletDescriptor;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.model.server.WebswingConfiguration;

@Path("admin")
public class AdminConsoleRestMock implements AdminConsoleRest {

	private static final ObjectMapper mapper = new ObjectMapper();
	static WebswingConfiguration config;
	static Sessions sessions;
	static ServerProperties props;
	static String users;

	@Override
	public WebswingConfiguration getConfig() {
		if (config == null) {
			try {
				File file = new File(AdminConsoleRestMock.class.getClassLoader().getResource("webswing.config").toURI());
				config = mapper.readValue(file, WebswingConfiguration.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return config;
	}

	@Override
	public Sessions getSessions() {
		if (sessions == null) {
			sessions = MockGenerator.createMockData(Sessions.class);
		}
		return sessions;
	}

	@Override
	public ServerProperties getSetting() {
		if (props == null) {
			props = MockGenerator.createMockData(ServerProperties.class);
		}
		return props;
	}

	@Override
	public UserConfiguration getUsers() {
		if (users == null) {
			try {
				users = IOUtils.toString(AdminConsoleRestMock.class.getClassLoader().getResourceAsStream("user.properties"), "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return new UserConfiguration(users);
	}

	@Override
	public void setUsers(UserConfiguration users) {
		AdminConsoleRestMock.users = users.getUsers();
	}

	@Override
	public void setConfig(WebswingConfiguration config) {
		AdminConsoleRestMock.config = config;

	}

	@Override
	public Map<String, String> getVariables() {
		Map<String, String> vars = new HashMap<String, String>();
		for (Object key : System.getProperties().keySet()) {
			vars.put((String) key, System.getProperty((String) key));
		}
		return vars;
	}

	@Override
	public SwingDescriptor getDefault(ApplicationType type) {
		if (ApplicationType.applet.equals(type)) {
			return new SwingAppletDescriptor();
		} else if (ApplicationType.application.equals(type)) {
			return new SwingApplicationDescriptor();
		}
		return null;
	}

	@Override
	public SwingSession getSession(String id) {
		for (SwingSession s : getSessions().getSessions()) {
			if (s.getId().equals(id)) {
				return s;
			}
		}
		return null;
	}

}
