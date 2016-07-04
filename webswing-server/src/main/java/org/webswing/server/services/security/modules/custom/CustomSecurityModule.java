package org.webswing.server.services.security.modules.custom;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.api.WebswingUser;

public class CustomSecurityModule implements WebswingSecurityModule<WebswingCredentials> {

	private WebswingSecurityModule<WebswingCredentials> custom;
	private CustomSecurityModuleConfig config;

	public CustomSecurityModule(CustomSecurityModuleConfig config) {
		this.config = config;
	}

	@Override
	public void init() {
		config.getClassPath();
		//create classloader
		config.getClass();
		//load class
		config.getConfig();
		//set config
		custom.init();

	}

	@Override
	public WebswingCredentials getCredentials(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException e) throws ServletException, IOException {
		return custom.getCredentials(request, response, e);
	}

	@Override
	public WebswingUser getUser(WebswingCredentials credentials) throws WebswingAuthenticationException {
		return custom.getUser(credentials);
	}

	@Override
	public void destroy() {
		custom.destroy();
	}

}
