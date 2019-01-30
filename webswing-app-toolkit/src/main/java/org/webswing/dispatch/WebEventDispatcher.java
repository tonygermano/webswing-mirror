package org.webswing.dispatch;

import netscape.javascript.JSObject;
import org.webswing.Constants;
import org.webswing.model.MsgIn;
import org.webswing.model.c2s.*;
import org.webswing.model.c2s.MouseEventMsgIn.MouseEventType;
import org.webswing.model.internal.OpenFileResultMsgInternal;
import org.webswing.model.jslink.JSObjectMsg;
import org.webswing.model.s2c.FileDialogEventMsg.FileDialogEventType;
import org.webswing.toolkit.FocusEventCause;
import org.webswing.toolkit.WebClipboard;
import org.webswing.toolkit.WebClipboardTransferable;
import org.webswing.toolkit.WebDragSourceContextPeer;
import org.webswing.toolkit.extra.DndEventHandler;
import org.webswing.toolkit.jslink.WebJSObject;
import org.webswing.toolkit.util.DeamonThreadFactory;
import org.webswing.toolkit.util.Logger;
import org.webswing.toolkit.util.Services;
import org.webswing.toolkit.util.Util;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("restriction")
public class WebEventDispatcher {

	protected static final int CLICK_TOLERANCE = 2;
	protected MouseEvent lastMouseEvent;
	protected MouseEventInfo lastMousePressEvent;
	protected Point lastMousePosition = new Point();
	public static AtomicBoolean javaFXdragStarted = new AtomicBoolean(false);
	private static Component lastEnteredWindow;
	private static final DndEventHandler dndHandler = new DndEventHandler();
	private HashMap<String, String> uploadMap = new HashMap<String, String>();
	private ExecutorService eventDispatcher = Executors.newSingleThreadExecutor(DeamonThreadFactory.getInstance("Webswing Event Dispatcher"));

	//release char map derives the event char for keyrelease event from previous keypressed events (keycode=char)
	private static final HashMap<Integer, Integer> releaseCharMap = new HashMap<Integer, Integer>();
	//these keycodes are assigned to different keys in browser
	private static final List<Integer> nonStandardKeyCodes = Arrays.asList(KeyEvent.VK_KP_DOWN, KeyEvent.VK_KP_UP, KeyEvent.VK_KP_RIGHT, KeyEvent.VK_KP_LEFT);
	private static final Map<Integer, Integer> convertedKeyCodes = new HashMap<Integer, Integer>();

	static {
		convertedKeyCodes.put(45, KeyEvent.VK_INSERT);//	Insert 155
		convertedKeyCodes.put(46, KeyEvent.VK_DELETE);//	Delete 127
		convertedKeyCodes.put(189, KeyEvent.VK_MINUS);//	Minus 45
		convertedKeyCodes.put(187, KeyEvent.VK_EQUALS);//	Equals 61
		convertedKeyCodes.put(219, KeyEvent.VK_OPEN_BRACKET);//	Open Bracket 91
		convertedKeyCodes.put(221, KeyEvent.VK_CLOSE_BRACKET);//	Close Bracket 93
		convertedKeyCodes.put(186, KeyEvent.VK_SEMICOLON);//	Semicolon 59
		convertedKeyCodes.put(220, KeyEvent.VK_BACK_SLASH);//	Back Slash 92
		convertedKeyCodes.put(226, KeyEvent.VK_BACK_SLASH);//	Back Slash 92
		convertedKeyCodes.put(188, KeyEvent.VK_COMMA);//	Comma 44
		convertedKeyCodes.put(190, KeyEvent.VK_PERIOD);//	Period 46
		convertedKeyCodes.put(191, KeyEvent.VK_SLASH);//	Slash 47
	}

	public static final long doubleClickMaxDelay = Long.getLong(Constants.SWING_START_SYS_PROP_DOUBLE_CLICK_DELAY, 750);

