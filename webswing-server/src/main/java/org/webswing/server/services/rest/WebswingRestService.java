package org.webswing.server.services.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface WebswingRestService {

	@GET
	@Path("version")
	@Produces(MediaType.TEXT_PLAIN)
	public String getVersion();

}
