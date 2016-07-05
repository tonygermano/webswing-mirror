package org.webswing.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.services.security.SecurityManagerService;
import org.webswing.server.services.startup.StartupService;

import com.google.inject.Guice;
import com.google.inject.Injector;

@WebServlet(asyncSupported = true, displayName = "WebswingServlet", urlPatterns = { "/*" })
@MultipartConfig(fileSizeThreshold = 5242880)
public class WebswingServlet extends HttpServlet {
	private static final long serialVersionUID = 1962501775857788874L;

	private StartupService startup;
	private GlobalUrlHandler handler;
	private SecurityManagerService securityManager;

	@Override
	public void init() throws ServletException {
		Injector injector = Guice.createInjector(new WebswingServerModule());
		this.securityManager = injector.getInstance(SecurityManagerService.class);
		this.securityManager.start();
		this.startup = injector.getInstance(StartupService.class);
		this.startup.start();
		this.handler = injector.getInstance(GlobalUrlHandler.class);
		this.handler.setServletContext(getServletContext());
		this.handler.init();
	}

	public void handleRequest(HttpServletRequest req, HttpServletResponse res) {
		securityManager.secure(handler, req, res);
	}

	@Override
	public void destroy() {
		handler.destroy();
		this.startup.stop();
		this.securityManager.stop();
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
}
