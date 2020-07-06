package org.webswing.server.services.rest.resources.api;

import org.webswing.server.services.rest.resources.RestException;
import java.io.File;
import org.webswing.server.services.rest.resources.model.LogRequest;
import org.webswing.server.services.rest.resources.model.LogResponse;
import org.webswing.server.services.rest.resources.model.SwingSession;

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
public interface ManageSessionsApi  {

    /**
     * Download Session Logs
     */
    @GET
    @Path("/rest/session/logs")
    @Produces({ "application/octet-stream" })
    public File downloadSessionsLog() throws RestException;

    /**
     * CSRF Token
     */
    @GET
    @Path("/rest/CSRFToken")
    @Produces({ "text/plain" })
    public String generateCsrfToken() throws RestException;

    /**
     * Get session-logging Instances
     */
    @GET
    @Path("/rest/session/logs/instanceIds")
    @Produces({ "application/json" })
    public List<String> getLogInstanceIds() throws RestException;

    /**
     * Get Session Metrics
     */
    @GET
    @Path("/rest/metrics/{uuid}")
    @Produces({ "application/json" })
    public SwingSession getMetrics(@PathParam("uuid") String uuid) throws RestException;

    /**
     * Get Session details
     */
    @GET
    @Path("/rest/session/{id}")
    @Produces({ "application/json" })
    public SwingSession getSession(@PathParam("id") String id) throws RestException;

    /**
     * Request Session Log Content
     */
    @POST
    @Path("/rest/session/logs")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public LogResponse getSessionLogs(LogRequest logRequest) throws RestException;

    /**
     * Get Thread Dump
     */
    @GET
    @Path("/rest/threadDump/{instanceId}")
    @Produces({ "text/plain" })
    public String getThreadDump(@PathParam("instanceId") String instanceId, @QueryParam("timestamp") String timestamp) throws RestException;

    /**
     * Create Thread Dump
     */
    @POST
    @Path("/rest/threadDump/{instanceId}")
    public void requestThreadDump(@PathParam("instanceId") String instanceId) throws RestException;

    /**
     * Request session shutdown
     */
    @DELETE
    @Path("/rest/session/{id}")
    public void shutdown(@PathParam("id") String id, @QueryParam("force") String force) throws RestException;

    /**
     * Start Session Recording
     */
    @GET
    @Path("/rest/record/{id}")
    @Produces({ "application/json" })
    public SwingSession startRecording(@PathParam("id") String id) throws RestException;

    /**
     * Set statistics logging value
     */
    @POST
    @Path("/rest/toggleStatisticsLogging/{instanceId}/{enabled}")
    public void toggleStatisticsLogging(@PathParam("instanceId") String instanceId, @PathParam("enabled") Boolean enabled) throws RestException;
}

