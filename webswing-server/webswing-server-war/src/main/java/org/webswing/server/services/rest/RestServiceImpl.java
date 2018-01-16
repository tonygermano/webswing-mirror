package org.webswing.server.services.rest;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.webswing.server.GlobalUrlHandler;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.rest.resources.GlobalRestService;
import org.webswing.server.services.rest.resources.SwingAppRestService;
import org.webswing.server.services.swingmanager.SwingInstanceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RestServiceImpl implements RestService {

	private ConfigurationService configService;

	@Inject
	public RestServiceImpl(ConfigurationService configService) {
		this.configService = configService;
	}

	public RestUrlHandler createGlobalRestHandler(GlobalUrlHandler parent) {
		AbstractBinder binding = new AbstractBinder() {
			@Override
			protected void configure() {
				bind(parent).to(GlobalUrlHandler.class);
				bind(configService).to(ConfigurationService.class);
			}
		};
		return new RestUrlHandlerImpl(parent, binding, GlobalRestService.class);
	}

	public RestUrlHandler createSwingAppRestHandler(PrimaryUrlHandler parent) {
		AbstractBinder binding = new AbstractBinder() {
			@Override
			protected void configure() {
				bind(parent).to(PrimaryUrlHandler.class);
				bind(parent).to(SwingInstanceManager.class);
				bind(configService).to(ConfigurationService.class);
			}
		};
		return new RestUrlHandlerImpl(parent, binding, SwingAppRestService.class);
	}
}
