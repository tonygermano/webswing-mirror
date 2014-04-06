package org.webswing.server.adminconsole.config;

import java.io.Serializable;

import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.config.service.Ready.DELIVER_TO;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.webswing.server.coder.SwingJsonCoder;

@ManagedService(path = "/async/admin/config")
public class ConfigurationAsyncManagedService {

    @Ready(value = DELIVER_TO.RESOURCE, encoders = { SwingJsonCoder.class })
    public Serializable onReady(final AtmosphereResource r) {
        return null;
    }

    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
    }

    @Message(encoders = { SwingJsonCoder.class }, decoders = { SwingJsonCoder.class })
    public Serializable onMessage(Object message) {
        return null;
    }
}