	public void dispatchEvent(final MsgIn event) {
		Logger.debug("WebEventDispatcher.dispatchEvent:", event);
		eventDispatcher.submit(new Runnable() {

			@Override
			public void run() {
				try {
					if (event instanceof MouseEventMsgIn) {
						dispatchMouseEvent((MouseEventMsgIn) event);
					}
					if (event instanceof KeyboardEventMsgIn) {
						dispatchKeyboardEvent((KeyboardEventMsgIn) event);
					}
					if (event instanceof ConnectionHandshakeMsgIn) {
						final ConnectionHandshakeMsgIn handshake = (ConnectionHandshakeMsgIn) event;
						Util.getWebToolkit().initSize(handshake.getDesktopWidth(), handshake.getDesktopHeight());
						Util.getWebToolkit().getPaintDispatcher().notifyFileDialogActive();
						Util.getWebToolkit().getPaintDispatcher().closePasteRequestDialog();
						Util.getWebToolkit().processApiEvent(handshake);
						if (System.getProperty(Constants.SWING_START_SYS_PROP_APPLET_CLASS) != null) {
							// resize and refresh the applet object exposed in javascript in case of page reload/session continue
							Applet a = (Applet) WebJSObject.getJavaReference(System.getProperty(Constants.SWING_START_SYS_PROP_APPLET_CLASS));
							a.resize(handshake.getDesktopWidth(), handshake.getDesktopHeight());
							JSObject root = new WebJSObject(new JSObjectMsg("instanceObject"));
							root.setMember("applet", a);
						}
					}
					if (event instanceof SimpleEventMsgIn) {
						SimpleEventMsgIn msg = (SimpleEventMsgIn) event;
						dispatchMessage(msg);
					}
					if (event instanceof PasteEventMsgIn) {
						PasteEventMsgIn paste = (PasteEventMsgIn) event;
						handlePasteEvent(paste);
					}
					if (event instanceof CopyEventMsgIn) {
						CopyEventMsgIn copy = (CopyEventMsgIn) event;
						handleCopyEvent(copy);
					}
					if (event instanceof FilesSelectedEventMsgIn) {
						handleFileSelectionEvent((FilesSelectedEventMsgIn) event);
					}
					if (event instanceof UploadEventMsgIn) {
						handleUploadEvent((UploadEventMsgIn) event);
					}
				} catch (Throwable e) {
					Logger.error("Failed to process event.", e);
				}
			}
		});
	}

	private void dispatchMessage(SimpleEventMsgIn message) {
		Logger.debug("WebEventDispatcher.dispatchMessage", message);
		switch (message.getType()) {
		case killSwing:
			Logger.info("Received kill signal. Application shutting down.");
			Util.getWebToolkit().exitSwing(0);
			break;
		case deleteFile:
			Util.getWebToolkit().getPaintDispatcher().notifyDeleteSelectedFile();
			break;
		case downloadFile:
			Util.getWebToolkit().getPaintDispatcher().notifyDownloadSelectedFile();
			break;
		case cancelFileSelection:
			handleAutoUploadCancelled();
			break;
		case paintAck:
			Util.getWebToolkit().getPaintDispatcher().clientReadyToReceive();
			break;
		case repaint:
			if (Util.isDD()) {
				Util.getWebToolkit().getPaintDispatcher().notifyBackgroundRepainted(new Rectangle(Util.getWebToolkit().getScreenSize()));
				Services.getDirectDrawService().resetCache();
				Util.repaintAllWindow();
			} else {
				Util.getWebToolkit().getPaintDispatcher().notifyWindowRepaintAll();
			}
			break;
		case unload:
			boolean instantExit = Integer.parseInt(System.getProperty(Constants.SWING_SESSION_TIMEOUT_SEC, "300")) == 0;
			if (instantExit) {
				Logger.warn("Exiting Application. Client has disconnected from web session. (swingSessionTimeout setting is 0 or less)");
				Util.getWebToolkit().exitSwing(1);
			}
			break;
		case hb:
			break;
		}
	}

