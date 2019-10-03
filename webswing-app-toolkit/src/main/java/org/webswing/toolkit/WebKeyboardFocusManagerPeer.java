package org.webswing.toolkit;

import java.awt.*;
import java.awt.peer.KeyboardFocusManagerPeer;

import org.webswing.model.s2c.FocusEventMsg;
import org.webswing.toolkit.util.Util;

import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

@SuppressWarnings("restriction")
public class WebKeyboardFocusManagerPeer implements KeyboardFocusManagerPeer {

	private static CaretListener caretListener = new CaretListener() {
		@Override
		public void caretUpdate(CaretEvent e) {
			Util.getWebToolkit().getPaintDispatcher().notifyFocusEvent(getFocusEvent());
		}
	};

	@Override
	public void clearGlobalFocusOwner(Window activeWindow) {
	}

	@Override
	public Component getCurrentFocusOwner() {
		Window window = Util.getWebToolkit().getWindowManager().getActiveWindow();
		if(window!=null){
			return window.getFocusOwner();
		}
		return null;
	}

	@Override
	public Window getCurrentFocusedWindow() {
		return Util.getWebToolkit().getWindowManager().getActiveWindow();
	}

	@Override
	public void setCurrentFocusOwner(Component comp) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Component o = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				if (o instanceof JTextComponent) {
					JTextComponent tc = (JTextComponent) o;
					tc.removeCaretListener(caretListener);
					tc.addCaretListener(caretListener);
				}
				Util.getWebToolkit().getPaintDispatcher().notifyFocusEvent(getFocusEvent());
			}
		});
	}

	private static FocusEventMsg getFocusEvent(){
		Component o = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		FocusEventMsg msg = new FocusEventMsg();
		if (o != null && o.isShowing()) {
			msg.setType(FocusEventMsg.FocusEventType.focusGained);
			Point l = o.getLocationOnScreen();
			msg.setX(l.x);
			msg.setY(l.y);
			Rectangle b = o.getBounds();
			msg.setW(b.width);
			msg.setH(b.height);
			if (o instanceof JTextComponent) {
				JTextComponent tc = (JTextComponent) o;
				int position = tc.getCaretPosition();
				try {
					Rectangle location = tc.modelToView(position);
					if (location != null) {
						msg.setType(FocusEventMsg.FocusEventType.focusWithCarretGained);
						msg.setCaretX(location.x);
						msg.setCaretY(location.y);
						msg.setCaretH(location.height);
						msg.setEditable(tc.isEditable());
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			if(o instanceof JPasswordField){
				msg.setType(FocusEventMsg.FocusEventType.focusPasswordGained);
			}
		} else {
			msg.setType(FocusEventMsg.FocusEventType.focusLost);
		}
		return msg;
	}

	@Override
	public void setCurrentFocusedWindow(Window win) {
	}

}
