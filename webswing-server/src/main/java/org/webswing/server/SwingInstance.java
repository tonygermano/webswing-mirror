package org.webswing.server;

import java.io.Serializable;
import java.util.Date;

import org.atmosphere.cpr.AtmosphereResource;
import org.webswing.Constants;
import org.webswing.model.admin.s2c.JsonSwingJvmStats;
import org.webswing.model.c2s.JsonConnectionHandshake;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.server.SwingJvmConnection.WebSessionListener;
import org.webswing.server.util.ServerUtil;

public class SwingInstance implements WebSessionListener {

    private String user;
    private AtmosphereResource resource;
    private AtmosphereResource mirroredResource;
    private SwingApplicationDescriptor application;
    private SwingJvmConnection connection;
    private Date disconnectedSince;
    private final Date startedAt = new Date();
    private Date endedAt = new Date();

    public SwingInstance(JsonConnectionHandshake h, SwingApplicationDescriptor app, AtmosphereResource resource) {
        this.application = app;
        this.user = ServerUtil.getUserName(resource);
        registerPrimaryWebSession(resource);
        this.connection = new SwingJvmConnection(h, app, this);
    }

    public boolean registerPrimaryWebSession(AtmosphereResource resource) {
        if (this.resource == null) {
            this.resource = resource;
            this.disconnectedSince = null;
            return true;
        } else {
            if (resource == null) {
                SwingInstance.this.resource = null;
                SwingInstance.this.disconnectedSince = new Date();
                SwingInstanceManager.getInstance().notifySwingChangeChange();
            }
            return false;
        }
    }

    public boolean registerMirroredWebSession(AtmosphereResource resource) {
        if (this.mirroredResource == null) {
            this.mirroredResource = resource;
            return true;
        } else {
            if (resource == null) {
                SwingInstance.this.mirroredResource = null;
            }
            return false;
        }
    }

    public void sendToWeb(Serializable o) {
        if (resource != null) {
            resource.getBroadcaster().broadcast(o, resource);
        }
        if (mirroredResource != null) {
            mirroredResource.getBroadcaster().broadcast(o, mirroredResource);
        }
    }

    public boolean sendToSwing(AtmosphereResource r, Serializable h) {
        if (connection.isRunning()) {
            if (h instanceof String) {
                if (((String) h).startsWith(Constants.PAINT_ACK_PREFIX) && ((resource != null && r.uuid().equals(resource.uuid())) || (resource == null && mirroredResource != null && r.uuid().equals(mirroredResource.uuid())))) {
                    connection.send(h);
                }else if( ((String) h).startsWith(Constants.UNLOAD_PREFIX)){
                    SwingInstanceManager.getInstance().notifySessionDisconnected(r.uuid());
                }else{
                    connection.send(h);
                }
            } else {
                connection.send(h);
            }
            return true;
        }else{
            return false;
        }
    }

    public void notifyClose() {
        endedAt = new Date();
        SwingInstanceManager.getInstance().notifySwingClose(this);
    }

    public String getClientId() {
        return connection.getClientId();
    }

    public String getApplicationName() {
        return application.getName();
    }

    public String getSessionId() {
        if (resource != null) {
            return resource.uuid();
        }
        return null;
    }

    public String getMirroredSessionId() {
        if (mirroredResource != null) {
            return mirroredResource.uuid();
        }
        return null;
    }

    public boolean isRunning() {
        return connection.isRunning();
    }

    public String getUser() {
        return user;
    }

    public Date getDisconnectedSince() {
        return disconnectedSince;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public JsonSwingJvmStats getStats() {
        return connection.getCurrentStatus();
    }

}
