package org.webswing.server.services.websocket;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.text.StrSubstitutor;
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
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.model.EncodedMessage;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.login.WebswingSecurityProvider;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;
import org.webswing.server.services.swingmanager.SwingInstanceManager;
import org.webswing.server.util.ServerUtil;

public class WebSocketUrlHandlerImpl implements WebSocketUrlHandler {

	private static final Logger log = LoggerFactory.getLogger(WebSocketUrlHandlerImpl.class);

	private final UrlHandler parent;
	private final WebSocketService websocket;
	private final SwingInstanceHolder instanceHolder;
	private final String path;

	public WebSocketUrlHandlerImpl(UrlHandler parent, String path, WebSocketService websocket, SwingInstanceHolder instanceHolder) {
		this.parent = parent;
		this.path = path;
		this.websocket = websocket;
		this.instanceHolder = instanceHolder;
	}

	@Override
	public void init() {
	}

	@Override
	public void destroy() {
		websocket.removeListener(getFullPathMapping());
	}

	@Override
	public boolean serve(HttpServletRequest req, HttpServletResponse res) throws WsException {
		try {
			websocket.serve(req, res);
			return true;
		} catch (Exception e) {
			log.error("WebSocket failed.", e);
			throw new WsException("WebSocket failed.", e);
		}
	}

	public void onReady(final WebSocketConnection r) {
		if (r.hasPermission(WebswingAction.websocket_connect)) {
			AppFrameMsgOut appInfo = new AppFrameMsgOut();
			List<ApplicationInfoMsg> result = new ArrayList<>();
			String userId = r.getUser() != null ? r.getUser().getUserId() : null;
			StrSubstitutor subs = CommonUtil.getConfigSubstitutor(userId, null, null, null, null);
			String pathPrefix = getServletContext().getContextPath() == null ? "" : getServletContext().getContextPath();
			for (SecuredPathConfig sd : instanceHolder.getAllConfiguredApps()) {
				ApplicationInfoMsg applicationInfoMsg = CommonUtil.toApplicationInfoMsg(pathPrefix, sd, subs);
				if (applicationInfoMsg != null) {
					result.add(applicationInfoMsg);
				}
			}
			appInfo.setApplications(result);
			appInfo.setSessionId(r.uuid());
			EncodedMessage encoded = new EncodedMessage(appInfo);
			if (r.isBinary()) {
				r.write(encoded.getProtoMessage());
			} else {
				r.write(encoded.getJsonMessage());
			}
		} else {
			r.broadcastMessage(SimpleEventMsgOut.unauthorizedAccess.buildMsgOut());
			r.disconnect();
		}
	}

	public void onDisconnect(WebSocketConnection r) {
		SwingInstance instance = instanceHolder.findInstanceBySessionId(r.uuid());
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
							SwingInstance existing = instanceHolder.findByInstanceId(evt.getHandshake(), r);
							if (existing != null) {
								existing.connectSwingInstance(r, evt.getHandshake());
							} else {
								if (instanceHolder instanceof SwingInstanceManager) {
									((SwingInstanceManager) instanceHolder).startSwingInstance(r, evt.getHandshake());
								} else {
									r.disconnect();
								}
							}
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
			SwingInstance instance = instanceHolder.findInstanceBySessionId(r.uuid());
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
		SwingInstance instance = instanceHolder.findInstanceBySessionId(r.uuid());
		if (instance != null) {
			instance.sendToSwing(r, o);
		}
	}

	@Override
	public UrlHandler getOwner() {
		return instanceHolder;
	}

	@Override
	public void registerFirstChildUrlHandler(UrlHandler handler) {
	}

	@Override
	public void registerChildUrlHandler(UrlHandler handler) {
	}

	@Override
	public void removeChildUrlHandler(UrlHandler Handler) {
	}

	@Override
	public ServletContext getServletContext() {
		return instanceHolder.getServletContext();
	}

	@Override
	public long getLastModified(HttpServletRequest req) {
		return -1;
	}

	@Override
	public String getPathMapping() {
		return AbstractUrlHandler.toPath(path);
	}

	@Override
	public String getFullPathMapping() {
		return parent.getFullPathMapping() + getPathMapping();
	}

	@Override
	public String getSecuredPath() {
		return parent.getSecuredPath();
	}

	@Override
	public AbstractWebswingUser getUser() {
		return parent.getUser();
	}

	@Override
	public void checkPermission(WebswingAction action) throws WsException {
		parent.checkPermission(action);
	}

	@Override
	public WebswingSecurityProvider getSecurityProvider() {
		return parent.getSecurityProvider();
	}
}
