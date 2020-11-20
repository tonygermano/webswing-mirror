package org.webswing.server.api.services.swinginstance.impl;

import org.webswing.model.common.in.ConnectionHandshakeMsgIn;
import org.webswing.server.api.services.sessionpool.ServerSessionPoolConnector;
import org.webswing.server.api.services.sessionpool.SessionPoolHolderService;
import org.webswing.server.api.services.swinginstance.ConnectedSwingInstance;
import org.webswing.server.api.services.swinginstance.SwingInstanceFactory;
import org.webswing.server.api.services.swinginstance.SwingInstanceInfo;
import org.webswing.server.api.services.websocket.BrowserWebSocketConnection;
import org.webswing.server.model.exception.WsException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SwingInstanceFactoryImpl implements SwingInstanceFactory {

	@Inject
	public SwingInstanceFactoryImpl(SessionPoolHolderService sessionPoolHolderService) {
	}

	@Override
	public ConnectedSwingInstance create(BrowserWebSocketConnection r, ConnectionHandshakeMsgIn h, SwingInstanceInfo instanceInfo, ServerSessionPoolConnector serverSessionPoolConnector) throws WsException {
		return new SwingInstanceImpl(r, h, instanceInfo, serverSessionPoolConnector);
	}
	
}
