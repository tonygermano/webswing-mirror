package org.webswing.dispatch;

import org.webswing.model.MsgIn;
import org.webswing.toolkit.WebDragSourceContextPeer;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;

public interface EventDispatcher {
	void dispatchEvent(final MsgIn event);

	void dispatchEventInSwing(final Component c, final AWTEvent e);

	Point getLastMousePosition();

	boolean isDndInProgress();

	void dragStart(WebDragSourceContextPeer peer, Transferable transferable, int actions, long[] formats);

	boolean isJavaFXdragStarted();

	void setJavaFXdragStarted(boolean b);
}
