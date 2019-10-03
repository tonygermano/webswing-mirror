package org.webswing.server.services.security.modules.anonym;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAction;

public class AnonymWebswingUser extends AbstractWebswingUser {
	private String userId;

	public AnonymWebswingUser(String userIdParam) {
		this.userId=userIdParam;
	}

	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public Map<String, Serializable> getUserAttributes() {
		return Collections.emptyMap();
	}

	@Override
	public boolean hasRole(String role) {
		return WebswingAction.AccessType.basic.name().equals(role);
	}

	public boolean isAuthenticated() {
		return true;
	}
};