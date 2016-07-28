package org.webswing.server.services.security.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebswingSecurityModule {

	void init();

	AbstractWebswingUser doLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

	void destroy();
}
