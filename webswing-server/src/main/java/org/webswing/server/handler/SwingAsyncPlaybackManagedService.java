package org.webswing.server.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.DefaultBroadcaster;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.ShiroInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.c2s.InputEventMsgIn;
import org.webswing.model.c2s.InputEventsFrameMsgIn;
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.server.recording.PlaybackSession;
import org.webswing.server.util.ServerUtil;

@AtmosphereHandlerService(path = "/async/swing-play", broadcasterCache = UUIDBroadcasterCache.class, broadcaster = DefaultBroadcaster.class, interceptors = { AtmosphereResourceLifecycleInterceptor.class, SuspendTrackerInterceptor.class, ShiroInterceptor.class })
public class SwingAsyncPlaybackManagedService extends AbstractAsyncManagedService {
	private static final Logger log = LoggerFactory.getLogger(SwingAsyncPlaybackManagedService.class);

	private static Map<String, PlaybackSession> playbackMap = new HashMap<String, PlaybackSession>();

	@Override
	protected List<ApplicationInfoMsg> getApplicationList(AtmosphereResource r, boolean includeAdmin) {
		List<ApplicationInfoMsg> result = new ArrayList<ApplicationInfoMsg>();
		if (ServerUtil.isUserinRole(r, Constants.ADMIN_ROLE)) {
			String file = r.getRequest().getParameter("file");
			File recordingFile = new File(file);
			if (recordingFile.exists() && recordingFile.canRead()) {
				PlaybackSession playback = new PlaybackSession(r, recordingFile);
				playbackMap.put(r.uuid(), playback);
				result.add(playback.getApplicationInfo());
			} else {
				log.error("Could not open recording file: " + recordingFile.getAbsolutePath());
			}
		}
		return result;
	}

	@Override
	public void onMessage(AtmosphereResource r, Object message) {
		try {
			PlaybackSession pb = playbackMap.get(r.uuid());
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
	public void onDisconnect(AtmosphereResourceEvent event) {
		PlaybackSession pbs = playbackMap.remove(event.getResource().uuid());
		if (pbs != null) {
			pbs.close();
		}
	}
}
