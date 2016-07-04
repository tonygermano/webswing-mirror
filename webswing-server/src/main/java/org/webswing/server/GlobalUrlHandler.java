package org.webswing.server;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.model.server.WebswingConfiguration;
import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationChangeListener;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.resources.ResourceHandlerService;
import org.webswing.server.services.startup.StartupService;
import org.webswing.server.services.swingmanager.SwingInstanceManager;
import org.webswing.server.services.swingmanager.SwingInstanceManagerService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GlobalUrlHandler extends AbstractUrlHandler {
	private static final Logger log = LoggerFactory.getLogger(StartupService.class);

	private final ConfigurationService config;
	private final SwingInstanceManagerService appFactory;
	private final ResourceHandlerService resourceService;

	private ServletContext servletContext;


	@Inject
	public GlobalUrlHandler(ConfigurationService config, SwingInstanceManagerService appFactory, ResourceHandlerService resourceService) {
		super(null);
		this.config = config;
		this.appFactory = appFactory;
		this.resourceService = resourceService;
	}

	public boolean serve(HttpServletRequest req, HttpServletResponse res) {
		try {
			boolean served = super.serve(req, res);
			if (!served) {
				throw new WsException("Not Found.");
			}
		} catch (Exception e) {
			handleException(e, req, res);
		}
		return true;
	}

	public void init() {
		registerChildUrlHandler(resourceService.create(this));
		reloadConfiguration(config.getLiveConfiguration());
		config.registerChangeListener(new ConfigurationChangeListener() {

			@Override
			public void notifyChange() {
				reloadConfiguration(config.getLiveConfiguration());
			}
		});
		super.init();
	}

	public void destroy() {
		super.destroy();
	}

	public void reloadConfiguration(WebswingConfiguration newConfig) {
		Set<String> installedPathsToRemove = getChildPaths();
		for (SwingDescriptor swing : newConfig.getAllApps()) {
			String pathMapping = toPath(swing.getPath());
			UrlHandler childHandler = getChildHandlerForPath(pathMapping);
			if (childHandler instanceof SwingInstanceManager) {
				SwingInstanceManager oldApp = (SwingInstanceManager) childHandler;
				SwingDescriptor oldConfig = oldApp.getConfiguration();
				if (!oldConfig.equals(swing)) {
					updateApplication(oldApp, swing);
				}
			} else {
				installApplication(swing);
			}
			installedPathsToRemove.remove(pathMapping);
		}
		for (String pathToRemove : installedPathsToRemove) {
			if (getChildHandlerForPath(pathToRemove) instanceof SwingInstanceManager) {
				SwingInstanceManager appToRemove = (SwingInstanceManager) getChildHandlerForPath(pathToRemove);
				uninstallApplication(appToRemove);
			}
		}
	}

	public void installApplication(SwingDescriptor swing) {
		try {
			SwingInstanceManager app = appFactory.createApp(this, swing);
			registerChildUrlHandler(app);
		} catch (Exception e) {
			log.error("Failed to install application '" + swing.getName() + "' (" + swing.getPath() + ").", e);
			//TODO: status to admin console
		}
	}

	public void updateApplication(SwingInstanceManager oldApp, SwingDescriptor newConfig) {
		try {
			oldApp.setConfig(newConfig);
		} catch (WsException e) {
			log.error("Failed to reload application '" + newConfig.getName() + "' (" + newConfig.getPath() + ").", e);
			//TODO: status to admin console
		}
	}

	public void uninstallApplication(SwingInstanceManager appToRemove) {
		try {
			appToRemove.destroy();
			removeChildUrlHandler(appToRemove);
		} catch (Exception e) {
			log.error("Failed to uninstall application '" + appToRemove.getConfiguration().getName() + "' (" + appToRemove.getConfiguration().getPath() + ").", e);
			//TODO: if running, add callback to uninstall when stopped
		}
	}

	private void handleException(Exception e, HttpServletRequest req, HttpServletResponse res) {
		log.debug("Failed to process request. ", e);
		if (!res.isCommitted()) {
			try {
				WsException wse = (WsException) ((e instanceof WsException) ? e : new WsException(e));
				res.sendError(wse.getReponseCode(), wse.getLocalizedMessage());
			} catch (IOException e1) {
				log.debug("Failed send error response to client. ");
			}
		}
	}

	protected String getPath() {
		return null;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

}
