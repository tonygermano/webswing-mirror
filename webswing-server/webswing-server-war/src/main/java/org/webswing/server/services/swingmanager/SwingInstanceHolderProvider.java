package org.webswing.server.services.swingmanager;

import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.websocket.WebSocketConnection;

public interface SwingInstanceHolderProvider  extends SwingInstanceHolder {
	
	SwingInstance findInstanceByInstanceId(ConnectionHandshakeMsgIn handshake, WebSocketConnection r);

	void add(SwingInstance swingInstance);

	int getRunningInstancesCount();

	void remove(SwingInstance swingInstance);

}
