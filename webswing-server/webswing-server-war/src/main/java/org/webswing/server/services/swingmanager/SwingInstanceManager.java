package org.webswing.server.services.swingmanager;

import java.io.File;

import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.services.rest.resources.model.ApplicationInfoMsg;
import org.webswing.server.services.security.login.SecuredPathHandler;
import org.webswing.server.services.stats.StatisticsReader;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swingmanager.instance.SwingInstanceHolder;
import org.webswing.server.services.websocket.WebSocketConnection;

public interface SwingInstanceManager extends SecuredPathHandler, UrlHandler {

	File resolveFile(String name);

	SecuredPathConfig getConfig();

	ApplicationInfoMsg getApplicationInfoMsg();

	void connectView(ConnectionHandshakeMsgIn handshake, WebSocketConnection r);

	void notifySwingClose(SwingInstance swingAppInstance);

	void logStatValue(String instance, String name, Number value);

	StatisticsReader getStatsReader();

	boolean isUserAuthorized();

	void disable();

	SwingInstanceHolder getSwingInstanceHolder();

	String getRecordingsDirPath();
}
