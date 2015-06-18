package org.webswing.ext.services;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

import org.webswing.model.MsgOut;

public interface ServerConnectionService {

	void sendObject(Serializable jsonPaintRequest);

	void sendShutdownNotification();

	Object sendObjectSync(MsgOut o, String correlationId) throws TimeoutException, IOException;
}
