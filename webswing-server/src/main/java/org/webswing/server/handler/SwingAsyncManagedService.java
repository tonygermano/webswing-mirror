package org.webswing.server.handler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.managed.ManagedServiceInterceptor;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.config.service.Ready.DELIVER_TO;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.ShiroInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.c2s.JsonConnectionHandshake;
import org.webswing.model.c2s.JsonEventKeyboard;
import org.webswing.model.c2s.JsonEventMouse;
import org.webswing.model.c2s.JsonEventPaste;
import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.server.ConfigurationManager;
import org.webswing.server.SwingInstanceManager;
import org.webswing.server.stats.SessionRecorder;
import org.webswing.server.util.ServerUtil;
import org.webswing.server.util.StatUtils;

@ManagedService(path = "/async/swing", interceptors = { AtmosphereResourceLifecycleInterceptor.class, ManagedServiceInterceptor.class, TrackMessageSizeInterceptor.class, HeartbeatInterceptor.class, SuspendTrackerInterceptor.class, ShiroInterceptor.class })
public class SwingAsyncManagedService {

	private static final Logger log = LoggerFactory.getLogger(SwingAsyncManagedService.class);

	private Map<String, AtmosphereResource> resourceMap = new HashMap<String, AtmosphereResource>();

	@Ready(value = DELIVER_TO.RESOURCE)
	public Serializable onReady(final AtmosphereResource r) {
		resourceMap.put(r.uuid(), r);
		r.getRequest().setAttribute(SessionRecorder.RECORDING_FLAG, r.getRequest().getSession().getAttribute(SessionRecorder.RECORDING_FLAG));
		String preSelectedApplicationName = ServerUtil.getPreSelectedApplication(r.getRequest(), true);
		boolean includeAdminApp = ServerUtil.isUserinRole(r, Constants.ADMIN_ROLE);
		Map<String, SwingApplicationDescriptor> applicationsMap = ConfigurationManager.getInstance().getApplications();
		if (preSelectedApplicationName != null) {
			includeAdminApp = false;
			SwingApplicationDescriptor preSelectedApp = applicationsMap.get(preSelectedApplicationName);
			applicationsMap.clear();
			if (preSelectedApp != null) {
				applicationsMap.put(preSelectedApplicationName, preSelectedApp);
			}
		}
		JsonAppFrame appInfo = new JsonAppFrame();
		appInfo.user = ServerUtil.getUserName(r);
		appInfo.applications = ServerUtil.createApplicationJsonInfo(r, applicationsMap, includeAdminApp);
		return ServerUtil.encode(appInfo);
	}

	@Disconnect
	public void onDisconnect(AtmosphereResourceEvent event) {
		resourceMap.remove(event.getResource().uuid());
		SwingInstanceManager.getInstance().notifySessionDisconnected(event.getResource().uuid());
	}

	@Message
	public Serializable onMessage(AtmosphereResource r, Object message) {
		try {
			return processWebswingMessage(r, message, true);
		} catch (Exception e) {
			log.error("Exception while processing websocket message.", e);
		}
		return null;
	}

	public static Serializable processWebswingMessage(AtmosphereResource r, Object message, boolean logStats) {
		if (message instanceof String) {
			String sm = (String) message;
			Object jsonMessage = ServerUtil.decode((String) message);
			if (jsonMessage != null) {
				if (jsonMessage instanceof JsonConnectionHandshake) {
					JsonConnectionHandshake h = (JsonConnectionHandshake) jsonMessage;
					SwingInstanceManager.getInstance().connectSwingInstance(r, h);
				} else if (jsonMessage instanceof JsonEventKeyboard) {
					JsonEventKeyboard k = (JsonEventKeyboard) jsonMessage;
					send(r, k.clientId, k, sm, logStats);
				} else if (jsonMessage instanceof JsonEventMouse) {
					JsonEventMouse m = (JsonEventMouse) jsonMessage;
					send(r, m.clientId, m, sm, logStats);
				} else if (jsonMessage instanceof JsonEventPaste) {
					JsonEventPaste p = (JsonEventPaste) jsonMessage;
					send(r, p.clientId, p, sm, logStats);
				} else {
					return null;
				}
			} else if (sm.startsWith(Constants.PAINT_ACK_PREFIX)) {
				send(r, sm.substring(Constants.PAINT_ACK_PREFIX.length()), sm, sm, logStats);
			} else if (sm.startsWith(Constants.UNLOAD_PREFIX)) {
				send(r, sm.substring(Constants.UNLOAD_PREFIX.length()), sm, sm, logStats);
			} else if (sm.startsWith(Constants.HEARTBEAT_MSG_PREFIX)) {
				send(r, sm.substring(Constants.HEARTBEAT_MSG_PREFIX.length()), sm, sm, logStats);
			} else if (sm.startsWith(Constants.REPAINT_REQUEST_PREFIX)) {
				send(r, sm.substring(Constants.REPAINT_REQUEST_PREFIX.length()), sm, sm, logStats);
			} else if (sm.startsWith(Constants.SWING_KILL_SIGNAL)) {
				send(r, sm.substring(Constants.SWING_KILL_SIGNAL.length()), sm, sm, logStats);
			} else if (sm.startsWith(Constants.DELETE_FILE_PREFIX)) {
				send(r, sm.substring(Constants.DELETE_FILE_PREFIX.length()), sm, sm, logStats);
			} else if (sm.startsWith(Constants.DOWNLOAD_FILE_PREFIX)) {
				send(r, sm.substring(Constants.DOWNLOAD_FILE_PREFIX.length()), sm, sm, logStats);
			} else {
				return sm;
			}
		}
		return null;
	}

	private static void send(AtmosphereResource r, String clientId, Serializable o, String message, boolean logStat) {
		if (logStat) {
			StatUtils.logInboundData(SwingInstanceManager.getInstance().findInstance(clientId), message.getBytes().length);
		}
		SwingInstanceManager.getInstance().sendMessageToSwing(r, clientId, o);
	}

}