	private void dispatchKeyboardEvent(KeyboardEventMsgIn event) {
		Window w = (Window) Util.getWebToolkit().getWindowManager().getActiveWindow();
		if (w != null) {
			long when = System.currentTimeMillis();
			int modifiers = Util.getKeyModifiersAWTFlag(event);
			int type = Util.getKeyType(event.getType());
			char character = Util.getKeyCharacter(event);
			Component src = w.getFocusOwner() == null ? w : w.getFocusOwner();
			if (event.getKeycode() == 13) {// enter keycode
				event.setKeycode(10);
				character = 10;
			} else if (convertedKeyCodes.containsKey(event.getKeycode()) && type != KeyEvent.KEY_TYPED) {
				int converted = convertedKeyCodes.get(event.getKeycode());
				event.setKeycode(converted);
				character = (char) converted;
			} else if (nonStandardKeyCodes.contains(event.getKeycode())) {
				event.setKeycode(0);
			}
			if (event.getType() == KeyboardEventMsgIn.KeyEventType.keydown) {
				releaseCharMap.put(event.getKeycode(), event.getCharacter());
			}
			if (event.getType() == KeyboardEventMsgIn.KeyEventType.keyup) {
				Integer c = releaseCharMap.get(event.getKeycode());
				character = (char) (c == null ? event.getCharacter() : c);
			}
			if (type == KeyEvent.KEY_TYPED) {
				AWTEvent e = new KeyEvent(src, KeyEvent.KEY_TYPED, when, modifiers, 0, (char) event.getCharacter());
				dispatchEventInSwing(w, e);
			} else {
				AWTEvent e = Util.createKeyEvent(src, type, when, modifiers, event.getKeycode(), character, KeyEvent.KEY_LOCATION_STANDARD);
				dispatchEventInSwing(w, e);
				if ((event.getKeycode() == 32 ||event.getKeycode() == 9) && event.getType() == KeyboardEventMsgIn.KeyEventType.keydown && !event.isCtrl()) {// space keycode handle press
					event.setType(KeyboardEventMsgIn.KeyEventType.keypress);
					dispatchKeyboardEvent(event);
				}
			}
		}
	}

