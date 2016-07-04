package org.webswing.server.model.exception;

import javax.servlet.http.HttpServletResponse;

public class WsException extends Exception {
	private static final long serialVersionUID = -6339066097142624126L;
	private int reponseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

	public WsException(String e) {
		super(e);
	}

	public WsException(Exception e) {
		super(e);
	}

	public WsException(String msg, int responseCode) {
		super(msg);
		reponseCode = responseCode;
	}

	public WsException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public int getReponseCode() {
		return reponseCode;
	}
}
