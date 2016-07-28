package org.webswing.server.services.security.modules;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.extension.api.WebswingExtendableSecurityModuleConfig;

public abstract class AbstractUserPasswordSecurityModule<T extends WebswingExtendableSecurityModuleConfig> extends AbstractExtendableSecurityModule<T> {

	public AbstractUserPasswordSecurityModule(T config) {
		super(config);
	}

	@Override
	public void init() {
		super.init();
	}

	public String getPartialTemplateName() {
		return "loginPartial.html";
	}

	public String getPageTemplateName() {
		return "loginPage.html";
	}

	public String getUserName(HttpServletRequest request) {
		return request.getParameter("username");
	}

	public String getPassword(HttpServletRequest request) {
		return request.getParameter("password");
	}

	protected AbstractWebswingUser authenticate(HttpServletRequest request) throws WebswingAuthenticationException {
		String username = getUserName(request);
		String password = getPassword(request);
		if (username != null || password != null) {
			return verifyUserPassword(username, password);
		} else {
			return null;
		}
	}

	protected void serveLoginPage(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException {
		Map<String, Object> variables = new HashMap<>();
		if (exception != null) {
			variables.put("errorMessage", exception.getLocalizedMessage());
		}
		sendHtml(request, response, getPageTemplateName(), variables);
	}

	protected void serveLoginPartial(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException {
		Map<String, Object> variables = new HashMap<>();
		if (exception != null) {
			variables.put("errorMessage", exception.getLocalizedMessage());
		}
		sendHtml(request, response, getPartialTemplateName(), variables);
	}

	public abstract AbstractWebswingUser verifyUserPassword(String user, String password) throws WebswingAuthenticationException;
}
