package org.webswing.server.services.swingmanager;

import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.websocket.WebSocketConnection;

public interface SwingInstanceManager extends SwingInstanceHolder, UrlHandler {

	void notifySwingClose(SwingInstance swingAppInstance);

	void startSwingInstance(WebSocketConnection r, ConnectionHandshakeMsgIn h);

}
