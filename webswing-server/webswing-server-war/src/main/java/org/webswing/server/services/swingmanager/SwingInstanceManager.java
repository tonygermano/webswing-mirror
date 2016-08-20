package org.webswing.server.services.swingmanager;

import java.io.File;

import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.admin.ApplicationInfo;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.websocket.WebSocketConnection;

public interface SwingInstanceManager extends SwingInstanceHolder, UrlHandler {

	File resolveFile(String name);

	SecuredPathConfig getConfig();

	ApplicationInfoMsg getApplicationInfoMsg();

	ApplicationInfo getApplicationInfo();

	void notifySwingClose(SwingInstance swingAppInstance);

	void startSwingInstance(WebSocketConnection r, ConnectionHandshakeMsgIn h);

}
