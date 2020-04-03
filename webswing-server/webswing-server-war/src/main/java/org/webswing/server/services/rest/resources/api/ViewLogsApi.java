package org.webswing.server.services.rest.resources.api;

import org.webswing.server.services.rest.resources.RestException;
import org.webswing.server.services.rest.resources.model.BasicApplicationInfo;
import java.io.File;
import org.webswing.server.services.rest.resources.model.LogRequest;
import org.webswing.server.services.rest.resources.model.LogResponse;

import java.util.List;
import javax.ws.rs.*;
import java.math.BigDecimal;


/**
 * Webswing REST API
 *
 * <p>The public REST API provided by Webswing Server
 *
 */
@Path("/")
public interface ViewLogsApi  {

    /**
     * Download Logs
     */
    @GET
    @Path("/rest/logs/{type}")
    @Produces({ "application/octet-stream" })
    public File downloadLog(@PathParam("type") String type) throws RestException;

    /**
     * Get Session-Logging Applications
     */
    @GET
    @Path("/rest/logs/sessionApps")
    @Produces({ "application/json" })
    public List<BasicApplicationInfo> getAppsForSessionLogView() throws RestException;

    /**
     * Request Log Content
     */
    @POST
    @Path("/rest/logs/{type}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public LogResponse getLogs(@PathParam("type") String type, LogRequest logRequest) throws RestException;
}

