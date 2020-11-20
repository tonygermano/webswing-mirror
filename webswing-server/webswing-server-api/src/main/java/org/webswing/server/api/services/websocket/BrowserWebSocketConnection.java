package org.webswing.server.api.services.websocket;

import org.webswing.model.appframe.out.AppFrameMsgOut;
import org.webswing.model.browser.out.ServerToBrowserFrameMsgOut;
import org.webswing.model.common.in.ConnectionHandshakeMsgIn;
import org.webswing.server.api.services.swinginstance.ConnectedSwingInstance;
import org.webswing.server.common.model.security.WebswingAction;
import org.webswing.server.common.service.security.AbstractWebswingUser;
import org.webswing.server.model.exception.WsException;

public interface BrowserWebSocketConnection extends WebSocketConnection {
	
	void sendMessage(ServerToBrowserFrameMsgOut msgOut, boolean logStats);
	
	void sendMessage(ServerToBrowserFrameMsgOut msgOut);
	
	void sendMessage(AppFrameMsgOut frame);
	
	void instanceConnected(ConnectedSwingInstance instance);
	
	void disconnect(String reason);
	
	String uuid();
	
	WebSocketUserInfo getUserInfo();

	AbstractWebswingUser getUser();

	String getUserId();

	boolean hasPermission(WebswingAction action);
	
	void checkPermissionLocalOrMaster(WebswingAction websocketStartmirrorview) throws WsException;

	boolean isRecording();
	
	String getPath();

	ConnectionHandshakeMsgIn getReconnectHandshake();
	
}
