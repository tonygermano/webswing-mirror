package org.webswing.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.atmosphere.cpr.AtmosphereResource;
import org.codehaus.jackson.map.ObjectMapper;
import org.webswing.Constants;
import org.webswing.model.admin.s2c.JsonAdminConsoleFrame;
import org.webswing.model.c2s.JsonConnectionHandshake;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.server.util.ServerUtil;

public class SwingInstanceManager {

    private static SwingInstanceManager instance = new SwingInstanceManager();

    private List<SwingInstance> swingInstances = new ArrayList<SwingInstance>();
    private SwingInstanceChangeListener changeListener;

    private SwingInstanceManager() {
        setChangeListener(new SwingInstanceChangeListener() {

            @Override
            public void swingInstancesChanged() {
                try {
                    System.err.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(extractStatus()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static SwingInstanceManager getInstance() {
        return instance;
    }

    private synchronized SwingInstance findSwingInstance(String clientId) {
        for (SwingInstance si : swingInstances) {
            if (si.getClientId().equals(clientId)) {
                return si;
            }
        }
        return null;
    }

    private synchronized void addSwingInstance(SwingInstance si) {
        swingInstances.add(si);
    }

    public void connectSwingInstance(AtmosphereResource resource, JsonConnectionHandshake h) {
        SwingInstance swingInstance = findSwingInstance(h.clientId);
        if (swingInstance == null) {//start new swing app
            SwingApplicationDescriptor app = ConfigurationManager.getInsatnce().getApplication(h.applicationName);
            if (app != null && !h.mirrored) {
                if (!reachedMaxConnections(app)) {
                    swingInstance = new SwingInstance(h, app, resource);
                    addSwingInstance(swingInstance);
                    changeListener.swingInstancesChanged();
                } else {
                    resource.getBroadcaster().broadcast(Constants.TOO_MANY_CLIENTS_NOTIFICATION, resource);
                }
            } else {
                resource.getBroadcaster().broadcast(Constants.CONFIGURATION_ERROR, resource);
            }
        } else {
            if (h.mirrored) {//connect as mirror viewer
                boolean result = swingInstance.registerMirroredWebSession(resource);
                if (!result) {
                    resource.getBroadcaster().broadcast(Constants.APPLICATION_ALREADY_RUNNING, resource);
                }
            } else {//continue old session?
                if (h.sessionId != null && h.sessionId.equals(swingInstance.getSessionId())) {
                    swingInstance.sendToSwing(h);
                } else {
                    boolean result = swingInstance.registerPrimaryWebSession(resource);
                    if (result) {
                        resource.getBroadcaster().broadcast(Constants.CONTINUE_OLD_SESSION_QUESTION, resource);
                        changeListener.swingInstancesChanged();
                    } else {
                        resource.getBroadcaster().broadcast(Constants.APPLICATION_ALREADY_RUNNING, resource);
                    }
                }
            }
        }
    }

    public synchronized void disconnectSwingInstance(String sessionID) {
        if (sessionID != null) {
            for (SwingInstance si : swingInstances) {
                if (sessionID.equals(si.getSessionId())) {
                    si.registerPrimaryWebSession(null);
                } else if (sessionID.equals(si.getMirroredSessionId())) {
                    si.registerMirroredWebSession(null);
                }
            }
            changeListener.swingInstancesChanged();
        }
    }

    public void sendMessageToSwing(String clientId, Serializable o) {
        SwingInstance si = findSwingInstance(clientId);
        if (si != null) {
            si.sendToSwing(o);
        }
    }

    private synchronized boolean reachedMaxConnections(SwingApplicationDescriptor app) {
        if (app.getMaxClients() < 0) {
            return false;
        } else if (app.getMaxClients() == 0) {
            return true;
        } else {
            int count = 0;
            for (SwingInstance si : swingInstances) {
                if (app.getName().equals(si.getApplicationName()) && si.isRunning()) {
                    count++;
                }
            }
            if (count < app.getMaxClients()) {
                return false;
            } else {
                return true;
            }
        }
    }

    public synchronized void notifySwingClose(SwingInstance swingInstance) {
        swingInstances.remove(swingInstance);
    }

    public synchronized JsonAdminConsoleFrame extractStatus() {
        JsonAdminConsoleFrame result = new JsonAdminConsoleFrame();
        for (SwingInstance si : swingInstances) {
            result.getSessions().add(ServerUtil.composeSwingInstanceStatus(si));
        }
        return result;
    }

    public void setChangeListener(SwingInstanceChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public interface SwingInstanceChangeListener {

        void swingInstancesChanged();
    }
}