	protected void dispatchMouseEvent(MouseEventMsgIn event) {
		Component c = null;
		if (Util.getWebToolkit().getWindowManager().isLockedToWindowDecorationHandler()) {
			c = Util.getWebToolkit().getWindowManager().getLockedToWindow();
		} else {
			c = Util.getWebToolkit().getWindowManager().getVisibleComponentOnPosition(event.getX(), event.getY());
			if (relatedToLastEvent(event, lastMouseEvent) && !javaFXdragStarted.get() && !dndHandler.isDndInProgress() ) {
				c = (Component) lastMouseEvent.getSource();
			}
		}
		if (c == null) {
			if (Util.getWebToolkit().getPaintDispatcher() != null) {
				Util.getWebToolkit().getPaintDispatcher().notifyCursorUpdate(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			dispatchExitEvent(System.currentTimeMillis(), 0, -1, -1, event.getX(), event.getY());
			return;
		}
		if (c != null && c.isShowing()) {
			MouseEvent e = null;
			int x = 0;
			int y = 0;
			try {
				x = (int) (event.getX() - c.getLocationOnScreen().getX());
				y = (int) (event.getY() - c.getLocationOnScreen().getY());
			} catch (IllegalComponentStateException e1) {
				//in case the window closed in meantime, just ignore this event
				return;
			}
			lastMousePosition.x = event.getX();
			lastMousePosition.y = event.getY();
			long when = System.currentTimeMillis();
			int modifiers = Util.getMouseModifiersAWTFlag(event);
			int id = 0;
			int clickcount = 0;
			int buttons = Util.getMouseButtonsAWTFlag(event.getButton());
			if (buttons != 0 && event.getType() == MouseEventType.mousedown) {
				Window w = (Window) (c instanceof Window ? c : SwingUtilities.windowForComponent(c));
				Util.getWebToolkit().getWindowManager().activateWindow(w, null, x, y, false, true, FocusEventCause.MOUSE_EVENT);
			}
			switch (event.getType()) {
			case mousemove:
				id = event.getButtons() != 0 ? MouseEvent.MOUSE_DRAGGED : MouseEvent.MOUSE_MOVED;
				buttons = 0; //in swing mouse move/drag has always MouseEvent.button==0
				e = new MouseEvent(c, id, when, modifiers, x, y, event.getX(), event.getY(), clickcount, false, buttons);
				lastMouseEvent = e;
				dispatchEventInSwing(c, e);
				break;
			case mouseup:
				id = MouseEvent.MOUSE_RELEASED;
				boolean popupTrigger = (buttons == 3) ? true : false;
				clickcount = computeClickCount(x, y, buttons, false, event.getTimeMilis());
				modifiers = modifiers & (((1 << 6) - 1) | (~((1 << 14) - 1)) | MouseEvent.CTRL_DOWN_MASK | MouseEvent.ALT_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK | MouseEvent.META_DOWN_MASK);
				e = new MouseEvent(c, id, when, modifiers, x, y, event.getX(), event.getY(), clickcount, popupTrigger, buttons);
				dispatchEventInSwing(c, e);
				if (lastMousePressEvent != null && Math.abs(lastMousePressEvent.x - x) < CLICK_TOLERANCE && Math.abs(lastMousePressEvent.y - y) < CLICK_TOLERANCE) {
					e = new MouseEvent(c, MouseEvent.MOUSE_CLICKED, when, modifiers, x, y, event.getX(), event.getY(), clickcount, popupTrigger, buttons);
					dispatchEventInSwing(c, e);
					lastMouseEvent = e;
					lastMousePressEvent = MouseEventInfo.get(e, event.getTimeMilis());
				} else {
					lastMouseEvent = e;
					lastMousePressEvent = MouseEventInfo.get(e, event.getTimeMilis());
				}
				break;
			case mousedown:
				id = MouseEvent.MOUSE_PRESSED;
				clickcount = computeClickCount(x, y, buttons, true, event.getTimeMilis());
				e = new MouseEvent(c, id, when, modifiers, x, y, event.getX(), event.getY(), clickcount, false, buttons);
				dispatchEventInSwing(c, e);
				lastMousePressEvent = MouseEventInfo.get(e, event.getTimeMilis());
				lastMouseEvent = e;
				break;
			case mousewheel:
				id = MouseEvent.MOUSE_WHEEL;
				buttons = 0;
				modifiers = 0;
				e = new MouseWheelEvent(c, id, when, modifiers, x, y, clickcount, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 3, event.getWheelDelta());
				dispatchEventInSwing(c, e);
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

	private static boolean relatedToLastEvent(MouseEventMsgIn event, MouseEvent lastMouseEvent) {
		return lastMouseEvent != null && (lastMouseEvent.getID() == MouseEvent.MOUSE_DRAGGED || lastMouseEvent.getID() == MouseEvent.MOUSE_PRESSED) && ((event.getType() == MouseEventType.mousemove && event.getButtons() != 0) || (event.getType() == MouseEventType.mouseup));
	}

	protected int computeClickCount(int x, int y, int button, boolean isPressed, int timeMilis) {
		if (isPressed) {
			if (lastMousePressEvent != null && lastMousePressEvent.type == MouseEvent.MOUSE_CLICKED && lastMousePressEvent.button == button && Math.abs(lastMousePressEvent.x - x) < CLICK_TOLERANCE && Math.abs(lastMousePressEvent.y - y) < CLICK_TOLERANCE) {
				if (timeMilis - lastMousePressEvent.time < doubleClickMaxDelay) {
					return lastMousePressEvent.clickcount + 1;
				}
			}
		} else {
			if (lastMousePressEvent != null && lastMousePressEvent.type == MouseEvent.MOUSE_PRESSED && lastMousePressEvent.button == button) {
				return lastMousePressEvent.clickcount;
			}
		}
		return 1;
	}

	private void handleCopyEvent(final CopyEventMsgIn copy) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (copy.getType() != null) {
					switch (copy.getType()) {
					case copy:
						dispatchCopyEvent();
						break;
					case cut:
						dispatchCutEvent();
						break;
					case getFileFromClipboard:
						handleClipboardFileDownload(copy);
					}
				}
			}
		});
	}

	private void handleClipboardFileDownload(CopyEventMsgIn copy) {
		if (Boolean.getBoolean(Constants.SWING_START_SYS_PROP_ALLOW_DOWNLOAD)) {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			if (clipboard.isDataFlavorAvailable(DataFlavor.javaFileListFlavor)) {
				try {
					List<?> files = (List<?>) clipboard.getData(DataFlavor.javaFileListFlavor);
					for (Object o : files) {
						File file = (File) o;
						if (file.getAbsolutePath().equals(copy.getFile())) {
							if (file != null && file.exists() && !file.isDirectory() && file.canRead()) {
								OpenFileResultMsgInternal f = new OpenFileResultMsgInternal();
								f.setClientId(System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID));
								f.setFile(file);
								Util.getWebToolkit().getPaintDispatcher().sendObject(f);
							} else {
								Logger.error("Failed to download file " + copy.getFile() + " from clipboard. File is not accessible or is a directory");
							}
						}
					}
				} catch (Exception e) {
					Logger.error("Failed to download file " + copy.getFile() + " from clipboard.", e);
				}
			}
		}
	}

