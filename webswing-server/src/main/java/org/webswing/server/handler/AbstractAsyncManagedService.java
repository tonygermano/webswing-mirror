package org.webswing.server.handler;

import static org.atmosphere.util.IOUtils.readEntirely;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter.OnClose;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter.OnResume;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter.OnSuspend;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.util.IOUtils;
import org.atmosphere.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.MsgIn;
import org.webswing.model.c2s.CopyEventMsgIn;
import org.webswing.model.c2s.InputEventMsgIn;
import org.webswing.model.c2s.InputEventsFrameMsgIn;
import org.webswing.model.c2s.PasteEventMsgIn;
import org.webswing.model.c2s.UploadedEventMsgIn;
import org.webswing.model.jslink.JavaEvalRequestMsgIn;
import org.webswing.model.jslink.JsResultMsg;
import org.webswing.model.s2c.AppFrameMsgOut;
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.server.SwingInstanceManager;
import org.webswing.server.model.EncodedMessage;
import org.webswing.server.util.ServerUtil;
import org.webswing.server.util.StatUtils;

abstract public class AbstractAsyncManagedService implements AtmosphereHandler {

	private static final Logger log = LoggerFactory.getLogger(AbstractAsyncManagedService.class);

	private Map<String, String> clientIdMap = new ConcurrentHashMap<String, String>();

	public void onReady(final AtmosphereResource r) {
		boolean includeAdminApp = ServerUtil.isUserinRole(r, Constants.ADMIN_ROLE);
		AppFrameMsgOut appInfo = new AppFrameMsgOut();
		appInfo.setApplications(getApplicationList(r, includeAdminApp));
		appInfo.setSessionId(r.uuid());
		EncodedMessage encoded = new EncodedMessage(appInfo);
		if (r.forceBinaryWrite()) {
			r.write(encoded.getProtoMessage());
		} else {
			r.write(encoded.getJsonMessage());
		}
	}

	protected List<ApplicationInfoMsg> getApplicationList(AtmosphereResource r, boolean includeAdmin) {
		return ServerUtil.createApplicationInfoMsg(r, includeAdmin);
	}

	public void onDisconnect(AtmosphereResourceEvent event) {
		clientIdMap.remove(event.getResource().uuid());
		SwingInstanceManager.getInstance().notifySessionDisconnected(event.getResource().uuid());
	}

	public void onMessage(AtmosphereResource r, Object message) {
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
							clientIdMap.put(r.uuid(), evt.getHandshake().getClientId());
							SwingInstanceManager.getInstance().connectSwingInstance(r, evt.getHandshake());
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
			StatUtils.logInboundData(SwingInstanceManager.getInstance().findInstance(clientIdMap.get(r.uuid())), length);
		} catch (Exception e) {
			log.error("Exception while processing websocket message.", e);
		}
	}

	public void onTimeout(AtmosphereResourceEvent event) {
	}

	private void send(AtmosphereResource r, MsgIn o) {
		SwingInstanceManager.getInstance().sendMessageToSwing(r, clientIdMap.get(r.uuid()), o);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void onRequest(final AtmosphereResource resource) throws IOException {
		AtmosphereRequest request = resource.getRequest();
		String method = request.getMethod();
		boolean polling = Utils.pollableTransport(resource.transport());
		boolean webSocketMessage = Utils.webSocketMessage(resource);

		if (!webSocketMessage && !polling) {
			resource.addEventListener(new OnSuspend() {
				@Override
				public void onSuspend(AtmosphereResourceEvent event) {
					onReady(event.getResource());
					resource.removeEventListener(this);
				}
			});

			resource.addEventListener(new OnResume() {
				@Override
				public void onResume(AtmosphereResourceEvent event) {
					onResume(event);
					resource.removeEventListener(this);
				}
			});

			resource.addEventListener(new OnClose() {
				@Override
				public void onClose(AtmosphereResourceEvent event) {
					onDisconnect(event);
				}
			});
		}
		if (method.equalsIgnoreCase("post")) {
			Object body = null;
			body = readEntirely(resource);
			if (body != null && body instanceof String) {
				resource.getRequest().body((String) body);
			} else if (body != null) {
				resource.getRequest().body((byte[]) body);
			}
			onMessage(resource, body);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onStateChange(AtmosphereResourceEvent event) throws IOException {
		AtmosphereResource r = event.getResource();
		AtmosphereResponse response = r.getResponse();
		AtmosphereRequest request = r.getRequest();

		if (event.isCancelled() || event.isClosedByClient()) {
			onDisconnect(event);
		} else if (event.isResumedOnTimeout() || event.isResuming()) {
			onTimeout(event);
		} else if (r.isSuspended()) {
			Object message = event.getMessage();
			boolean writeAsBytes = IOUtils.isBodyBinary(request);
			if (message instanceof List) {
				Iterator<Object> i = ((List<Object>) message).iterator();
				try {
					Object s;
					while (i.hasNext()) {
						s = i.next();
						if (s instanceof String) {
							response.getOutputStream().write(((String) s).getBytes(response.getCharacterEncoding()));
						} else if (s instanceof byte[]) {
							response.getOutputStream().write((byte[]) s);
						} else {
							response.getOutputStream().write(s.toString().getBytes(response.getCharacterEncoding()));
						}
						i.remove();
					}
				} catch (IOException ex) {
					event.setMessage(new ArrayList<String>().addAll((List<String>) message));
					throw ex;
				}
				response.getOutputStream().flush();
			} else {
				response.getOutputStream().write(writeAsBytes ? (byte[]) message : message.toString().getBytes(response.getCharacterEncoding()));
				response.getOutputStream().flush();
			}

			switch (r.transport()) {
			case JSONP:
			case LONG_POLLING:
				r.resume();
				break;
			default:
			}
		}

	}
}
