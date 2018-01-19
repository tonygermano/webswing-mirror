package org.webswing.server.services.swingmanager.instance;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SwingInstanceHolderServiceImpl implements SwingInstanceHolderService {

	@Inject
	public SwingInstanceHolderServiceImpl() {
	}

	@Override
	public SwingInstanceHolder createInstanceHolder(String path) {
		return new SwingInstanceHolderImpl();
	}
}
