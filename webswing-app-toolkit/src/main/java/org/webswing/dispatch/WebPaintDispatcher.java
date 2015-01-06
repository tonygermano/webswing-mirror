package org.webswing.dispatch;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import org.webswing.Constants;
import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.model.s2c.JsonCopyEvent;
import org.webswing.model.s2c.JsonCursorChange;
import org.webswing.model.s2c.JsonFileDialogEvent;
import org.webswing.model.s2c.JsonFileDialogEvent.FileDialogEventType;
import org.webswing.model.s2c.JsonLinkAction;
import org.webswing.model.s2c.JsonLinkAction.JsonLinkActionType;
import org.webswing.model.s2c.JsonWindow;
import org.webswing.model.s2c.JsonWindowMoveAction;
import org.webswing.model.s2c.OpenFileResult;
import org.webswing.toolkit.WebToolkit;
import org.webswing.toolkit.WebWindowPeer;
import org.webswing.toolkit.extra.WebRepaintManager;
import org.webswing.toolkit.extra.WindowManager;
import org.webswing.util.Logger;
import org.webswing.util.Services;
import org.webswing.util.Util;

public class WebPaintDispatcher {

	public static final Object webPaintLock = new Object();

	private volatile Map<String, Set<Rectangle>> areasToUpdate = new HashMap<String, Set<Rectangle>>();
	private volatile JsonWindowMoveAction moveAction;
	private volatile boolean clientReadyToReceive = true;
	private long lastReadyStateTime;
	private JFileChooser fileChooserDialog;

	private ScheduledExecutorService contentSender = Executors.newScheduledThreadPool(1);

	public WebPaintDispatcher() {
		Runnable sendUpdate = new Runnable() {

			public void run() {
				try {
					JsonAppFrame json;
					Map<String, Map<Integer, BufferedImage>> windowImages = null;
					Map<String, Image> windowWebImages = null;
					Map<String, List<Rectangle>> windowNonVisibleAreas;
					Map<String, Set<Rectangle>> currentAreasToUpdate = null;
					synchronized (webPaintLock) {
						if (clientReadyToReceive) {
							if (RepaintManager.currentManager(null) instanceof WebRepaintManager) {
								((WebRepaintManager) RepaintManager.currentManager(null)).process();
							}
							lastReadyStateTime = System.currentTimeMillis();
						}
						if ((areasToUpdate.size() == 0 && moveAction == null) || !clientReadyToReceive) {
							if (!clientReadyToReceive && (System.currentTimeMillis() - lastReadyStateTime) > 2000) {
								Logger.info("contentSender.readyToReceive re-enabled after timeout");
								clientReadyToReceive = true;
							}
							return;
						}
						currentAreasToUpdate = areasToUpdate;
						areasToUpdate = Util.postponeNonShowingAreas(currentAreasToUpdate);
						if (currentAreasToUpdate.size() == 0 && moveAction == null) {
							return;
						}
						windowNonVisibleAreas = WindowManager.getInstance().extractNonVisibleAreas();
						json = Util.fillJsonWithWindowsData(currentAreasToUpdate, windowNonVisibleAreas);
						if (Util.isDD()) {
							windowWebImages = new HashMap<String, Image>();
							windowWebImages = Util.extractWindowWebImages(json, windowWebImages);
						} else {
							windowImages = new HashMap<String, Map<Integer, BufferedImage>>();
							windowImages = Util.extractWindowImages(json, windowImages);
						}
						if (moveAction != null) {
							json.moveAction = moveAction;
							moveAction = null;
						}
						clientReadyToReceive = false;
					}
					Logger.trace("contentSender:paintJson", json);
					if (Util.isDD()) {
						Logger.trace("contentSender:pngWebImageEncodingStart", json.hashCode());
						Util.encodeWindowWebImages(windowWebImages, json);
						Logger.trace("contentSender:pngWebImageEncodingDone", json.hashCode());
					} else {
						Logger.trace("contentSender:pngEncodingStart", json.hashCode());
						Util.encodeWindowImages(windowImages, json);
						Logger.trace("contentSender:pngEncodingDone", json.hashCode());
					}
					Services.getConnectionService().sendJsonObject(json);
				} catch (Exception e) {
					Logger.error("contentSender:error", e);
				}
			}
		};
		contentSender.scheduleWithFixedDelay(sendUpdate, 50, 50, TimeUnit.MILLISECONDS);

	}

