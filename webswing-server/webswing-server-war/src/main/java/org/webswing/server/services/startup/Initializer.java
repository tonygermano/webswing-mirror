package org.webswing.server.services.startup;

import org.webswing.server.model.exception.WsInitException;

public interface Initializer {

	void start() throws WsInitException;

}
