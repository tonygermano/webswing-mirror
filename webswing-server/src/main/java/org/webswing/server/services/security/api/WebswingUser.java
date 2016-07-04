package org.webswing.server.services.security.api;

import java.util.Map;

public interface WebswingUser {

	public String getUserId();
	
	public Map<String, String> getUserAttributes();
	
	public boolean isPermitted(String permission);
}
