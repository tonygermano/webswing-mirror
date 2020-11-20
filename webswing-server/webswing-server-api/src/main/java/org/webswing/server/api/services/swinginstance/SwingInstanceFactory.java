package org.webswing.server.api.services.swinginstance;

import org.webswing.model.common.in.ConnectionHandshakeMsgIn;
import org.webswing.server.api.services.sessionpool.ServerSessionPoolConnector;
import org.webswing.server.api.services.websocket.BrowserWebSocketConnection;
import org.webswing.server.model.exception.WsException;

public interface SwingInstanceFactory {

	ConnectedSwingInstance create(BrowserWebSocketConnection r, ConnectionHandshakeMsgIn h, SwingInstanceInfo instanceInfo, ServerSessionPoolConnector serverSessionPoolConnector) throws WsException;
	
}