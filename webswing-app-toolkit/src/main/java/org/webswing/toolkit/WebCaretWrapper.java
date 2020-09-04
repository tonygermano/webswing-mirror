package org.webswing.toolkit;

import javax.swing.event.ChangeListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import java.awt.Graphics;
import java.awt.Point;

public class WebCaretWrapper implements Caret {

	private final Caret original;

	public WebCaretWrapper(Caret c) {
		this.original = c;
		c.setBlinkRate(0);
	}

	@Override
	public void install(JTextComponent c) {
		this.original.install(c);
	}

	@Override
	public void deinstall(JTextComponent c) {
		this.original.deinstall(c);
	}

	@Override
	public void paint(Graphics g) {
		this.original.paint(g);
	}

	@Override
	public void addChangeListener(ChangeListener l) {
		this.original.addChangeListener(l);
	}

	@Override
	public void removeChangeListener(ChangeListener l) {
		this.original.removeChangeListener(l);
	}

	@Override
	public boolean isVisible() {
		return this.original.isVisible();
	}

	@Override
	public void setVisible(boolean v) {
		this.original.setVisible(v);
	}

	@Override
	public boolean isSelectionVisible() {
		return this.original.isSelectionVisible();
	}

	@Override
	public void setSelectionVisible(boolean v) {
		this.original.setSelectionVisible(v);
	}

	@Override
	public void setMagicCaretPosition(Point p) {
		this.original.setMagicCaretPosition(p);
	}

	@Override
	public Point getMagicCaretPosition() {
		return this.original.getMagicCaretPosition();
	}

	@Override
	public void setBlinkRate(int rate) {
		this.original.setBlinkRate(0);
	}

	@Override
	public int getBlinkRate() {
		return this.original.getBlinkRate();
	}

	@Override
	public int getDot() {
		return this.original.getDot();
	}

	@Override
	public int getMark() {
		return this.original.getMark();
	}

	@Override
	public void setDot(int dot) {
		this.original.setDot(dot);
	}

	@Override
	public void moveDot(int dot) {
		this.original.moveDot(dot);
	}
}
