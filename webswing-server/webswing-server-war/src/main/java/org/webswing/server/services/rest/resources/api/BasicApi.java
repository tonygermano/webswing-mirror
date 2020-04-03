package org.webswing.server.services.rest.resources.api;

import org.webswing.server.services.rest.resources.RestException;
import org.webswing.server.services.rest.resources.model.ApplicationInfo;
import org.webswing.server.services.rest.resources.model.ApplicationInfoMsg;
import org.webswing.server.services.rest.resources.model.BasicApplicationInfo;
import java.io.File;
import org.webswing.server.services.rest.resources.model.Manifest;
import java.util.Map;
import org.webswing.server.services.rest.resources.model.Permissions;
import org.webswing.server.services.rest.resources.model.Sessions;

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
public interface BasicApi  {

    /**
     * URL of admin console
     */
    @GET
    @Path("/rest/adminConsoleUrl")
    @Produces({ "text/plain" })
    public String getAdminConsoleUrl() throws RestException;

    /**
     * Get Applications
     */
    @GET
    @Path("/apps")
    @Produces({ "application/json" })
    public List<ApplicationInfoMsg> getApps() throws RestException;

    /**
     * Get Application Icon
     */
    @GET
    @Path("/appicon")
    @Produces({ "image/png" })
    public File getIcon() throws RestException;

    /**
     * Get Path Info
     */
    @GET
    @Path("/rest/info")
    @Produces({ "application/json" })
    public ApplicationInfo getInfo() throws RestException;

    /**
     * PWA manifest
     */
    @GET
    @Path("/manifest.json")
    @Produces({ "application/json" })
    public Manifest getManifest() throws RestException;

    /**
     * Get Available Applications
     */
    @GET
    @Path("/rest/paths")
    @Produces({ "application/json" })
    public List<BasicApplicationInfo> getPaths() throws RestException;

    /**
     * Get User Permissions
     */
    @GET
    @Path("/rest/permissions")
    @Produces({ "application/json" })
    public Permissions getPermissions() throws RestException;

    /**
     * Get All Sessions
     */
    @GET
    @Path("/rest/sessions")
    @Produces({ "application/json" })
    public Sessions getSessions() throws RestException;

    /**
     * Get Aggregated Stats
     */
    @GET
    @Path("/rest/stats")
    @Produces({ "application/json" })
    public Map<String, Map<String, BigDecimal>> getStats() throws RestException;

    /**
     * Webswing Server Version
     */
    @GET
    @Path("/rest/version")
    @Produces({ "text/plain" })
    public String getVersion() throws RestException;

    /**
     * Ping
     */
    @GET
    @Path("/rest/ping")
    public void ping() throws RestException;
}

