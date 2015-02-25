package org.webswing.server.handler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.managed.ManagedServiceInterceptor;
import org.atmosphere.config.service.DeliverTo;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.config.service.DeliverTo.DELIVER_TO;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.ShiroInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.MsgIn;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.c2s.KeyboardEventMsgIn;
import org.webswing.model.c2s.MouseEventMsgIn;
import org.webswing.model.c2s.PasteEventMsgIn;
import org.webswing.model.c2s.UploadedEventMsgIn;
import org.webswing.model.c2s.InputEventMsgIn;
import org.webswing.model.s2c.AppFrameMsgOut;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.server.ConfigurationManager;
import org.webswing.server.SwingInstanceManager;
import org.webswing.server.model.EncodedMessage;
import org.webswing.server.stats.SessionRecorder;
import org.webswing.server.util.ServerUtil;
import org.webswing.server.util.StatUtils;

@ManagedService(path = "/async/swing", interceptors = { AtmosphereResourceLifecycleInterceptor.class, ManagedServiceInterceptor.class, TrackMessageSizeInterceptor.class, HeartbeatInterceptor.class, SuspendTrackerInterceptor.class, ShiroInterceptor.class })
public class SwingAsyncManagedService {

	private static final Logger log = LoggerFactory.getLogger(SwingAsyncManagedService.class);

	private Map<String, AtmosphereResource> resourceMap = new HashMap<String, AtmosphereResource>();

	@Ready
	@DeliverTo(DELIVER_TO.RESOURCE)
	public void onReady(final AtmosphereResource r) {
		resourceMap.put(r.uuid(), r);
		r.getRequest().setAttribute(ApplicationSelectorServlet.APPLICATION_CUSTOM_ARGS, r.getRequest().getSession().getAttribute(ApplicationSelectorServlet.APPLICATION_CUSTOM_ARGS));
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
		AppFrameMsgOut appInfo = new AppFrameMsgOut();
		appInfo.user = ServerUtil.getUserName(r);
		appInfo.applications = ServerUtil.createApplicationJsonInfo(r, applicationsMap, includeAdminApp);
		ServerUtil.broadcastMessage(r, new EncodedMessage(appInfo));
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

	@SuppressWarnings("unchecked")
	public static Serializable processWebswingMessage(AtmosphereResource r, Object message, boolean logStats) {
		if (message instanceof String) {
			String sm = (String) message;
			Object jsonMessage = ServerUtil.decode((String) message);
			if (jsonMessage != null) {
				if (jsonMessage instanceof List) {
					List<InputEventMsgIn> evts = (List<InputEventMsgIn>) jsonMessage;
					for (InputEventMsgIn evt : evts) {
						if (evt.handshake != null) {
							SwingInstanceManager.getInstance().connectSwingInstance(r, evt.handshake);
						} else if (evt.key != null) {
							send(r, evt.key.clientId, evt.key, sm, logStats);
						} else if (evt.mouse != null) {
							send(r, evt.mouse.clientId, evt.mouse, sm, logStats);
						} else if (evt.event != null) {
							send(r, evt.event.clientId, evt.event, sm, logStats);
						}
					}
				} else if (jsonMessage instanceof PasteEventMsgIn) {
					PasteEventMsgIn p = (PasteEventMsgIn) jsonMessage;
					send(r, p.clientId, p, sm, logStats);
				} else if (jsonMessage instanceof UploadedEventMsgIn) {
					UploadedEventMsgIn p = (UploadedEventMsgIn) jsonMessage;
					send(r, p.clientId, p, sm, logStats);
				} else {
					return null;
				}
			} else {
				return sm;
			}
		}
		return null;
	}

	private static void send(AtmosphereResource r, String clientId, MsgIn o, String message, boolean logStat) {
		if (logStat) {
			StatUtils.logInboundData(SwingInstanceManager.getInstance().findInstance(clientId), message.getBytes().length);
		}
		SwingInstanceManager.getInstance().sendMessageToSwing(r, clientId, o);
	}

}