	public void clientReadyToReceive() {
		synchronized (webPaintLock) {
			clientReadyToReceive = true;
		}
	}

	public void notifyShutdown() {
		Logger.info("WebPaintDispatcher:notifyShutdown");
		Services.getConnectionService().sendShutdownNotification();
	}

	public void sendJsonObject(Serializable object) {
		Logger.info("WebPaintDispatcher:sendJsonObject", object);
		Services.getConnectionService().sendJsonObject(object);
	}

	public void notifyWindowAreaRepainted(String guid, Rectangle repaintedArea) {
		synchronized (webPaintLock) {
			if (validBounds(repaintedArea)) {
				if (areasToUpdate.containsKey(guid)) {
					Set<Rectangle> rset = areasToUpdate.get(guid);
					rset.add(repaintedArea);
				} else {
					Set<Rectangle> rset = new HashSet<Rectangle>();
					rset.add(repaintedArea);
					areasToUpdate.put(guid, rset);
				}
				Logger.trace("WebPaintDispatcher:notifyWindowAreaRepainted", guid, repaintedArea);
			}
		}
	}

	public void notifyWindowBoundsChanged(String guid, Rectangle newBounds) {
		synchronized (webPaintLock) {
			if (validBounds(newBounds)) {
				Set<Rectangle> rset;
				if (areasToUpdate.containsKey(guid)) {
					rset = areasToUpdate.get(guid);
					rset.clear();
				} else {
					rset = new HashSet<Rectangle>();
					areasToUpdate.put(guid, rset);
				}
				rset.add(newBounds);
				Logger.trace("WebPaintDispatcher:notifyWindowBoundsChanged", guid, newBounds);
			}
		}
	}

