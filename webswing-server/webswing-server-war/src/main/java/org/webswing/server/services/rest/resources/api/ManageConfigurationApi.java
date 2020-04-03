package org.webswing.server.services.rest.resources.api;

import org.webswing.server.services.rest.resources.RestException;
import java.util.Map;
import org.webswing.server.services.rest.resources.model.MetaObject;

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
public interface ManageConfigurationApi  {

    /**
     * Get Config
     */
    @GET
    @Path("/rest/config")
    @Produces({ "application/json" })
    public MetaObject getConfig() throws RestException;

    /**
     * Describe Configuration
     */
    @POST
    @Path("/rest/metaConfig")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public MetaObject getMeta(Map<String, Object> requestBody) throws RestException;

    /**
     * Resolve Variables.
     */
    @GET
    @Path("/rest/variables/resolve/{type}")
    @Produces({ "text/plain" })
    public String resolve(@PathParam("type") String type, @QueryParam("resolve") @DefaultValue("")String resolve) throws RestException;

    /**
     * Set Config
     */
    @POST
    @Path("/rest/config")
    @Consumes({ "application/json" })
    public void saveConfig(Map<String, Object> requestBody) throws RestException;

    /**
     * Find Variables
     */
    @GET
    @Path("/rest/variables/search/{type}")
    @Produces({ "application/json" })
    public Map<String, String> searchVariables(@PathParam("type") String type, @QueryParam("search") @DefaultValue("")String search) throws RestException;
}

