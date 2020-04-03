package org.webswing.server.services.rest.resources.api;

import org.webswing.server.services.rest.resources.RestException;

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
public interface ManageApplicationsApi  {

    /**
     * Create Application
     */
    @GET
    @Path("/rest/create{appPath}")
    public void createApp(@PathParam("appPath") String appPath) throws RestException;

    /**
     * Remove Application
     */
    @GET
    @Path("/rest/remove{appPath}")
    public void removeApp(@PathParam("appPath") String appPath) throws RestException;

    /**
     * Enable Application
     */
    @GET
    @Path("/rest/start{appPath}")
    public void startApp(@PathParam("appPath") String appPath) throws RestException;

    /**
     * Disable Application
     */
    @GET
    @Path("/rest/stop{appPath}")
    public void stopApp(@PathParam("appPath") String appPath) throws RestException;
}

