package org.webswing.server.services.security.api;

/**
 * Exception thrown when user credentials are not valid or can not be verified. 
 */
public class WebswingAuthenticationException extends Exception {
	private static final long serialVersionUID = 5376492025474474131L;

	public WebswingAuthenticationException(String message) {
		super(message);
	}

	public WebswingAuthenticationException(String message,Throwable t) {
		super(message,t);
	}

}
