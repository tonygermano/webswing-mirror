package org.webswing.model.admin.s2c;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.webswing.model.server.WebswingConfiguration;
import org.webswing.model.server.WebswingConfigurationBackup;

public class JsonAdminConsoleFrame implements Serializable {

    private static final long serialVersionUID = -607390486198389246L;

    List<JsonSwingSession> sessions = new ArrayList<JsonSwingSession>();
    List<JsonSwingSession> closedSessions = new ArrayList<JsonSwingSession>();
    WebswingConfiguration configuration;
    WebswingConfigurationBackup configurationBackup;

    public List<JsonSwingSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<JsonSwingSession> sessions) {
        this.sessions = sessions;
    }

    public WebswingConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(WebswingConfiguration configuration) {
        this.configuration = configuration;
    }

    public WebswingConfigurationBackup getConfigurationBackup() {
        return configurationBackup;
    }

    public void setConfigurationBackup(WebswingConfigurationBackup configurationBackup) {
        this.configurationBackup = configurationBackup;
    }

    public List<JsonSwingSession> getClosedSessions() {
        return closedSessions;
    }

    public void setClosedSessions(List<JsonSwingSession> closedSessions) {
        this.closedSessions = closedSessions;
    }

}
