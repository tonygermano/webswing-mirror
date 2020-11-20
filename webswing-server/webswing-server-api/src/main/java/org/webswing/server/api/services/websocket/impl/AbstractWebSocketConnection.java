package org.webswing.server.api.services.websocket.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.websocket.EndpointConfig;
import javax.websocket.PongMessage;
import javax.websocket.Session;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.webswing.Constants;
import org.webswing.model.Msg;

public abstract class AbstractWebSocketConnection {

	private static int maxMessageSize = Integer.getInteger(Constants.WEBSOCKET_MESSAGE_SIZE, Constants.WEBSOCKET_MESSAGE_SIZE_DEFAULT_VALUE);
	
	private ByteArrayOutputStream partialMsg = new ByteArrayOutputStream();
	
	protected Session session;

	protected void onOpen(Session session, EndpointConfig config) {
		this.session = session;
		session.setMaxBinaryMessageBufferSize(maxMessageSize);
	}

	protected Pair<Msg, Integer> getCompleteMessage(byte[] bytes, boolean last) throws IOException {
		if (bytes == null) {
			return null;
		}
		
		try {
			partialMsg.write(bytes);
			if (last) {
				byte[] array = partialMsg.toByteArray();
				
				return Pair.of(decodeIncomingMessage(array), array.length);
			}
		} finally {
			if (last) {
				try {
					partialMsg.close();
				} catch (IOException e) {
					// ignore
				}
				partialMsg = new ByteArrayOutputStream();
			}
		}
		
		return null;
	}
	
	protected void sendMessage(byte[] encoded) throws IOException {
		synchronized (session) {
			int length = encoded.length;
			if (length > maxMessageSize) {
				int sent = 0;
				while (sent != length) {
					int sendLength = sent + maxMessageSize > length ? length - sent : maxMessageSize;
					session.getBasicRemote().sendBinary(ByteBuffer.wrap(encoded, sent, sendLength), (sent + sendLength) == length);
					sent += sendLength;
				}
			} else {
				session.getBasicRemote().sendBinary(ByteBuffer.wrap(encoded));
			}
		}
	}
	
	protected void onPong(Session session, PongMessage pongMessage, Logger log) {
		ByteBuffer buffer = pongMessage.getApplicationData();
		if (buffer == null) {
			log.warn("Empty pong message received for session [" + session.getId() + "]!");
		}
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		
		String pong = new String(bytes, StandardCharsets.UTF_8);
		if (!Constants.WEBSOCKET_PING_PONG_CONTENT.equals(pong)) {
			log.warn("Error receiving pong message for session [" + session.getId() + "], content received [" + pong + "]!");
		}
	}
	
	protected abstract Msg decodeIncomingMessage(byte[] bytes) throws IOException;
	
}
