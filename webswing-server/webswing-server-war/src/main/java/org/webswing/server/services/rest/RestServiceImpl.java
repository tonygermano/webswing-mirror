package org.webswing.server.services.rest;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.webswing.server.GlobalUrlHandler;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.rest.resources.GlobalRestService;
import org.webswing.server.services.rest.resources.SwingAppRestService;
import org.webswing.server.services.swingmanager.SwingInstanceManager;
import org.webswing.server.services.swingprocess.SwingProcessService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RestServiceImpl implements RestService {

	private final ConfigurationService configService;
	private final SwingProcessService processService;

	@Inject
	public RestServiceImpl(ConfigurationService configService,SwingProcessService processService) {
		this.configService = configService;
		this.processService = processService;
	}

	public RestUrlHandler createGlobalRestHandler(GlobalUrlHandler parent) {
		return new RestUrlHandlerImpl(parent, createRestInjectionBinder(parent), GlobalRestService.class);
	}

	public RestUrlHandler createSwingAppRestHandler(PrimaryUrlHandler parent) {
		return new RestUrlHandlerImpl(parent, createRestInjectionBinder(parent), SwingAppRestService.class);
	}

	public RestUrlHandler createRestHandler(UrlHandler parent, Class... resources) {
		return new RestUrlHandlerImpl(parent, createRestInjectionBinder(parent), resources);
	}

	protected AbstractBinder createRestInjectionBinder(UrlHandler parent){
		AbstractBinder binding = new AbstractBinder() {
			@Override
			protected void configure() {
				bind(parent).to(parent.getClass());
				if(parent instanceof PrimaryUrlHandler){
					bind(parent).to(PrimaryUrlHandler.class);
				}
				if(parent instanceof GlobalUrlHandler){
					bind(parent).to(GlobalUrlHandler.class);
				}
				if(parent instanceof SwingInstanceManager){
					bind(parent).to(SwingInstanceManager.class);
				}
				bind(configService).to(ConfigurationService.class);
				bind(processService).to(SwingProcessService.class);
			}
		};
		return binding;
	}
}
