package org.webswing.server.services.websocket;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.base.UrlHandler;
import org.webswing.server.base.WebswingService;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;

public interface WebSocketService extends WebswingService {
	WebSocketUrlHandler createBinaryWebSocketHandler(UrlHandler parent, SwingInstanceHolder instanceHolder);

	WebSocketUrlHandler createJsonWebSocketHandler(UrlHandler parent, SwingInstanceHolder instanceHolder);

	WebSocketUrlHandler createPlaybackWebSocketHandler(UrlHandler parent);

	void removeListener(String mapping);

	void serve(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException;


}
