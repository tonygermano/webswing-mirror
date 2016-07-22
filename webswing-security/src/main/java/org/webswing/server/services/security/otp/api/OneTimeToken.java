package org.webswing.server.services.security.otp.api;

import org.webswing.server.services.security.api.AbstractWebswingUser;

public class OneTimeToken {
	private AbstractWebswingUser user;

	public OneTimeToken(AbstractWebswingUser user) {
		this.user = user;
	}

	public AbstractWebswingUser getUser() {
		return user;
	}
}
