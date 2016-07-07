package org.webswing.server.services.swinginstance;

import org.webswing.model.MsgIn;
import org.webswing.model.MsgOut;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.model.server.admin.SwingSession;
import org.webswing.server.services.websocket.WebSocketConnection;

public interface SwingInstance {

	String getInstanceId();

	String getSessionId();

	String getMirrorSessionId();

	String getClientId();

	String getUser();

	SwingDescriptor getAppConfig();

	void sendToWeb(MsgOut o);

	boolean sendToSwing(WebSocketConnection r, MsgIn h);

	void notifyExiting();

	void shutdown(boolean force);

	void kill(int waitMs);

	void webSessionDisconnected(String connectionId);

	boolean connectPrimaryWebSession(WebSocketConnection r);

	void connectMirroredWebSession(WebSocketConnection r);

	void logInboundData(int length);

	SwingSession toSwingSession();

}