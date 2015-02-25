package org.webswing.server.handler.admin;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.managed.ManagedServiceInterceptor;
import org.atmosphere.config.service.DeliverTo;
import org.atmosphere.config.service.DeliverTo.DELIVER_TO;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.ShiroInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.admin.c2s.ApplyConfigurationMsgIn;
import org.webswing.model.admin.s2c.AdminConsoleFrameMsgOut;
import org.webswing.server.ConfigurationManager;
import org.webswing.server.SwingInstanceManager;
import org.webswing.server.handler.SwingAsyncManagedService;
import org.webswing.server.model.EncodedMessage;
import org.webswing.server.util.ServerUtil;

@ManagedService(path = "/async/admin", interceptors = { AtmosphereResourceLifecycleInterceptor.class, ManagedServiceInterceptor.class, TrackMessageSizeInterceptor.class, HeartbeatInterceptor.class, SuspendTrackerInterceptor.class, ShiroInterceptor.class })
public class AdminAsyncManagedService implements ConfigurationManager.ConfigurationChangeListener, SwingInstanceManager.SwingInstanceChangeListener {

	private static final Logger log = LoggerFactory.getLogger(AdminAsyncManagedService.class);

	public static final Object BROADCAST_LOCK = new Object();

	private Map<String, AtmosphereResource> resourceMap = new HashMap<String, AtmosphereResource>();

	public AdminAsyncManagedService() {
		ConfigurationManager.getInstance().registerListener(this);
		SwingInstanceManager.getInstance().setChangeListener(this);
	}

	@Ready
	@DeliverTo(DELIVER_TO.RESOURCE)
	public void onReady(final AtmosphereResource r) {
		Subject sub = SecurityUtils.getSubject();
		if (sub.hasRole(Constants.ADMIN_ROLE)) {
			resourceMap.put(r.uuid(), r);
			broadcast(createAdminConsoleUpdate(true, true));
		} else {
			log.warn("Unauthorized connection atempt from " + sub.getPrincipal());
			try {
				r.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}

	@Disconnect
	public void onDisconnect(AtmosphereResourceEvent event) {
		if (resourceMap.containsKey(event.getResource().uuid())) {
			resourceMap.remove(event.getResource().uuid());
			SwingInstanceManager.getInstance().notifySessionDisconnected(event.getResource().uuid());
		}
	}

	@Message
	public Serializable onMessage(AtmosphereResource r, Object message) {
		try {
			Serializable result = SwingAsyncManagedService.processWebswingMessage(r, message, false);
			if (result != null) {
				return result;
			} else if (message instanceof String) {
				Object jsonMessage = ServerUtil.decode((String) message);
				if (jsonMessage != null && jsonMessage instanceof ApplyConfigurationMsgIn) {
					ApplyConfigurationMsgIn jac = (ApplyConfigurationMsgIn) jsonMessage;
					if (jac.getType().equals(ApplyConfigurationMsgIn.Type.user)) {
						ServerUtil.validateUserFile(jac.getConfigContent());
						ConfigurationManager.getInstance().applyUserProperties(jac.getConfigContent());
						return ServerUtil.composeAdminSuccessReply("User configuration saved successfully.");
					} else if (jac.getType().equals(ApplyConfigurationMsgIn.Type.config)) {
						ServerUtil.validateConfigFile(jac.getConfigContent());
						ConfigurationManager.getInstance().applyApplicationConfiguration(jac.getConfigContent());
						return ServerUtil.composeAdminSuccessReply("Server configuration saved successfully.");
					}
				}
			}
		} catch (Exception e) {
			log.error("Exception while processing websocket message.", e);
			return ServerUtil.composeAdminErrorReply(e);
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
		broadcast(createAdminConsoleUpdate(true, false));
	}

	@Override
	public void swingInstancesChangedStats() {
		broadcast(createAdminConsoleUpdate(true, false));
	}

	@Override
	public void notifyChange() {
		broadcast(createAdminConsoleUpdate(false, true));
	}

	public void broadcast(AdminConsoleFrameMsgOut msg) {
		Broadcaster bc = getBroadcaster();
		if (bc != null) {
			synchronized (BROADCAST_LOCK) {
				for (AtmosphereResource r : bc.getAtmosphereResources()) {
					ServerUtil.broadcastMessage(r, new EncodedMessage(msg));
				}
			}
		}
	}

	private AdminConsoleFrameMsgOut createAdminConsoleUpdate(boolean swingInstances, boolean configuration) {
		AdminConsoleFrameMsgOut message;
		if (swingInstances) {
			message = SwingInstanceManager.getInstance().extractStatus();
		} else {
			message = new AdminConsoleFrameMsgOut();
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
