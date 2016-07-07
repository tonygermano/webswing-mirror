package org.webswing.server.services.websocket;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.MsgIn;
import org.webswing.model.c2s.CopyEventMsgIn;
import org.webswing.model.c2s.InputEventMsgIn;
import org.webswing.model.c2s.InputEventsFrameMsgIn;
import org.webswing.model.c2s.PasteEventMsgIn;
import org.webswing.model.c2s.UploadedEventMsgIn;
import org.webswing.model.jslink.JavaEvalRequestMsgIn;
import org.webswing.model.jslink.JsResultMsg;
import org.webswing.model.s2c.AppFrameMsgOut;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.EncodedMessage;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swingmanager.SwingInstanceManager;
import org.webswing.server.util.ServerUtil;

public class SwingWebSocketMessageListener implements WebSocketMessageListener {

	private static final Logger log = LoggerFactory.getLogger(SwingWebSocketMessageListener.class);

	private final SwingInstanceManager manager;

	public SwingWebSocketMessageListener(SwingInstanceManager manager) {
		this.manager = manager;
	}

	public void onReady(final WebSocketConnection r) {
		AppFrameMsgOut appInfo = new AppFrameMsgOut();
		StrSubstitutor subs = ServerUtil.getConfigSubstitutor(r.getUser().getUserId(), null, ServerUtil.getClientIp(r), null, null);
		appInfo.setApplications(Arrays.asList(ServerUtil.toApplicationInfoMsg(manager.getConfiguration(), subs)));
		appInfo.setSessionId(r.uuid());
		EncodedMessage encoded = new EncodedMessage(appInfo);
		if (r.isBinary()) {
			r.write(encoded.getProtoMessage());
		} else {
			r.write(encoded.getJsonMessage());
		}
	}

	public void onDisconnect(WebSocketConnection r) {
		SwingInstance instance = manager.findInstanceBySessionId(r.uuid());
		if (instance != null) {
			instance.webSessionDisconnected(r.uuid());
		}
	}

	public void onMessage(WebSocketConnection r, Object message) {
		try {
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
							manager.connectSwingInstance(r, evt.getHandshake());
						} else if (evt.getKey() != null) {
							send(r, evt.getKey());
						} else if (evt.getMouse() != null) {
							send(r, evt.getMouse());
						} else if (evt.getEvent() != null) {
							send(r, evt.getEvent());
						}
					}
				} else if (frame.getPaste() != null) {
					PasteEventMsgIn p = frame.getPaste();
					send(r, p);
				} else if (frame.getCopy() != null) {
					CopyEventMsgIn p = frame.getCopy();
					send(r, p);
				} else if (frame.getUploaded() != null) {
					UploadedEventMsgIn p = frame.getUploaded();
					send(r, p);
				} else if (frame.getJsResponse() != null) {
					JsResultMsg p = frame.getJsResponse();
					send(r, p);
				} else if (frame.getJavaRequest() != null) {
					JavaEvalRequestMsgIn p = frame.getJavaRequest();
					send(r, p);
				}
			} else {
				log.error("Unable to decode message: " + message);
			}
			SwingInstance instance = manager.findInstanceBySessionId(r.uuid());
			if (instance != null) {
				instance.logInboundData(length);
			}
		} catch (Exception e) {
			log.error("Exception while processing websocket message.", e);
		}
	}

	public void onTimeout(WebSocketConnection event) {
	}

	private void send(WebSocketConnection r, MsgIn o) {
		SwingInstance instance = manager.findInstanceBySessionId(r.uuid());
		if (instance != null) {
			instance.sendToSwing(r, o);
		}
	}

	@Override
	public UrlHandler getOwner() {
		return manager;
	}

}
