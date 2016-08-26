package org.webswing.server.services.rest;

import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RestHandlerServiceImpl implements RestHandlerService {

	@Inject
	public RestHandlerServiceImpl() {
	}

	@Override
	public AbstractUrlHandler createSwingRestHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		return new SwingRestUrlHandler(parent, instanceHolder);
	}

	@Override
	public AbstractUrlHandler createAdminRestHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		return new AdminRestUrlHandler(parent, instanceHolder);
	}

}
