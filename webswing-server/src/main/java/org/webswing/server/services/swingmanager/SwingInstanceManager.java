package org.webswing.server.services.swingmanager;

import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.websocket.WebSocketConnection;

public interface SwingInstanceManager extends SwingInstanceHolder, UrlHandler {

	SwingDescriptor getConfiguration();

	void setConfig(SwingDescriptor newConfig) throws WsException;

	void connectSwingInstance(WebSocketConnection r, ConnectionHandshakeMsgIn handshake);

	void notifySwingClose(SwingInstance swingAppInstance);

}
