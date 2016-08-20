package org.webswing.server.services.rest;

import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RestHandlerServiceImpl implements RestHandlerService {

	private final ConfigurationService configService;

	@Inject
	public RestHandlerServiceImpl(ConfigurationService configService) {
		this.configService = configService;

	}

	@Override
	public AbstractRestUrlHandler createConfigRestHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		return new ConfigRestUrlHandler(parent, configService, instanceHolder);
	}

	@Override
	public AbstractRestUrlHandler createSwingRestHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		return new SwingRestUrlHandler(parent, instanceHolder);
	}

	@Override
	public AbstractRestUrlHandler createSessionRestHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		return new SessionRestUrlHandler(parent, instanceHolder);
	}

	@Override
	public AbstractRestUrlHandler createServerRestHandler(UrlHandler parent) {
		return new ServerRestUrlHandler(parent);
	}

	@Override
	public AbstractRestUrlHandler createOtpRestHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		return new OneTimeUrlRestUrlHandler(parent, instanceHolder);
	}

	@Override
	public AbstractRestUrlHandler createAdminRestHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		return new AdminRestUrlHandler(parent, instanceHolder);
	}

}
