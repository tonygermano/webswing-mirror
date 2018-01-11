package org.webswing.server.services.rest;

import org.atmosphere.annotation.WebSocketProtocolServiceProcessor;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.base.WebswingService;
import org.webswing.server.model.exception.WsInitException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;

@Singleton
public class RestServiceImpl implements RestService{

	@Inject
	public RestServiceImpl(ServletContext ctx) {
	}

	public RestUrlHandler createRestHandler(UrlHandler parent, Object... registrations) {
		return new RestUrlHandlerImpl(parent,registrations);
	}
}
