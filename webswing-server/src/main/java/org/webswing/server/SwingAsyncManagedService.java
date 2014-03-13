package org.webswing.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.c2s.JsonConnectionHandshake;
import org.webswing.model.c2s.JsonEventKeyboard;
import org.webswing.model.c2s.JsonEventMouse;
import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.server.coder.JsonEncoder;
import org.webswing.server.coder.JsonDecoder;
import org.webswing.server.model.SwingApplicationDescriptor;
import org.webswing.server.util.ServerUtil;

@ManagedService(path = "/async/swing")
public class SwingAsyncManagedService {

    private static final Logger log = LoggerFactory.getLogger(SwingAsyncManagedService.class);

    private Map<String, SwingJvmConnection> clients = new HashMap<String, SwingJvmConnection>();
    private Map<String, AtmosphereResource> resourceMap = new HashMap<String, AtmosphereResource>();
    private Map<String, SwingApplicationDescriptor> applications = new HashMap<String, SwingApplicationDescriptor>();

    public SwingAsyncManagedService() {
        try {
            applications = ServerUtil.loadApplicationConfiguration();
        } catch (Exception e) {
            log.error("No application configuration to start");
        }
    }

    @Ready(value = Ready.DELIVER_TO.RESOURCE)
    public String onReady(final AtmosphereResource r) {
        resourceMap.put(r.uuid(), r);
        return "debug";
    }

    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        resourceMap.remove(event.getResource().uuid());
    }

    @Message(encoders = { JsonEncoder.class }, decoders = { JsonDecoder.class })
    public Serializable onMessage(Object message) {
        if (message instanceof JsonConnectionHandshake) {
            JsonConnectionHandshake h = (JsonConnectionHandshake) message;
            SwingApplicationDescriptor app = applications.get(h.applicationName);
            if (app != null) {
                clients.put(h.clientId, new SwingJvmConnection(h, app, resourceMap.get(h.sessionId).getBroadcaster()));
            }
        } else if (message instanceof JsonEventKeyboard) {
            JsonEventKeyboard k = (JsonEventKeyboard) message;
            clients.get(k.clientId).send(k);
        } else if (message instanceof JsonEventMouse) {
            JsonEventMouse m = (JsonEventMouse) message;
            clients.get(m.clientId).send(m);
        } else if (message instanceof JsonAppFrame) {
            return (JsonAppFrame) message;
        }
        return null;
    }

}
