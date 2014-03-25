package org.webswing.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.config.service.Ready.DELIVER_TO;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.c2s.JsonConnectionHandshake;
import org.webswing.model.c2s.JsonEventKeyboard;
import org.webswing.model.c2s.JsonEventMouse;
import org.webswing.model.c2s.JsonEventPaste;
import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.server.coder.JsonDecoder;
import org.webswing.server.coder.JsonEncoder;
import org.webswing.server.model.SwingApplicationDescriptor;
import org.webswing.server.util.ServerUtil;

@ManagedService(path = "/async/swing")
public class SwingAsyncManagedService {

    private static final Logger log = LoggerFactory.getLogger(SwingAsyncManagedService.class);

    protected static Map<String, SwingJvmConnection> clients = new HashMap<String, SwingJvmConnection>();
    private Map<String, AtmosphereResource> resourceMap = new HashMap<String, AtmosphereResource>();
    private Map<String, SwingApplicationDescriptor> applications = new HashMap<String, SwingApplicationDescriptor>();

    public SwingAsyncManagedService() {
        try {
            applications = ServerUtil.loadApplicationConfiguration();
        } catch (Exception e) {
            log.error("No application configuration to start");
        }
    }

    @Ready(value = DELIVER_TO.RESOURCE, encoders = { JsonEncoder.class })
    public Serializable onReady(final AtmosphereResource r) {
        resourceMap.put(r.uuid(), r);
        JsonAppFrame appInfo = new JsonAppFrame();
        appInfo.applications = ServerUtil.createApplicationJsonInfo(applications);
        return appInfo;
    }

    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        resourceMap.remove(event.getResource().uuid());
    }

    @Message(encoders = { JsonEncoder.class }, decoders = { JsonDecoder.class })
    public Serializable onMessage(Object message) {
        try {
            if (message instanceof JsonConnectionHandshake) {
                JsonConnectionHandshake h = (JsonConnectionHandshake) message;
                if (!clients.containsKey(h.clientId)) {
                    SwingApplicationDescriptor app = applications.get(h.applicationName);
                    if (app != null) {
                        SwingJvmConnection connection = new SwingJvmConnection(h, h.applicationName, app, resourceMap.get(h.sessionId));
                        if (connection.isInitialized()) {
                            clients.put(h.clientId, connection);
                            if (!connection.isNewAppStarted()) {
                                AtmosphereResource resource = resourceMap.get(h.sessionId);
                                if (resource != null) {
                                    resource.getBroadcaster().broadcast(Constants.CONTINUE_OLD_SESSION_QUESTION, resource);
                                }
                            }
                        }
                    }
                } else {
                    if (h.sessionId != null && h.sessionId.equals(clients.get(h.clientId).getSessionId())) {
                        send(h.clientId, h);
                    } else {
                        AtmosphereResource resource = resourceMap.get(h.sessionId);
                        if (resource != null) {
                            resource.getBroadcaster().broadcast(Constants.APPLICATION_ALREADY_RUNNING, resource);
                        }
                    }
                }
            } else if (message instanceof JsonEventKeyboard) {
                JsonEventKeyboard k = (JsonEventKeyboard) message;
                send(k.clientId, k);
            } else if (message instanceof JsonEventMouse) {
                JsonEventMouse m = (JsonEventMouse) message;
                send(m.clientId, m);
            } else if (message instanceof JsonEventPaste) {
                JsonEventPaste p = (JsonEventPaste) message;
                send(p.clientId, p);
            } else if (message instanceof String) {
                String sm = (String) message;
                if (sm.startsWith(Constants.PAINT_ACK_PREFIX)) {
                    send(sm.substring(Constants.PAINT_ACK_PREFIX.length()), sm);
                } else if (sm.startsWith(Constants.UNLOAD_PREFIX)) {
                    send(sm.substring(Constants.UNLOAD_PREFIX.length()), sm);
                } else if (sm.startsWith(Constants.HEARTBEAT_MSG_PREFIX)) {
                    send(sm.substring(Constants.HEARTBEAT_MSG_PREFIX.length()), sm);
                } else if (sm.startsWith(Constants.REPAINT_REQUEST_PREFIX)) {
                    send(sm.substring(Constants.REPAINT_REQUEST_PREFIX.length()), sm);
                } else {
                    return sm;
                }
            } else if (message instanceof JsonAppFrame) {
                return (JsonAppFrame) message;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void send(String clientId, Serializable o) {
        if (clients.containsKey(clientId)) {
            SwingJvmConnection client = clients.get(clientId);
            if (o instanceof String) {
                client.sendMsg((String) o);
            } else {
                client.send(o);
            }
        }

    }

}
