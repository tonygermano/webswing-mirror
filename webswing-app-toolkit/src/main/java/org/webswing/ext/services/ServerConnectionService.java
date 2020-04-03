package org.webswing.ext.services;

import java.awt.Component;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

import org.webswing.model.s2c.AccessibilityMsg;
import org.webswing.toolkit.api.lifecycle.ShutdownReason;

public interface ServerConnectionService {

	void sendObject(Serializable jsonPaintRequest);

	Object sendObjectSync(Serializable o, String correlationId) throws TimeoutException, IOException;

	void disconnect();

	void resetInactivityTimers();

	void messageApiPublish(Serializable o) throws IOException;

	void scheduleShutdown(ShutdownReason admin);

	AccessibilityMsg getAccessibilityInfo();

	AccessibilityMsg getAccessibilityInfo(Component c, int x, int y);
}
