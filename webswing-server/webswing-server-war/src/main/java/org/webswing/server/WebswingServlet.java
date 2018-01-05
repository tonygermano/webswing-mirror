package org.webswing.server;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.server.services.security.SecurityManagerService;
import org.webswing.server.services.startup.StartupService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(asyncSupported = true, displayName = "WebswingServlet", urlPatterns = { "/*" })
@MultipartConfig(fileSizeThreshold = 5242880)
public class WebswingServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(StartupService.class);

	private static final long serialVersionUID = 1962501775857788874L;

	private StartupService startup;
	private GlobalUrlHandler handler;
	private SecurityManagerService securityManager;

	@Override
	public void init() throws ServletException {
		log.info("Initializing Webswing...");
		Module servletModule = new Module() {
			public void configure(Binder binder) {
				binder.bind(ServletContext.class).toInstance(getServletContext());
			}
		};
				
		try {
			Injector injector;
			if(System.getProperty(Constants.GUICE_BINDING_MODULE) == null) {
				injector = Guice.createInjector(servletModule, new WebswingServerModule());
			} else {
				injector = Guice.createInjector(Modules.combine(servletModule, Modules.override(new WebswingServerModule()).with(createBindingModule())));
			}
				
			this.startup = injector.getInstance(StartupService.class);
			this.startup.start();
			this.securityManager = injector.getInstance(SecurityManagerService.class);
			this.securityManager.start();
			this.handler = injector.getInstance(GlobalUrlHandler.class);
			this.handler.init();
		} catch (Exception e) {
			log.error("Initialization of Webswing failed. ", e);
			destroy();
			throw new ServletException("Webswing failed to start!", e);
		}
	}

	public void handleRequest(HttpServletRequest req, HttpServletResponse res) {
		securityManager.secure(handler, req, res);
	}

	@Override
	public void destroy() {
		if (this.handler != null) {
			this.handler.destroy();
		}
		if (this.startup != null) {
			this.startup.stop();
		}
		if (this.securityManager != null) {
			this.securityManager.stop();
		}
	}

	@Override
	public void doHead(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		handleRequest(req, res);
	}

	@Override
	public void doOptions(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		handleRequest(req, res);
	}

	@Override
	public void doTrace(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		handleRequest(req, res);
	}

	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		handleRequest(req, res);
	}

	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		handleRequest(req, res);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		handleRequest(req, res);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		handleRequest(req, res);
	}

	@Override
	protected long getLastModified(HttpServletRequest req) {
		return handler.getLastModified(req);
	}
	
	private Module createBindingModule() throws InstantiationException, IllegalAccessException {
		Module result = null;
		String moduleClassName = System.getProperty(Constants.GUICE_BINDING_MODULE);

		try {
			result = (AbstractModule) WebswingServerModule.class.getClassLoader().loadClass(moduleClassName)
					.newInstance();
			return result;
		} catch (Exception e) {
			log.warn("Failed to load extension module {} ", moduleClassName);
		}

		return result;
	}
}
