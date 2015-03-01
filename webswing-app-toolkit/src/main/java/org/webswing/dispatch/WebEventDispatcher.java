package org.webswing.dispatch;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.webswing.model.MsgIn;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.c2s.KeyboardEventMsgIn;
import org.webswing.model.c2s.MouseEventMsgIn;
import org.webswing.model.c2s.MouseEventMsgIn.MouseEventType;
import org.webswing.model.c2s.PasteEventMsgIn;
import org.webswing.model.c2s.SimpleEventMsgIn;
import org.webswing.model.c2s.UploadEventMsgIn;
import org.webswing.model.c2s.UploadedEventMsgIn;
import org.webswing.toolkit.WebClipboard;
import org.webswing.toolkit.WebDragSourceContextPeer;
import org.webswing.toolkit.extra.DndEventHandler;
import org.webswing.toolkit.extra.WindowManager;
import org.webswing.util.Logger;
import org.webswing.util.Services;
import org.webswing.util.Util;

public class WebEventDispatcher {

	private MouseEvent lastMouseEvent;
	private MouseEvent lastMousePressEvent;
	private Point lastMousePosition = new Point();
	private static final DndEventHandler dndHandler = new DndEventHandler();
	private HashMap<String, String> uploadMap = new HashMap<String, String>();

	public void dispatchEvent(MsgIn event) {
		Logger.debug("WebEventDispatcher.dispatchEvent:", event);

		if (event instanceof MouseEventMsgIn) {
			dispatchMouseEvent((MouseEventMsgIn) event);
		}
		if (event instanceof KeyboardEventMsgIn) {
			dispatchKeyboardEvent((KeyboardEventMsgIn) event);
		}
		if (event instanceof ConnectionHandshakeMsgIn) {
			ConnectionHandshakeMsgIn handshake = (ConnectionHandshakeMsgIn) event;
			Util.getWebToolkit().initSize(handshake.getDesktopWidth(), handshake.getDesktopHeight());
		}
		if (event instanceof SimpleEventMsgIn) {
			SimpleEventMsgIn msg = (SimpleEventMsgIn) event;
			dispatchMessage(msg);
		}
		if (event instanceof PasteEventMsgIn) {
			PasteEventMsgIn paste = (PasteEventMsgIn) event;
			handlePasteEvent(paste.getContent());
		}
		if (event instanceof UploadedEventMsgIn) {
			handleUploadedEvent((UploadedEventMsgIn) event);
		}
		if (event instanceof UploadEventMsgIn) {
			UploadEventMsgIn upload = (UploadEventMsgIn) event;
			JFileChooser dialog = Util.getWebToolkit().getPaintDispatcher().getFileChooserDialog();
			if (dialog != null) {
				File currentDir = dialog.getCurrentDirectory();
				File tempFile = new File(upload.getTempFileLocation());
				String validfilename = Util.resolveFilename(currentDir, upload.getFileName());
				if (currentDir.canWrite() && tempFile.exists()) {
					try {
						Services.getImageService().moveFile(tempFile, new File(currentDir, validfilename));
						uploadMap.put(upload.getFileName(), validfilename);
					} catch (IOException e) {
						Logger.error("Error while moving uploaded file to target folder: ", e);
					}
				}
			}
		}
	}

