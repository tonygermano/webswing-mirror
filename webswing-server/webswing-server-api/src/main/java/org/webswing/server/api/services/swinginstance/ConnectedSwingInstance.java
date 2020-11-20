package org.webswing.server.api.services.swinginstance;

import org.webswing.model.adminconsole.out.SwingSessionMsgOut;
import org.webswing.model.adminconsole.out.ThreadDumpMsgOut;
import org.webswing.model.app.in.ServerToAppFrameMsgIn;
import org.webswing.model.app.out.AppToServerFrameMsgOut;
import org.webswing.model.appframe.in.AppFrameMsgIn;
import org.webswing.model.browser.in.BrowserToServerFrameMsgIn;
import org.webswing.model.browser.out.ServerToBrowserFrameMsgOut;
import org.webswing.model.common.in.ConnectionHandshakeMsgIn;
import org.webswing.server.api.model.ProcessStatusEnum;
import org.webswing.server.api.services.websocket.ApplicationWebSocketConnection;
import org.webswing.server.api.services.websocket.BrowserWebSocketConnection;
import org.webswing.server.common.model.SecuredPathConfig;

public interface ConnectedSwingInstance extends RemoteSwingInstance {
	
	SecuredPathConfig getConfig();
	
	String getUserId();
	
	String getConnectionId();
	
	void connectBrowser(BrowserWebSocketConnection r, ConnectionHandshakeMsgIn h);
	
	void connectApplication(ApplicationWebSocketConnection r, boolean reconnect);
	
	void browserDisconnected(String connectionId);
	
	void applicationDisconnected(boolean reconnect);
	
	void handleAppMessage(AppToServerFrameMsgOut msgOut);
	
	void handleBrowserMessage(BrowserWebSocketConnection r, BrowserToServerFrameMsgIn msgIn);
	
	boolean sendMessageToBrowser(ServerToBrowserFrameMsgOut msgOut);

	boolean sendMessageToApp(ServerToAppFrameMsgIn msgIn);
	
	boolean sendMessageToApp(AppFrameMsgIn msgIn);
	
	void notifyUserConnected();
	
	void notifyExiting();
	
	void close();

	SwingSessionMsgOut toSwingSession();

	boolean isStatisticsLoggingEnabled();

	void logWarningHistory();

	void toggleRecording();
	
	ThreadDumpMsgOut getThreadDump(String timestamp);
	
	void toggleStatisticsLogging(boolean enabled);
	
	boolean isRunning();

	String getPathMapping();

	String getAppName();

	void updateProcessStatus(ProcessStatusEnum processStatus);
	
}
