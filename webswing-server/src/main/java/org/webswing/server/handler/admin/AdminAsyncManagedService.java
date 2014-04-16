package org.webswing.server.handler.admin;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.managed.ManagedServiceInterceptor;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.config.service.Ready.DELIVER_TO;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.ShiroInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.webswing.Constants;
import org.webswing.model.admin.s2c.JsonAdminConsoleFrame;
import org.webswing.server.ConfigurationManager;
import org.webswing.server.SwingInstanceManager;
import org.webswing.server.coder.SwingJsonCoder;

@ManagedService(path = "/async/admin", interceptors = { AtmosphereResourceLifecycleInterceptor.class, ManagedServiceInterceptor.class, TrackMessageSizeInterceptor.class, HeartbeatInterceptor.class, SuspendTrackerInterceptor.class, ShiroInterceptor.class })
public class AdminAsyncManagedService implements ConfigurationManager.ConfigurationChangeListener, SwingInstanceManager.SwingInstanceChangeListener {

    private Map<String, AtmosphereResource> resourceMap = new HashMap<String, AtmosphereResource>();

    public AdminAsyncManagedService() {
        ConfigurationManager.getInstance().setListener(this);
        SwingInstanceManager.getInstance().setChangeListener(this);
    }

    @Ready(value = DELIVER_TO.RESOURCE, encoders = { SwingJsonCoder.class })
    public Serializable onReady(final AtmosphereResource r) {
        if (SecurityUtils.getSubject().hasRole(Constants.ADMIN_ROLE)) {
            resourceMap.put(r.uuid(), r);
            JsonAdminConsoleFrame result = createAdminConsoleUpdate(true, true);
            return result;
        } else {
            try {
                r.close();
            } catch (IOException e) {
                // do nothing
            }
        }
        return null;
    }

    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        if (resourceMap.containsKey(event.getResource().uuid())) {
            resourceMap.remove(event.getResource().uuid());
        }
    }

    @Message(encoders = { SwingJsonCoder.class }, decoders = { SwingJsonCoder.class })
    public Serializable onMessage(Object message) {
        if (message instanceof JsonAdminConsoleFrame) {
            return (JsonAdminConsoleFrame) message;
        }
        return null;
    }

    private Broadcaster getBroadcaster() {
        if (resourceMap.keySet().size() > 0) {
            return resourceMap.get(resourceMap.keySet().iterator().next()).getBroadcaster();
        } else {
            return null;
        }
    }

    @Override
    public void swingInstancesChanged() {
        Broadcaster bc = getBroadcaster();
        if (bc != null) {
            bc.broadcast(createAdminConsoleUpdate(true, false));
        }
    }

    @Override
    public void notifyChange() {
        Broadcaster bc = getBroadcaster();
        if (bc != null) {
            bc.broadcast(createAdminConsoleUpdate(false, true));
        }
    }

    private JsonAdminConsoleFrame createAdminConsoleUpdate(boolean swingInstances, boolean configuration) {
        JsonAdminConsoleFrame message;
        if(swingInstances){
            message =SwingInstanceManager.getInstance().extractStatus();
        }else{
            message = new JsonAdminConsoleFrame();
        }
        if (configuration) {
            message.setConfiguration(ConfigurationManager.getInstance().loadApplicationConfiguration());
            message.setConfigurationBackup(ConfigurationManager.getInstance().loadApplicationConfigurationBackup());
            message.setLiveConfiguration(ConfigurationManager.getInstance().getLiveConfiguration());
            message.setUserConfig(ConfigurationManager.getInstance().loadUserProperties());
            message.setServerProperties(ConfigurationManager.getInstance().getServerProperties());
        }
        return message;
    }
}
