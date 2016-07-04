package org.webswing.server.services.websocket;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.base.WebswingService;

public interface WebSocketService extends WebswingService {
	void registerBinaryWebSocketListener(String mapping, WebSocketMessageListener listener);

	void registerJsonWebSocketListener(String mapping, WebSocketMessageListener listener);

	void removeListener(String mapping);

	boolean canServe(String path);
	
	void serve(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException;

}
