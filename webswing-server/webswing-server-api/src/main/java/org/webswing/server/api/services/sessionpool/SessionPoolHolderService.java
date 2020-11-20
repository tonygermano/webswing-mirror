package org.webswing.server.api.services.sessionpool;

import org.webswing.model.adminconsole.in.AdminConsoleFrameMsgIn;
import org.webswing.model.common.in.ConnectionHandshakeMsgIn;
import org.webswing.server.api.base.WebswingService;
import org.webswing.server.api.services.swinginstance.SwingInstanceInfo;
import org.webswing.server.api.services.websocket.AdminConsoleWebSocketConnection;
import org.webswing.server.api.services.websocket.ApplicationWebSocketConnection;
import org.webswing.server.api.services.websocket.BrowserWebSocketConnection;
import org.webswing.server.common.datastore.WebswingDataStoreModule;
import org.webswing.server.model.exception.WsException;

public interface SessionPoolHolderService extends WebswingService {
	
	void registerSessionPool(ServerSessionPoolConnector sessionPool);
	
	void unregisterSessionPool(ServerSessionPoolConnector sessionPool);
	
	boolean registerAdminConsole(AdminConsoleWebSocketConnection connection);
	
	void unregisterAdminConsole(AdminConsoleWebSocketConnection connection);
	
	void handleAdminConsoleMessage(AdminConsoleFrameMsgIn frame, AdminConsoleWebSocketConnection connection);
	
	boolean connectApplication(ApplicationWebSocketConnection connection, boolean reconnect);
	
	void registerReconnect(String instanceId, BrowserWebSocketConnection r);
	
	void unregisterReconnect(BrowserWebSocketConnection r);
	
	void destroy(String path);
	
	void killAll(String path);
	
	void connectView(String path, ConnectionHandshakeMsgIn handshake, BrowserWebSocketConnection r, SwingInstanceInfo instanceInfo) throws WsException;
	
	void logStatValue(String instanceId, String path, String metric, Number value);
	
	void unregisterWithAdminConsole(String path, String instanceId);
	
	void registerWithAdminConsole(String path, String instanceId);
	
	void sendServerInfoUpdate();
	
	WebswingDataStoreModule getDataStore(String path);

	boolean issueAdminConsoleAccessToken(String accessId, String acLoginToken, String servletPrefix);
	
}
