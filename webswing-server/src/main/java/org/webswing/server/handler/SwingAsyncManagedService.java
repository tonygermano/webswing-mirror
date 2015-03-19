package org.webswing.server.handler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.atmosphere.config.managed.ManagedServiceInterceptor;
import org.atmosphere.config.service.DeliverTo;
import org.atmosphere.config.service.DeliverTo.DELIVER_TO;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.CorsInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.ShiroInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.MsgIn;
import org.webswing.model.c2s.InputEventMsgIn;
import org.webswing.model.c2s.InputEventsFrameMsgIn;
import org.webswing.model.c2s.PasteEventMsgIn;
import org.webswing.model.c2s.UploadedEventMsgIn;
import org.webswing.model.s2c.AppFrameMsgOut;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.server.ConfigurationManager;
import org.webswing.server.SwingInstanceManager;
import org.webswing.server.model.EncodedMessage;
import org.webswing.server.stats.SessionRecorder;
import org.webswing.server.util.ServerUtil;
import org.webswing.server.util.StatUtils;

@ManagedService(path = "/async/swing", interceptors = { AtmosphereResourceLifecycleInterceptor.class, ManagedServiceInterceptor.class, HeartbeatInterceptor.class, SuspendTrackerInterceptor.class, ShiroInterceptor.class, CorsInterceptor.class })
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
		appInfo.setApplications(ServerUtil.createApplicationJsonInfo(r, applicationsMap, includeAdminApp));
		EncodedMessage encoded = new EncodedMessage(appInfo);
		if (r.forceBinaryWrite()) {
			r.write(encoded.getProtoMessage());
		} else {
			r.write(encoded.getJsonMessage());
		}
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
		int length = 0;
		Object decodedMessage = null;
		if (message instanceof String) {
			length = ((String) message).getBytes().length;
			decodedMessage = ServerUtil.decodeJson((String) message);
		} else if (message instanceof byte[]) {
			length = ((byte[]) message).length;
			decodedMessage = ServerUtil.decodeProto((byte[]) message);
		}
		if (decodedMessage != null && decodedMessage instanceof InputEventsFrameMsgIn) {
			InputEventsFrameMsgIn frame = (InputEventsFrameMsgIn) decodedMessage;
			if (frame.getEvents() != null && frame.getEvents().size() > 0) {
				List<InputEventMsgIn> evts = frame.getEvents();
				for (InputEventMsgIn evt : evts) {
					if (evt.getHandshake() != null) {
						SwingInstanceManager.getInstance().connectSwingInstance(r, evt.getHandshake());
					} else if (evt.getKey() != null) {
						send(r, evt.getKey().getClientId(), evt.getKey(), length, logStats);
					} else if (evt.getMouse() != null) {
						send(r, evt.getMouse().getClientId(), evt.getMouse(), length, logStats);
					} else if (evt.getEvent() != null) {
						send(r, evt.getEvent().getClientId(), evt.getEvent(), length, logStats);
					}
				}
			} else if (frame.getPaste() != null) {
				PasteEventMsgIn p = frame.getPaste();
				send(r, p.getClientId(), p, length, logStats);
			} else if (frame.getUploaded() != null) {
				UploadedEventMsgIn p = frame.getUploaded();
				send(r, p.getClientId(), p, length, logStats);
			} else {
				return null;
			}
		} else {
			return (Serializable) message;
		}
		return null;
	}

	private static void send(AtmosphereResource r, String clientId, MsgIn o, int length, boolean logStat) {
		if (logStat) {
			StatUtils.logInboundData(SwingInstanceManager.getInstance().findInstance(clientId), length);
		}
		SwingInstanceManager.getInstance().sendMessageToSwing(r, clientId, o);
	}

}