	private void dispatchMessage(SimpleEventMsgIn message) {
		Logger.debug("WebEventDispatcher.dispatchMessage", message);
		switch (message.getType()) {
		case killSwing:
			Logger.info("Received kill signal. Swing application shutting down.");
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					System.exit(0);
				}
			});
			break;
		case deleteFile:
			Util.getWebToolkit().getPaintDispatcher().notifyDeleteSelectedFile();
			break;
		case downloadFile:
			Util.getWebToolkit().getPaintDispatcher().notifyDownloadSelectedFile();
			break;
		case paintAck:
			Util.getWebToolkit().getPaintDispatcher().clientReadyToReceive();
			break;
		case repaint:
			if (Util.isDD()) {
				Services.getDirectDrawService().resetCache();
				Util.repaintAllWindow();
			} else {
				Util.getWebToolkit().getPaintDispatcher().notifyWindowRepaintAll();
			}
			break;
		case hb:
		case unload:
		}
	}

	private void dispatchKeyboardEvent(KeyboardEventMsgIn event) {
		Window w = (Window) WindowManager.getInstance().getActiveWindow();
		if (w != null) {
			long when = System.currentTimeMillis();
			int modifiers = Util.getKeyModifiersAWTFlag(event);
			int type = Util.getKeyType(event.getType());
			Component src = w.getFocusOwner() == null ? w : w.getFocusOwner();
			if (type == KeyEvent.KEY_TYPED) {
				AWTEvent e = new KeyEvent(src, KeyEvent.KEY_TYPED, when, 0, 0, (char) event.getCharacter());
				dispatchEventInSwing(w, e);
			} else {
				if (event.getKeycode() == 13) {// enter keycode
					event.setKeycode(10);
					event.setCharacter(10);
				} else if (event.getKeycode() == 46) {// delete keycode
					event.setKeycode(127);
					event.setCharacter(127);
				}
				AWTEvent e = new KeyEvent(src, type, when, modifiers, event.getKeycode(), (char) event.getCharacter(), KeyEvent.KEY_LOCATION_STANDARD);

				// filter out ctrl+c for copy
				if (event.getType() == KeyboardEventMsgIn.KeyEventType.keydown && event.getCharacter() == 67 && event.isCtrl() == true && event.isAlt() == false && event.isAltgr() == false && event.isMeta() == false && event.isShift() == false) {
					// on copy event - do nothing, default behavior calls
					// setContents on WebClipboard, which notifies the browser
				}
				if (event.getType() == KeyboardEventMsgIn.KeyEventType.keydown && event.getCharacter() == 86 && event.isCtrl() == true && event.isAlt() == false && event.isAltgr() == false && event.isMeta() == false && event.isShift() == false) {
					// on paste event -do nothing
				} else {
					dispatchEventInSwing(w, e);
					if (event.getKeycode() == 32 && event.getType() == KeyboardEventMsgIn.KeyEventType.keydown) {// space
																													// keycode
																													// handle
																													// press
						event.setType(KeyboardEventMsgIn.KeyEventType.keypress);
						dispatchKeyboardEvent(event);
					}
				}
			}
		}
	}

	private void dispatchMouseEvent(MouseEventMsgIn event) {
		Window w = null;
		if (WindowManager.getInstance().isLockedToWindowDecorationHandler()) {
			w = WindowManager.getInstance().getLockedToWindow();
		} else {
			w = WindowManager.getInstance().getVisibleWindowOnPosition(event.getX(), event.getY());
			if (lastMouseEvent != null && (lastMouseEvent.getID() == MouseEvent.MOUSE_DRAGGED || lastMouseEvent.getID() == MouseEvent.MOUSE_PRESSED) && ((event.getType() == MouseEventType.mousemove && event.getButton() == 1) || (event.getType() == MouseEventType.mouseup))) {
				w = (Window) lastMouseEvent.getSource();
			}
		}
		if (w == null) {
			if (Util.getWebToolkit().getPaintDispatcher() != null) {
				Util.getWebToolkit().getPaintDispatcher().notifyCursorUpdate(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			return;
		}
		if (w != null) {
			MouseEvent e = null;
			int x = event.getX() - w.getX();
			int y = event.getY() - w.getY();
			lastMousePosition.x = event.getX();
			lastMousePosition.y = event.getY();
			long when = System.currentTimeMillis();
			int modifiers = Util.getMouseModifiersAWTFlag(event);
			int id = 0;
			int clickcount = 0;
			int buttons = Util.getMouseButtonsAWTFlag(event.getButton());
			if (buttons != 0 && event.getType() == MouseEventType.mousedown) {
				WindowManager.getInstance().activateWindow(w, x, y);
			}
			switch (event.getType()) {
			case mousemove:
				id = event.getButton() == 1 ? MouseEvent.MOUSE_DRAGGED : MouseEvent.MOUSE_MOVED;
				e = new MouseEvent(w, id, when, modifiers, x, y, event.getX(), event.getY(), clickcount, false, buttons);
				lastMouseEvent = e;
				dispatchEventInSwing(w, e);
				break;
			case mouseup:
				id = MouseEvent.MOUSE_RELEASED;
				boolean popupTrigger = (buttons == 3) ? true : false;
				clickcount = computeClickCount(x, y, buttons, false);
				modifiers = modifiers & (((1 << 6) - 1) | (~((1 << 14) - 1)) | MouseEvent.CTRL_DOWN_MASK | MouseEvent.ALT_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK | MouseEvent.META_DOWN_MASK);
				e = new MouseEvent(w, id, when, modifiers, x, y, event.getX(), event.getY(), clickcount, popupTrigger, buttons);
				dispatchEventInSwing(w, e);
				if (lastMousePressEvent != null && lastMousePressEvent.getX() == x && lastMousePressEvent.getY() == y) {
					e = new MouseEvent(w, MouseEvent.MOUSE_CLICKED, when, modifiers, x, y, event.getX(), event.getY(), clickcount, popupTrigger, buttons);
					dispatchEventInSwing(w, e);
					lastMouseEvent = e;
					lastMousePressEvent = e;
				} else {
					lastMouseEvent = e;
					lastMousePressEvent = e;
				}
				break;
			case mousedown:
				id = MouseEvent.MOUSE_PRESSED;
				clickcount = computeClickCount(x, y, buttons, true);
				e = new MouseEvent(w, id, when, modifiers, x, y, event.getX(), event.getY(), clickcount, false, buttons);
				dispatchEventInSwing(w, e);
				lastMousePressEvent = e;
				lastMouseEvent = e;
				break;
			case mousewheel:
				id = MouseEvent.MOUSE_WHEEL;
				buttons = 0;
				modifiers = 0;
				e = new MouseWheelEvent(w, id, when, modifiers, x, y, clickcount, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 3, event.getWheelDelta());
				dispatchEventInSwing(w, e);
				break;
			case dblclick:
				// e = new MouseEvent(w, MouseEvent.MOUSE_CLICKED, when,
				// modifiers, x, y, event.x, event.y, 2, false, buttons);
				// dispatchEventInSwing(w, e);
				// break;
			default:
				break;
			}
		}
	}

	private int computeClickCount(int x, int y, int buttons, boolean isPressed) {
		if (isPressed) {
			if (lastMousePressEvent != null && lastMousePressEvent.getID() == MouseEvent.MOUSE_CLICKED && lastMousePressEvent.getButton() == buttons && lastMousePressEvent.getX() == x && lastMousePressEvent.getY() == y) {
				return lastMousePressEvent.getClickCount() + 1;
			}
		} else {
			if (lastMousePressEvent != null && lastMousePressEvent.getID() == MouseEvent.MOUSE_PRESSED && lastMousePressEvent.getButton() == buttons) {
				return lastMousePressEvent.getClickCount();
			}
		}
		return 1;
	}

	private void handlePasteEvent(final String content) {
		Logger.debug("WebEventDispatcher.handlePasteEvent", content);
		final Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		if (c != null && c instanceof JTextComponent) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JTextComponent txtc = (JTextComponent) c;
					WebClipboard wc = (WebClipboard) Util.getWebToolkit().getSystemClipboard();
					wc.setContent(content);
					txtc.paste();
				}
			});
		}
	}

	public Point getLastMousePosition() {
		return lastMousePosition;
	}

	public void dragStart(WebDragSourceContextPeer peer, Transferable transferable, int actions, long[] formats) {
		dndHandler.dragStart(peer, transferable, actions, formats);
	}

	public static void dispatchEventInSwing(final Window w, final AWTEvent e) {
		if (e instanceof MouseEvent) {
			w.setCursor(w.getCursor());// force cursor update
		}
		if ((Util.isWindowDecorationEvent(w, e) || WindowManager.getInstance().isLockedToWindowDecorationHandler()) && e instanceof MouseEvent) {
			Logger.debug("WebEventDispatcher.dispatchEventInSwing:windowManagerHandle", e);
			WindowManager.getInstance().handleWindowDecorationEvent(w, (MouseEvent) e);
		} else if (DndEventHandler.isDndInProgress() && (e instanceof MouseEvent || e instanceof KeyEvent)) {
			dndHandler.processMouseEvent(w, e);
		} else {
			Logger.debug("WebEventDispatcher.dispatchEventInSwing:postSystemQueue", e);
			Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
		}
	}

	private void handleUploadedEvent(UploadedEventMsgIn e) {
		JFileChooser fc = Util.getWebToolkit().getPaintDispatcher().getFileChooserDialog();
		UploadedEventMsgIn event = (UploadedEventMsgIn) e;
		if (fc != null) {
			fc.rescanCurrentDirectory();
			if (event.getFiles().size() > 0) {
				if (fc.isMultiSelectionEnabled()) {
					File arr[] = new File[event.getFiles().size()];
					for (int i = 0; i < event.getFiles().size(); i++) {
						arr[i] = new File(fc.getCurrentDirectory(), uploadMap.get(event.getFiles().get(i)));
					}
					fc.setSelectedFiles(arr);
				} else {
					File f = new File(fc.getCurrentDirectory(), uploadMap.get(event.getFiles().get(0)));
					fc.setSelectedFile(f);
				}
				// fc.approveSelection();
			} else {
				fc.cancelSelection();
			}
		}
		uploadMap.clear();
	}

}
