package org.webswing.server.services.rest;

import org.webswing.server.base.UrlHandler;

public interface RestService {

	public RestUrlHandler createRestHandler(UrlHandler parent);
}
