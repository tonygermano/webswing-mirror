package org.webswing.server.services.security.modules.none;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.api.WebswingUser;

public class UnsecuredSecurityModule implements WebswingSecurityModule<WebswingCredentials> {
	public static final String anonymUserName = "anonym";

	public UnsecuredSecurityModule(Map<String, Object> config) {
	}

	@Override
	public void init() {
	}

	@Override
	public WebswingCredentials getCredentials(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException e) {
		return new WebswingCredentials() {

			@Override
			public boolean matchCredentials(WebswingCredentials other) {
				return true;
			}
		};
	}

	@Override
	public WebswingUser getUser(WebswingCredentials token) throws WebswingAuthenticationException {
		return new WebswingUser() {

			@Override
			public String getUserId() {
				return anonymUserName;
			}

			@Override
			public Map<String, String> getUserAttributes() {
				return Collections.emptyMap();
			}

			@Override
			public boolean isPermitted(String permission) {
				return true;
			}
		};
	}

	@Override
	public void destroy() {
	}

}
