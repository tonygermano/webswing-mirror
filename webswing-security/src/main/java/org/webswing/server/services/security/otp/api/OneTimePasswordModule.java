package org.webswing.server.services.security.otp.api;

import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;

public interface OneTimePasswordModule {

	AbstractWebswingUser verifyOneTimePassword(String otp) throws WebswingAuthenticationException;

	String generateOneTimePassword(String application, String requestingClient, String user, String[] roles, String[] permissions) throws WebswingAuthenticationException;
}
