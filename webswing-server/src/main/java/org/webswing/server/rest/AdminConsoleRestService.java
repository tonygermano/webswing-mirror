package org.webswing.server.rest;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.webswing.model.server.SwingDescriptor;
import org.webswing.model.server.WebswingConfiguration;
import org.webswing.model.server.admin.ServerProperties;
import org.webswing.model.server.admin.Sessions;
import org.webswing.model.server.admin.SwingSession;
import org.webswing.model.server.admin.UserConfiguration;

public interface AdminConsoleRestService {

	public static enum ApplicationType {
		applet, application;
	}

	@GET
	@Path("config")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public WebswingConfiguration getConfig();

	@POST
	@Path("config")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setConfig(WebswingConfiguration config);

	@GET
	@Path("config/variables")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Map<String, String> getVariables();

	@GET
	@Path("config/default/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public SwingDescriptor getDefault(@PathParam("type") ApplicationType type);

	@GET
	@Path("sessions")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Sessions getSessions();

	@GET
	@Path("session/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public SwingSession getSession(@PathParam("id") String id);

	@DELETE
	@Path("session/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void shutdown(@PathParam("id") String id,@QueryParam("force")String forceKill);

	@GET
	@Path("settings")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ServerProperties getSetting();

	@GET
	@Path("users")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public UserConfiguration getUsers();

	@POST
	@Path("users")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setUsers(UserConfiguration users);

}
