package org.webswing.model.admin.s2c;

import java.io.Serializable;
import java.util.Date;

public class JsonSwingSession implements Serializable {

    private static final long serialVersionUID = -3986868457733865212L;
    private String id;
    private String user;
    private String application;
    private Date startedAt;
    private Boolean connected;
    private Date disconnectedSince;
    private JsonSwingJvmStats state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getDisconnectedSince() {
        return disconnectedSince;
    }

    public void setDisconnectedSince(Date disconnectedSince) {
        this.disconnectedSince = disconnectedSince;
    }

    public JsonSwingJvmStats getState() {
        return state;
    }

    public void setState(JsonSwingJvmStats state) {
        this.state = state;
    }

}
