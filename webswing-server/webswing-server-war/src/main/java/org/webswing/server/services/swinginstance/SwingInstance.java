package org.webswing.server.services.swinginstance;

import org.webswing.model.MsgIn;
import org.webswing.model.MsgOut;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.server.common.model.SwingConfig;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.rest.resources.model.SwingSession;
import org.webswing.server.services.websocket.WebSocketConnection;

public interface SwingInstance {
	String getOwnerId();

	String getInstanceId();

	String getConnectionId();

	String getMirrorConnectionId();

	SwingConfig getAppConfig();

	void connectSwingInstance(WebSocketConnection r, ConnectionHandshakeMsgIn h);

	void sendToWeb(MsgOut o);

	boolean sendToSwing(WebSocketConnection r, MsgIn h);

	void notifyExiting();

	void shutdown(boolean force);

	void kill(int waitMs);

	void webSessionDisconnected(String connectionId);

	void logStatValue(String name, Number value);
	
	boolean isStatisticsLoggingEnabled();

	SwingSession toSwingSession(boolean stats);

	void logWarningHistory();

	boolean isRecording();

	void startRecording() throws WsException;

	void stopRecording() throws WsException;

	String getThreadDump(String id);

	void requestThreadDump();
	
	void toggleStatisticsLogging(boolean enabled);

	String getUserId();
}
