package org.webswing.toolkit;

import sun.awt.CausedFocusEvent;
import sun.awt.LightweightFrame;
import sun.swing.JLightweightFrame;
import sun.swing.SwingAccessor;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;

public class WebLWFramePeer8 extends WebFramePeer {
	private int oldWidth;
	private int oldHeight;
	private int oldX;
	private int oldY;

	@Override
	public Insets getInsets() {
		return new Insets(0, 0, 0, 0);
	}

	public WebLWFramePeer8(LightweightFrame var1) {
		super(var1);
	}

	private LightweightFrame getLwTarget() {
		return (LightweightFrame) this.target;
	}

	public Graphics getGraphics() {
		return this.getLwTarget().getGraphics();
	}

	public void show() {
		this.postEvent(new ComponentEvent((Component) this.getTarget(), ComponentEvent.COMPONENT_SHOWN));
	}

	public void hide() {
		this.postEvent(new ComponentEvent((Component) this.getTarget(), ComponentEvent.COMPONENT_HIDDEN));
	}

	public void reshape(int x, int y, int w, int h) {
		setBounds(x, y, w, h, 0);
	}

	public void setBounds(int x, int y, int w, int h, int op) {
		boolean resized = (w != this.oldWidth) || (h != this.oldHeight);
		boolean moved = (x != this.oldX) || (y != this.oldY);
		if (resized) {
			ComponentEvent e = new ComponentEvent((Component) target, ComponentEvent.COMPONENT_RESIZED);
			postEvent(e);
		}
		if (moved) {
			ComponentEvent e = new ComponentEvent((Component) target, ComponentEvent.COMPONENT_MOVED);
			postEvent(e);
		}

		if ((x != this.oldX) || (y != this.oldY) || (w != this.oldWidth) || (h != this.oldHeight)) {
			this.oldX = x;
			this.oldY = y;
			this.oldWidth = w;
			this.oldHeight = h;
		}
	}

	public void handleEvent(AWTEvent var1) {
		if (var1.getID() == 501) {
			this.emulateActivation(true);
		}

		super.handleEvent(var1);
	}

	public void dispose() {

	}

	@Override
	public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
		return true;
	}

	public void grab() {
		this.getLwTarget().grabFocus();
	}

	public void ungrab() {
		this.getLwTarget().ungrabFocus();
	}

	public void updateCursorImmediately() {
		SwingAccessor.getJLightweightFrameAccessor().updateCursor((JLightweightFrame) this.getLwTarget());
	}

	public boolean isLightweightFramePeer() {
		return true;
	}

	//	public void addDropTarget(DropTarget var1) {
	//		this.getLwTarget().addDropTarget(var1);
	//	}
	//
	//	public void removeDropTarget(DropTarget var1) {
	//		this.getLwTarget().removeDropTarget(var1);
	//	}
}
