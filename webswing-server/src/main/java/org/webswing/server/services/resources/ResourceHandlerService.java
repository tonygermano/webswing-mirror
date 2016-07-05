package org.webswing.server.services.resources;

import org.webswing.server.base.UrlHandler;

public interface ResourceHandlerService {
	ResourceHandler create(UrlHandler manager, String overlayPath);
}
