package org.webswing.server.services.security.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebswingSecurityModule<T extends WebswingCredentials> {

	void init();

	T getCredentials(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException e) throws ServletException, IOException;

	AbstractWebswingUser getUser(T credentials) throws WebswingAuthenticationException;

	void destroy();
}