	private void handlePasteEvent(final PasteEventMsgIn paste) {
		Logger.debug("WebEventDispatcher.handlePasteEvent", paste);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				boolean clipboardRequested = Util.getWebToolkit().getPaintDispatcher().closePasteRequestDialog();
				WebClipboardTransferable transferable = new WebClipboardTransferable(paste);
				WebClipboard wc = (WebClipboard) Util.getWebToolkit().getSystemClipboard();
				wc.setBrowserClipboard(transferable);
				if (!transferable.isEmpty() && Boolean.getBoolean(Constants.SWING_START_SYS_PROP_ALLOW_LOCAL_CLIPBOARD)) {
					wc.setContents(transferable);
				}
				if (!clipboardRequested) {
					WebEventDispatcher.this.dispatchPasteEvent(paste.isSpecial());
				}
			}
		});
	}

	private void dispatchPasteEvent(boolean special) {
		KeyboardEventMsgIn event = new KeyboardEventMsgIn();
		event.setType(KeyboardEventMsgIn.KeyEventType.keydown);
		event.setCharacter(KeyEvent.VK_V);
		event.setKeycode(KeyEvent.VK_V);// 'v'
		event.setCtrl(true);
		event.setShift(special);
		dispatchKeyboardEvent(event);
		event.setType(KeyboardEventMsgIn.KeyEventType.keyup);
		dispatchKeyboardEvent(event);
	}

	private void dispatchCopyEvent() {
		KeyboardEventMsgIn event = new KeyboardEventMsgIn();
		event.setType(KeyboardEventMsgIn.KeyEventType.keydown);
		event.setCharacter(KeyEvent.VK_C); // 'c'
		event.setKeycode(KeyEvent.VK_C);
		event.setCtrl(true);
		dispatchKeyboardEvent(event);
		event.setType(KeyboardEventMsgIn.KeyEventType.keyup);
		dispatchKeyboardEvent(event);
	}

	private void dispatchCutEvent() {
		KeyboardEventMsgIn event = new KeyboardEventMsgIn();
		event.setType(KeyboardEventMsgIn.KeyEventType.keydown);
		event.setCharacter(KeyEvent.VK_X);
		event.setKeycode(KeyEvent.VK_X);// 'x'
		event.setCtrl(true);
		dispatchKeyboardEvent(event);
		event.setType(KeyboardEventMsgIn.KeyEventType.keyup);
		dispatchKeyboardEvent(event);
	}

	public Point getLastMousePosition() {
		return lastMousePosition;
	}

	public void dragStart(WebDragSourceContextPeer peer, Transferable transferable, int actions, long[] formats) {
		dndHandler.dragStart(peer, transferable, actions, formats);
	}

	public static void dispatchEventInSwing(final Component c, final AWTEvent e) {
		Window w = (Window) (c instanceof Window ? c : SwingUtilities.windowForComponent(c));
		if (w.isEnabled()) {
			if (e instanceof MouseEvent) {
				w.setCursor(w.getCursor());// force cursor update
			}
			if ((Util.isWindowDecorationEvent(w, e) || Util.getWebToolkit().getWindowManager().isLockedToWindowDecorationHandler()) && e instanceof MouseEvent) {
				Logger.debug("WebEventDispatcher.dispatchEventInSwing:windowManagerHandle", e);
				Util.getWebToolkit().getWindowManager().handleWindowDecorationEvent(w, (MouseEvent) e);
			} else if (dndHandler.isDndInProgress() && (e instanceof MouseEvent || e instanceof KeyEvent)) {
				dndHandler.processMouseEvent(w, e);
			} else {
				Logger.debug("WebEventDispatcher.dispatchEventInSwing:postSystemQueue", e);
				dispatchEnterExitEvents(w, e);
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
			}
		}
	}

	private static void dispatchEnterExitEvents(Window w, AWTEvent e) {
		if (e instanceof MouseEvent && lastEnteredWindow != w) {
			MouseEvent oe = (MouseEvent) e;
			dispatchExitEvent(oe.getWhen(), oe.getModifiersEx() | oe.getModifiers(), oe.getX(), oe.getY(), oe.getXOnScreen(), oe.getYOnScreen());
			if (w != null) {
				MouseEvent enterEvent = new MouseEvent(w, MouseEvent.MOUSE_ENTERED, oe.getWhen(), oe.getModifiersEx() | oe.getModifiers(), oe.getX(), oe.getY(), oe.getXOnScreen(), oe.getYOnScreen(), oe.getClickCount(), oe.isPopupTrigger(), oe.getButton());
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(enterEvent);
			}
			lastEnteredWindow = w;
		}
	}

	private static void dispatchExitEvent(long when, int mod, int x, int y, int absX, int absY) {
		if (lastEnteredWindow != null && lastEnteredWindow.isShowing()) {
			MouseEvent exitEvent = new MouseEvent(lastEnteredWindow, MouseEvent.MOUSE_EXITED, when, mod, x, y, absX, absY, 0, false, 0);
			Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(exitEvent);
			lastEnteredWindow = null;
		}
	}

	private void handleFileSelectionEvent(final FilesSelectedEventMsgIn e) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFileChooser fc = Util.getWebToolkit().getPaintDispatcher().getFileChooserDialog();
				FilesSelectedEventMsgIn event = (FilesSelectedEventMsgIn) e;
				if (fc != null) {
					FileDialogEventType fileChooserEventType = Util.getFileChooserEventType(fc);
					boolean saveMode = FileDialogEventType.AutoSave == fileChooserEventType;
					fc.rescanCurrentDirectory();
					if (event.getFiles() != null && event.getFiles().size() > 0) {
						if (fc.isMultiSelectionEnabled()) {
							List<File> arr = new ArrayList<File>();
							for (int i = 0; i < event.getFiles().size(); i++) {
								if (uploadMap.get(event.getFiles().get(i)) != null) {
									arr.add(new File(fc.getCurrentDirectory(), uploadMap.get(event.getFiles().get(i))));
								} else if (saveMode) {
									arr.add(new File(fc.getCurrentDirectory(), event.getFiles().get(i)));
								}
							}
							fc.setSelectedFiles(arr.toArray(new File[arr.size()]));
							Logger.info("Files selected :" + arr);
						} else {
							if (uploadMap.get(event.getFiles().get(0)) != null) {
								File f = new File(fc.getCurrentDirectory(), uploadMap.get(event.getFiles().get(0)));
								fc.setSelectedFile(f);
							} else if (saveMode) {
								fc.setSelectedFile(new File(fc.getCurrentDirectory(), event.getFiles().get(0)));
							}
							Logger.info("File selected :" + fc.getSelectedFile().getAbsoluteFile());
						}
						if (FileDialogEventType.AutoUpload == fileChooserEventType || FileDialogEventType.AutoSave == fileChooserEventType) {
							fc.approveSelection();
						}
					} else {
						if (FileDialogEventType.AutoUpload == fileChooserEventType || FileDialogEventType.AutoSave == fileChooserEventType) {
							fc.cancelSelection();
						}
					}
				}
				uploadMap.clear();
			}
		});
	}

	private void handleUploadEvent(final UploadEventMsgIn upload) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFileChooser dialog = Util.getWebToolkit().getPaintDispatcher().getFileChooserDialog();
				if (dialog != null) {
					File currentDir = dialog.getCurrentDirectory();
					File tempFile = new File(upload.getTempFileLocation());
					String validfilename = Util.resolveUploadFilename(currentDir, upload.getFileName());
					if (currentDir.canWrite() && tempFile.exists()) {
						try {
							Services.getImageService().moveFile(tempFile, new File(currentDir, validfilename));
							uploadMap.put(upload.getFileName(), validfilename);
							Logger.info("File upload notification received: " + validfilename);
						} catch (IOException e) {
							Logger.error("Error while moving uploaded file '" + validfilename + "' to target folder: ", e);
						}
					} else {
						Logger.error("Error while uploading file '" + validfilename + "'. " + (currentDir.canWrite() ? " Temp upload file " + tempFile.getAbsoluteFile() + " not found" : "Can not write to target folder " + currentDir.getAbsoluteFile()));
					}
				} else {
					Logger.error("Error while uploading file. FileChooser dialog instance not found");
				}
			}
		});
	}

	public void handleAutoUploadCancelled() {
		JFileChooser dialog = Util.getWebToolkit().getPaintDispatcher().getFileChooserDialog();
		if (dialog != null) {
			dialog.cancelSelection();
		}
	}

	public static boolean isDndInProgress() {
		return dndHandler.isDndInProgress();
	}

	protected static class MouseEventInfo {
		final int x;
		final int y;
		final int type;
		final int button;
		final int clickcount;
		final int time;

		private MouseEventInfo(MouseEvent e, int time) {
			this.x = e.getX();
			this.y = e.getY();
			this.type = e.getID();
			this.clickcount = e.getClickCount();
			this.button = e.getButton();
			this.time = time;
		}

		public static MouseEventInfo get(MouseEvent e, int time) {
			return new MouseEventInfo(e, time);
		}
	}
}
