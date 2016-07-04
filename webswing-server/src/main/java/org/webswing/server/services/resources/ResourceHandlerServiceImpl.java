package org.webswing.server.services.resources;

import org.webswing.server.base.UrlHandler;

import com.google.inject.Singleton;

@Singleton
public class ResourceHandlerServiceImpl implements ResourceHandlerService {

	@Override
	public ResourceHandler create(UrlHandler manager) {
		return new ResourceHandlerImpl(manager);
	}

}
