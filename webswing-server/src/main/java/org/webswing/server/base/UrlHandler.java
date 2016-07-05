package org.webswing.server.base;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.WebswingUser;

public interface UrlHandler {

	void init();

	void destroy();

	boolean serve(HttpServletRequest req, HttpServletResponse res) throws WsException;

	void registerFirstChildUrlHandler(UrlHandler handler);

	void registerChildUrlHandler(UrlHandler handler);

	void removeChildUrlHandler(UrlHandler Handler);

	ServletContext getServletContext();

	long getLastModified(HttpServletRequest req);

	String getPathMapping();

	String getFullPathMapping();

	String getSecuredPath();

	WebswingUser getUser();


}
