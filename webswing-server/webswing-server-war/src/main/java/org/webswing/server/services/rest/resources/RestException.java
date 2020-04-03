package org.webswing.server.services.rest.resources;

import org.webswing.server.model.exception.WsException;

public class RestException extends Exception {
	private static final long serialVersionUID = -6339066097142624126L;
	private int reponseCode = 500;

	public RestException(String e) {
		super(e);
	}

	public RestException(Exception e) {
		super(e);
		if(e instanceof WsException){
			this.reponseCode=((WsException) e).getReponseCode();
		}
	}

	public RestException(String msg, int responseCode) {
		super(msg);
		reponseCode = responseCode;
	}

	public RestException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public int getReponseCode() {
		return reponseCode;
	}
}
