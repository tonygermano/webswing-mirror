package org.webswing.server.services.rest;

import org.webswing.server.GlobalUrlHandler;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.base.UrlHandler;

public interface RestService {

	RestUrlHandler createGlobalRestHandler(GlobalUrlHandler parent);

	RestUrlHandler createSwingAppRestHandler(PrimaryUrlHandler parent);

	RestUrlHandler createRestHandler(UrlHandler parent, Class... resources);
}
