package org.webswing.server.handler;

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
import org.webswing.server.ConfigurationManager;
import org.webswing.server.SwingInstanceManager;
import org.webswing.server.coder.SwingJsonCoder;
import org.webswing.server.util.ServerUtil;

@ManagedService(path = "/async/swing")
public class SwingAsyncManagedService {

    private static final Logger log = LoggerFactory.getLogger(SwingAsyncManagedService.class);

    private Map<String, AtmosphereResource> resourceMap = new HashMap<String, AtmosphereResource>();

    @Ready(value = DELIVER_TO.RESOURCE, encoders = { SwingJsonCoder.class })
    public Serializable onReady(final AtmosphereResource r) {
        resourceMap.put(r.uuid(), r);
        JsonAppFrame appInfo = new JsonAppFrame();
        appInfo.applications = ServerUtil.createApplicationJsonInfo(ConfigurationManager.getInsatnce().getApplications());
        return appInfo;
    }

    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        resourceMap.remove(event.getResource().uuid());
        SwingInstanceManager.getInstance().disconnectSwingInstance(event.getResource().uuid());
    }

    @Message(encoders = { SwingJsonCoder.class }, decoders = { SwingJsonCoder.class })
    public Serializable onMessage(Object message) {
        try {
            if (message instanceof JsonConnectionHandshake) {
                JsonConnectionHandshake h = (JsonConnectionHandshake) message;
                AtmosphereResource resource = resourceMap.get(h.sessionId);
                SwingInstanceManager.getInstance().connectSwingInstance(resource, h);
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
                }  else if (sm.startsWith(Constants.SWING_KILL_SIGNAL)) {
                    send(sm.substring(Constants.SWING_KILL_SIGNAL.length()), sm);
                } else {
                    return sm;
                }
            } else if (message instanceof JsonAppFrame) {
                return (JsonAppFrame) message;
            }
        } catch (Exception e) {
            log.error("Exception while processing websocket message.",e);
        }
        return null;
    }

    private void send(String clientId, Serializable o) {
        SwingInstanceManager.getInstance().sendMessageToSwing(clientId,o);


    }

}
