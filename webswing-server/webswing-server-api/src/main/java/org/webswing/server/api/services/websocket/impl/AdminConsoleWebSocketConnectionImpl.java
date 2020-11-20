package org.webswing.server.api.services.websocket.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCode;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.Msg;
import org.webswing.model.adminconsole.in.AdminConsoleFrameMsgIn;
import org.webswing.model.adminconsole.in.AdminConsoleHandshakeMsgIn;
import org.webswing.model.adminconsole.out.AdminConsoleFrameMsgOut;
import org.webswing.server.api.services.sessionpool.SessionPoolHolderService;
import org.webswing.server.api.services.websocket.AdminConsoleWebSocketConnection;
import org.webswing.server.api.services.websocket.util.AdminConsoleWebSocketConfigurator;
import org.webswing.server.common.util.JwtUtil;
import org.webswing.server.common.util.ProtoMapper;

import com.google.inject.Inject;

@ServerEndpoint(value = "/async/adminconsole", configurator = AdminConsoleWebSocketConfigurator.class)
public class AdminConsoleWebSocketConnectionImpl extends AbstractWebSocketConnection implements AdminConsoleWebSocketConnection {
	
	private static final Logger log = LoggerFactory.getLogger(AdminConsoleWebSocketConnectionImpl.class);

	private ProtoMapper protoMapper = new ProtoMapper(ProtoMapper.PROTO_PACKAGE_ADMIN_CONSOLE_FRAME);

	private final SessionPoolHolderService sessionPoolHolderService;
	
	private boolean secured;
	private Timer pingTimer = new Timer(true);
	
	@Inject
	public AdminConsoleWebSocketConnectionImpl(SessionPoolHolderService sessionPoolHolderService) {
		this.sessionPoolHolderService = sessionPoolHolderService;
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		super.onOpen(session, config);
		
		if (!sessionPoolHolderService.registerAdminConsole(this)) {
			disconnect(CloseCodes.CANNOT_ACCEPT, "An admin console is already connected!");
			return;
		}
		
		pingTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (session != null) {
					synchronized (session) {
						try {
							session.getBasicRemote().sendPing(ByteBuffer.wrap(Constants.WEBSOCKET_PING_PONG_CONTENT.getBytes(StandardCharsets.UTF_8)));
						} catch (IllegalArgumentException | IOException e) {
							log.warn("Could not send ping message for session [" + session.getId() + "]", e);
						}
					}
				}
			}
		}, Constants.WEBSOCKET_PING_PONG_INTERVAL, Constants.WEBSOCKET_PING_PONG_INTERVAL);
	}
	
	@OnMessage
	public void onMessage(Session session, byte[] bytes, boolean last) {
		try {
			Pair<Msg, Integer> frameWithLength = super.getCompleteMessage(bytes, last);
			if (frameWithLength == null) {
				// incomplete
				return;
			}
			
			AdminConsoleFrameMsgIn msgIn = (AdminConsoleFrameMsgIn) frameWithLength.getKey();
			
			if (msgIn.getHandshake() != null) {
				AdminConsoleHandshakeMsgIn handshake = msgIn.getHandshake();
				
				try {
					if (!JwtUtil.validateHandshakeToken(handshake.getSecretMessage())) {
						throw new IllegalArgumentException("Invalid token [" + handshake.getSecretMessage() + "] received during handshake!");
					}
					secured = true;
				} catch (Exception e1) {
					log.error("Could not validate handshake secret message! Disconnecting...", e1);
					disconnect(CloseCodes.CANNOT_ACCEPT, "Connection not secured!");
					return;
				}
			} else if (!secured) {
				// we must get handshake first, otherwise we disconnect
				disconnect(CloseCodes.CANNOT_ACCEPT, "Connection not secured!");
				return;
			} else {
				sessionPoolHolderService.handleAdminConsoleMessage(msgIn, this);
			}
		} catch (IOException e) {
			log.error("Could not decode proto message from admin console, session id [" + session.getId() + "]!", e);
		}
	}
	
	@OnMessage
	public void onPong(Session session, PongMessage pongMessage) {
		super.onPong(session, pongMessage, log);
	}
	
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		if (session != null) {
			log.info("Websocket closed to admin console, session [" + session.getId() + "]" 
					+ (closeReason != null ? ", close code [" + closeReason.getCloseCode().getCode() + "], reason [" + closeReason.getReasonPhrase() + "]!" : ""));
		}
		
		sessionPoolHolderService.unregisterAdminConsole(this);
		
		pingTimer.cancel();
	}
	
	@OnError
	public void onError(Session session, Throwable t) {
		if (session != null) {
			log.error("Websocket error in admin console connection, session [" + session.getId() + "]!", t);
		} else {
			log.error("Websocket error in admin console connection, no session!", t);
		}
	}

	@Override
	public void sendMessage(AdminConsoleFrameMsgOut msgOut) {
		try {
			super.sendMessage(protoMapper.encodeProto(msgOut));
		} catch (IOException e) {
			log.error("Error sending msg to admin console, session [" + session.getId() + "]!", e);
		}
	}
	
	@Override
	protected Msg decodeIncomingMessage(byte[] bytes) throws IOException {
		return protoMapper.decodeProto(bytes, AdminConsoleFrameMsgIn.class);
	}
	
	private void disconnect(CloseCode closeCode, String reason) {
		if (session != null && session.isOpen()) {
			try {
				session.close(new CloseReason(closeCode, reason));
			} catch (IOException e) {
				log.error("Failed to destroy websocket admin console connection session [" + session.getId() + "]!", e);
			}
		}
		pingTimer.cancel();
	}
	
}
