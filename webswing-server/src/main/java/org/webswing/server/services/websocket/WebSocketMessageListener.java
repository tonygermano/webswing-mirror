package org.webswing.server.services.websocket;

public interface WebSocketMessageListener {

	void onReady(WebSocketConnection c);

	void onMessage(WebSocketConnection connection, Object message);

	void onDisconnect(WebSocketConnection  connection);

	void onTimeout(WebSocketConnection connection);

}