	private boolean validBounds(Rectangle newBounds) {
		if (newBounds.width > 0 && newBounds.height > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void notifyWindowClosed(String guid) {
		synchronized (webPaintLock) {
			areasToUpdate.remove(guid);
		}
		JsonAppFrame f = new JsonAppFrame();
		JsonWindow fdEvent = new JsonWindow();
		fdEvent.setId(guid);
		f.closedWindow = fdEvent;
		Logger.info("WebPaintDispatcher:notifyWindowClosed", guid);
		Services.getConnectionService().sendJsonObject(f);
	}

	public void notifyWindowRepaint(Window w) {
		Rectangle bounds = w.getBounds();
		WebWindowPeer peer = (WebWindowPeer) WebToolkit.targetToPeer(w);
		notifyWindowAreaRepainted(peer.getGuid(), new Rectangle(0, 0, bounds.width, bounds.height));
	}

	@SuppressWarnings("restriction")
	public void notifyWindowRepaintAll() {
		notifyWindowAreaRepainted(WebToolkit.BACKGROUND_WINDOW_ID, new Rectangle(Util.getWebToolkit().getScreenSize()));
		for (Window w : Window.getWindows()) {
			if (w.isShowing()) {
				notifyWindowRepaint(w);
			}
		}
	}

	public void notifyBackgroundRepainted(Rectangle toRepaint) {
		notifyWindowAreaRepainted(WebToolkit.BACKGROUND_WINDOW_ID, toRepaint);
	}

	public void notifyOpenLinkAction(URI uri) {
		JsonAppFrame f = new JsonAppFrame();
		JsonLinkAction linkAction = new JsonLinkAction(JsonLinkActionType.url, uri.toString());
		f.setLinkAction(linkAction);
		Logger.info("WebPaintDispatcher:notifyOpenLinkAction", uri);
		Services.getConnectionService().sendJsonObject(f);
	}

	@SuppressWarnings("restriction")
	public void resetWindowsPosition(int oldWidht, int oldHeight) {
		for (Window w : Window.getWindows()) {
			WebWindowPeer peer = (WebWindowPeer) WebToolkit.targetToPeer(w);
			if (peer != null) {
				Rectangle b = w.getBounds();
				// move the window position to same position relative to
				// previous size;
				if (oldWidht != 0 && oldHeight != 0) {
					Dimension current = Util.getWebToolkit().getScreenSize();
					int xCenterWinPoint = b.x + (b.width / 2);
					int yCenterWinPoint = b.y + (b.height / 2);
					boolean xCenterValid = b.width < oldWidht;
					boolean yCenterValid = b.height < oldHeight;
					double xrelative = (double) xCenterWinPoint / (double) oldWidht;
					double yrelative = (double) yCenterWinPoint / (double) oldHeight;
					int xCenterCurrent = (int) (current.width * xrelative);
					int yCenterCurrent = (int) (current.height * yrelative);
					int newx = xCenterCurrent - (b.width / 2);
					int newy = yCenterCurrent - (b.height / 2);
					if (xCenterValid || newx < b.x) {
						b.x = newx >= 0 ? newx : 0;
					}
					if (yCenterValid || newy < b.y) {
						b.y = newy >= 0 ? newy : 0;
					}
					w.setLocation(b.x, b.y);
				}
				peer.setBounds(b.x, b.y, b.width, b.height, 0);
			}
		}
	}

	public void notifyWindowMoved(Window w, Rectangle from, Rectangle to) {
		synchronized (webPaintLock) {
			if (moveAction == null) {
				moveAction = new JsonWindowMoveAction(from.x, from.y, to.x, to.y, from.width, from.height);
				notifyRepaintOffScreenAreas(w, moveAction);
			} else if (moveAction.dx == from.x && moveAction.dy == from.y && moveAction.width == from.width && moveAction.height == from.height) {
				moveAction.dx = to.x;
				moveAction.dy = to.y;
				notifyRepaintOffScreenAreas(w, moveAction);
			} else {
				notifyWindowRepaint(w);
			}
		}
	}

	@SuppressWarnings("restriction")
	private void notifyRepaintOffScreenAreas(Window w, JsonWindowMoveAction m) {
		Rectangle screen = new Rectangle(Util.getWebToolkit().getScreenSize());
		Rectangle before = new Rectangle(m.sx, m.sy, m.width, m.height);
		Rectangle after = new Rectangle(m.dx, m.dy, m.width, m.height);
		int xdiff = m.sx - m.dx;
		int ydiff = m.sy - m.dy;
		Rectangle[] invisibleBefore = SwingUtilities.computeDifference(before, screen);
		if (invisibleBefore.length != 0) {
			for (Rectangle r : invisibleBefore) {
				r.setLocation(r.x - xdiff, r.y - ydiff);
			}
			Rectangle[] invisibleAfter = SwingUtilities.computeDifference(after, screen);
			List<Rectangle> toRepaint = Util.joinRectangles(Util.getGrid(Arrays.asList(invisibleBefore), Arrays.asList(invisibleAfter)));
			WebWindowPeer peer = (WebWindowPeer) WebToolkit.targetToPeer(w);
			for (Rectangle r : toRepaint) {
				r.setLocation(r.x - w.getX(), r.y - w.getY());
				notifyWindowAreaRepainted(peer.getGuid(), r);
			}
		}
	}

	public void notifyCursorUpdate(Cursor cursor) {
		notifyCursorUpdate(cursor, null);
	}

	public void notifyCursorUpdate(Cursor cursor, String overridenCursorName) {
		String webcursorName = null;
		if (overridenCursorName == null) {
			switch (cursor.getType()) {
			case Cursor.DEFAULT_CURSOR:
				webcursorName = JsonCursorChange.DEFAULT_CURSOR;
				break;
			case Cursor.HAND_CURSOR:
				webcursorName = JsonCursorChange.HAND_CURSOR;
				break;
			case Cursor.CROSSHAIR_CURSOR:
				webcursorName = JsonCursorChange.CROSSHAIR_CURSOR;
				break;
			case Cursor.MOVE_CURSOR:
				webcursorName = JsonCursorChange.MOVE_CURSOR;
				break;
			case Cursor.TEXT_CURSOR:
				webcursorName = JsonCursorChange.TEXT_CURSOR;
				break;
			case Cursor.WAIT_CURSOR:
				webcursorName = JsonCursorChange.WAIT_CURSOR;
				break;
			case Cursor.E_RESIZE_CURSOR:
			case Cursor.W_RESIZE_CURSOR:
				webcursorName = JsonCursorChange.EW_RESIZE_CURSOR;
				break;
			case Cursor.N_RESIZE_CURSOR:
			case Cursor.S_RESIZE_CURSOR:
				webcursorName = JsonCursorChange.NS_RESIZE_CURSOR;
				break;
			case Cursor.NW_RESIZE_CURSOR:
			case Cursor.SE_RESIZE_CURSOR:
				webcursorName = JsonCursorChange.BACKSLASH_RESIZE_CURSOR;
				break;
			case Cursor.NE_RESIZE_CURSOR:
			case Cursor.SW_RESIZE_CURSOR:
				webcursorName = JsonCursorChange.SLASH_RESIZE_CURSOR;
				break;
			default:
				webcursorName = JsonCursorChange.DEFAULT_CURSOR;
			}
		} else {
			webcursorName = overridenCursorName;
		}
		if (!WindowManager.getInstance().getCurrentCursor().equals(webcursorName)) {
			JsonAppFrame f = new JsonAppFrame();
			JsonCursorChange cursorChange = new JsonCursorChange(webcursorName);
			f.cursorChange = cursorChange;
			WindowManager.getInstance().setCurrentCursor(webcursorName);
			Logger.debug("WebPaintDispatcher:notifyCursorUpdate", f);
			Services.getConnectionService().sendJsonObject(f);
		}
	}

	public void notifyCopyEvent(String content) {
		JsonAppFrame f = new JsonAppFrame();
		JsonCopyEvent copyEvent;
		copyEvent = new JsonCopyEvent(content);
		f.copyEvent = copyEvent;
		Logger.debug("WebPaintDispatcher:notifyCopyEvent", f);
		Services.getConnectionService().sendJsonObject(f);
	}

	public void notifyFileDialogActive(WebWindowPeer webWindowPeer) {
		JsonAppFrame f = new JsonAppFrame();
		JsonFileDialogEvent fdEvent = new JsonFileDialogEvent();
		fdEvent.eventType = FileDialogEventType.Open;
		f.fileDialogEvent = fdEvent;
		Logger.info("WebPaintDispatcher:notifyFileTransferBarActive", f);
		fileChooserDialog = Util.discoverFileChooser(webWindowPeer);
		Services.getConnectionService().sendJsonObject(f);
	}

	public void notifyFileDialogHidden(WebWindowPeer webWindowPeer) {
		JsonAppFrame f = new JsonAppFrame();
		JsonFileDialogEvent fdEvent = new JsonFileDialogEvent();
		fdEvent.eventType = FileDialogEventType.Close;
		f.fileDialogEvent = fdEvent;
		Logger.info("WebPaintDispatcher:notifyFileTransferBarActive", f);
		fileChooserDialog = null;
		Services.getConnectionService().sendJsonObject(f);
	}

	public JFileChooser getFileChooserDialog() {
		return fileChooserDialog;
	}

	public void notifyDownloadSelectedFile() {
		if (fileChooserDialog != null) {
			File file = fileChooserDialog.getSelectedFile();
			if (file != null && file.exists() && !file.isDirectory() && file.canRead()) {
				OpenFileResult f = new OpenFileResult();
				f.setClientId(System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID));
				f.setF(file);
				Util.getWebToolkit().getPaintDispatcher().sendJsonObject(f);
			}
		}
	}

	public void notifyDeleteSelectedFile() {
		if (fileChooserDialog != null) {
			File[] selected = fileChooserDialog.getSelectedFiles();
			if ((selected == null || selected.length == 0) && fileChooserDialog.getSelectedFile() != null) {
				selected = new File[] { fileChooserDialog.getSelectedFile() };
			}
			for (File f : selected) {
				if (f.exists() && f.canWrite()) {
					f.delete();
				}
			}
			fileChooserDialog.rescanCurrentDirectory();
		}

	}

}
