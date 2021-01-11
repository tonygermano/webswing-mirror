package org.webswing.server.api.services.websocket;

import org.webswing.model.common.in.ConnectionHandshakeMsgIn;

public interface PrimaryWebSocketConnection extends BrowserWebSocketConnection {
	
	String uuid();
	
	boolean isRecording();
	
	ConnectionHandshakeMsgIn getReconnectHandshake();
	
	WebSocketUserInfo getUserInfo();
	
	String getPath();
	
}
