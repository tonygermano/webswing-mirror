package org.webswing.server.services.security;

import java.util.Map;

import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.api.WebswingUser;

public class WebswingPrincipal extends WebswingPermission {
	private WebswingUser user;

	public WebswingPrincipal(String securityPath, WebswingUser user) {
		super(securityPath, null);
		this.user = user;
	}

	public String getUserId() {
		return user.getUserId();
	}

	public Map<String, String> getUserAttributes() {
		return user.getUserAttributes();
	}

	public boolean isPermitted(String permission) {
		return user.isPermitted(WebswingAction.valueOf(permission));
	}

	public WebswingUser getUser() {
		return user;
	}

}
