package org.webswing.server.services.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.base.WebswingService;
import org.webswing.server.extension.ExtensionService;
import org.webswing.server.model.exception.WsInitException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.jms.JmsService;
import org.webswing.server.services.websocket.WebSocketService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Set;

@Singleton
public class StartupServiceImpl implements StartupService {

	private static final Logger log = LoggerFactory.getLogger(StartupServiceImpl.class);

	Set<WebswingService> services;

	@Inject
	public StartupServiceImpl(Set<WebswingService> services) {
		this.services = services;
	}

	public void start() throws WsInitException {

		try {
			for(WebswingService service : services) {
				log.info("Starting service {}", service.getClass().getSimpleName());
				service.start();
			}
		} catch (WsInitException e) {
			throw e;
		} catch (Exception e) {
			throw new WsInitException("Failed to start Webswing. " + e.getMessage(), e);
		}

	}

	public void stop() {
		for(WebswingService service : services)
			service.stop();
	}
}
