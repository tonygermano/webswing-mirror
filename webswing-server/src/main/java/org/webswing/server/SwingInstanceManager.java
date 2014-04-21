package org.webswing.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.admin.s2c.JsonAdminConsoleFrame;
import org.webswing.model.admin.s2c.JsonSwingSession;
import org.webswing.model.c2s.JsonConnectionHandshake;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.server.util.ServerUtil;

public class SwingInstanceManager {

    private static SwingInstanceManager instance = new SwingInstanceManager();
    private static final Logger log = LoggerFactory.getLogger(SwingInstanceManager.class);

    private List<JsonSwingSession> closedInstances = new ArrayList<JsonSwingSession>();
    private Map<String, SwingInstance> swingInstances = new ConcurrentHashMap<String, SwingInstance>();
    private SwingInstanceChangeListener changeListener;

    private SwingInstanceManager() {
    }

    public static SwingInstanceManager getInstance() {
        return instance;
    }

    private synchronized Set<SwingInstance> getSwingInstanceSet() {
        Set<SwingInstance> set = new HashSet<SwingInstance>();
        for (SwingInstance si : swingInstances.values()) {
            set.add(si);
        }
        return set;
    }

    public void connectSwingInstance(AtmosphereResource resource, JsonConnectionHandshake h) {
        SwingApplicationDescriptor app = ConfigurationManager.getInstance().getApplication(h.applicationName);
        if(app==null){
            throw new RuntimeException("Application "+h.applicationName+" is not configured.");
        }
        if (ServerUtil.isUserAuthorizedForApplication(resource, app)) {
            SwingInstance swingInstance = swingInstances.get(h.clientId);
            if (swingInstance == null) {//start new swing app
                if (app != null && !h.mirrored) {
                    if (!reachedMaxConnections(app)) {
                        swingInstance = new SwingInstance(h, app, resource);
                        swingInstances.put(h.clientId, swingInstance);
                        notifySwingChangeChange();
                    } else {
                        resource.getBroadcaster().broadcast(Constants.TOO_MANY_CLIENTS_NOTIFICATION, resource);
                    }
                } else {
                    resource.getBroadcaster().broadcast(Constants.CONFIGURATION_ERROR, resource);
                }
            } else {
                if (h.mirrored) {//connect as mirror viewer
                    notifySessionDisconnected(resource.uuid());//disconnect possible running mirror sessions
                    boolean result = swingInstance.registerMirroredWebSession(resource);
                    if (!result) {
                        resource.getBroadcaster().broadcast(Constants.APPLICATION_ALREADY_RUNNING, resource);
                    }
                } else {//continue old session?
                    if (h.sessionId != null && h.sessionId.equals(swingInstance.getSessionId())) {
                        swingInstance.sendToSwing(resource, h);
                    } else {
                        boolean result = swingInstance.registerPrimaryWebSession(resource);
                        if (result) {
                            resource.getBroadcaster().broadcast(Constants.CONTINUE_OLD_SESSION_QUESTION, resource);
                            notifySwingChangeChange();
                        } else {
                            resource.getBroadcaster().broadcast(Constants.APPLICATION_ALREADY_RUNNING, resource);
                        }
                    }
                }
            }
        }else{
            log.error("Authorization error: User "+ServerUtil.getUserName(resource)+" is not authorized to connect to application "+ app.getName());
        }
    }

    private boolean reachedMaxConnections(SwingApplicationDescriptor app) {
        if (app.getMaxClients() < 0) {
            return false;
        } else if (app.getMaxClients() == 0) {
            return true;
        } else {
            int count = 0;
            for (SwingInstance si : getSwingInstanceSet()) {
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
        closedInstances.add(ServerUtil.composeSwingInstanceStatus(swingInstance));
        swingInstances.remove(swingInstance.getClientId());
        notifySwingChangeChange();
    }

    public synchronized JsonAdminConsoleFrame extractStatus() {
        JsonAdminConsoleFrame result = new JsonAdminConsoleFrame();
        for (SwingInstance si : getSwingInstanceSet()) {
            result.getSessions().add(ServerUtil.composeSwingInstanceStatus(si));
        }
        result.setClosedSessions(closedInstances);
        return result;
    }

    public void notifySessionDisconnected(String uuid) {
        Set<SwingInstance> set = getSwingInstanceSet();
        for (SwingInstance i : set) {
            if (i.getSessionId() != null && i.getSessionId().equals(uuid)) {
                i.registerPrimaryWebSession(null);
            } else if (i.getMirroredSessionId() != null && i.getMirroredSessionId().equals(uuid)) {
                i.registerMirroredWebSession(null);
            }
        }
    }

    public void notifySwingChangeChange() {
        if (changeListener != null) {
            changeListener.swingInstancesChanged();
        }
    }

    public void setChangeListener(SwingInstanceChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public interface SwingInstanceChangeListener {

        void swingInstancesChanged();
    }

    public void sendMessageToSwing(AtmosphereResource r, String clientId, Serializable o) {
        SwingInstance client = swingInstances.get(clientId);
        if (client != null) {
            client.sendToSwing(r, o);
        }
    }
}
