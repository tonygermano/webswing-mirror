package org.webswing.server.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.DefaultBroadcaster;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.ShiroInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.c2s.InputEventsFrameMsgIn;
import org.webswing.model.s2c.AppFrameMsgOut;
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.server.model.EncodedMessage;
import org.webswing.server.services.websocket.WebSocketConnection;
import org.webswing.server.services.websocket.WebSocketMessageListener;
import org.webswing.server.util.ServerUtil;

@AtmosphereHandlerService(path = "/async/swing-play", broadcasterCache = UUIDBroadcasterCache.class, broadcaster = DefaultBroadcaster.class, interceptors = { AtmosphereResourceLifecycleInterceptor.class, SuspendTrackerInterceptor.class, ShiroInterceptor.class })
public class SessionRecordingPlaybackWebSocketMessgeListener implements WebSocketMessageListener {

	private static final Logger log = LoggerFactory.getLogger(SessionRecordingPlaybackWebSocketMessgeListener.class);

	private static Map<String, SessionRecordingPlayback> playbackMap = new HashMap<String, SessionRecordingPlayback>();

	@Override
	public void onReady(WebSocketConnection r) {
		AppFrameMsgOut appInfo = new AppFrameMsgOut();
		List<ApplicationInfoMsg> apps = new ArrayList<ApplicationInfoMsg>();
		if (ServerUtil.isUserinRole(r, Constants.ADMIN_ROLE)) {
			String file = r.getRequest().getParameter("file");
			File recordingFile = new File(file);
			if (recordingFile.exists() && recordingFile.canRead()) {
				SessionRecordingPlayback playback = new SessionRecordingPlayback(r, recordingFile);
				playbackMap.put(r.uuid(), playback);
				apps.add(playback.getApplicationInfo());
			} else {
				log.error("Could not open recording file: " + recordingFile.getAbsolutePath());
			}
		}
		appInfo.setApplications(apps);
		appInfo.setSessionId(r.uuid());
		EncodedMessage encoded = new EncodedMessage(appInfo);
		if (r.isBinary()) {
			r.write(encoded.getProtoMessage());
		} else {
			r.write(encoded.getJsonMessage());
		}
	}

	@Override
	public void onMessage(WebSocketConnection conn, Object message) {
		try {
			SessionRecordingPlayback pb = playbackMap.get(conn.uuid());
			if (pb != null) {
				Object decodedMessage = null;
				if (message instanceof byte[]) {
					decodedMessage = ServerUtil.decodeProto((byte[]) message);
				}
				if (decodedMessage != null && decodedMessage instanceof InputEventsFrameMsgIn) {
					InputEventsFrameMsgIn frame = (InputEventsFrameMsgIn) decodedMessage;
					if (frame.getPlayback() != null) {
						pb.handlePlaybackControl(frame.getPlayback());
					}
				}
			}
		} catch (Exception e) {
			log.error("Exception while processing websocket message.", e);
		}
	}

	@Override
	public void onDisconnect(WebSocketConnection conn) {
		SessionRecordingPlayback pbs = playbackMap.remove(conn.uuid());
		if (pbs != null) {
			pbs.close();
		}
	}

	@Override
	public void onTimeout(WebSocketConnection connection) {
	}
}
