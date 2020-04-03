package org.webswing.server;

import com.google.inject.*;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.server.services.security.SecurityManagerService;
import org.webswing.server.services.startup.StartupService;
import org.webswing.toolkit.util.GitRepositoryState;

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
		log.info("Initializing Webswing " + GitRepositoryState.getInstance().getDescribe());
		Module servletModule = new Module() {
			public void configure(Binder binder) {
				binder.bind(ServletContext.class).toInstance(getServletContext());
			}
		};
				
		try {
			Injector injector;
			Module enterpriseModule = createEnterpriseModule();
			if(enterpriseModule == null) {
				injector = Guice.createInjector(servletModule, new WebswingServerModule());
			} else {
				Module overriddenModule = Modules.override(new WebswingServerModule()).with(enterpriseModule);
				injector = Guice.createInjector(Modules.combine(servletModule,overriddenModule ));
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
	
	private Module createEnterpriseModule(){
		try {
			Class<?> moduleClass = WebswingServerModule.class.getClassLoader().loadClass(Constants.ENTERPRISE_MODULE);
			Module module = (AbstractModule) moduleClass.newInstance();
			log.info("Webswing Enterprise module intialized.");
			return module;
		} catch (Throwable e) {
			log.debug("Enterprise module not available. "+e.getMessage());
			return null;
		}

	}
}
