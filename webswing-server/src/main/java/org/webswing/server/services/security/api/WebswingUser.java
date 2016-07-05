package org.webswing.server.services.security.api;

import java.util.Collections;
import java.util.Map;

public interface WebswingUser {
	public static final String anonymUserName = "anonym";

	public static final WebswingUser anonym = new WebswingUser() {

		@Override
		public boolean isPermitted(String permission) {
			return true;
		}

		@Override
		public String getUserId() {
			return anonymUserName;
		}

		@Override
		public Map<String, String> getUserAttributes() {
			return Collections.emptyMap();
		}
	};

	String getUserId();

	Map<String, String> getUserAttributes();

	boolean isPermitted(String permission);

}
