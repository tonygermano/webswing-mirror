package org.webswing.server.services.rest;

import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;

public interface RestHandlerService {

	AbstractUrlHandler createAdminRestHandler(UrlHandler parent, SwingInstanceHolder instanceHolder);

}
