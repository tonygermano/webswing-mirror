package org.webswing.dispatch;

import org.webswing.Constants;
import org.webswing.model.internal.ExitMsgInternal;
import org.webswing.model.internal.OpenFileResultMsgInternal;
import org.webswing.model.internal.PrinterJobResultMsgInternal;
import org.webswing.model.s2c.*;
import org.webswing.toolkit.WebCursor;
import org.webswing.toolkit.WebToolkit;
import org.webswing.toolkit.WebWindowPeer;
import org.webswing.toolkit.api.clipboard.PasteRequestContext;
import org.webswing.toolkit.api.clipboard.WebswingClipboardData;
import org.webswing.toolkit.extra.IsolatedFsShellFolderManager;
import org.webswing.toolkit.util.*;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractPaintDispatcher implements PaintDispatcher{
	private volatile Map<String, Set<Rectangle>> areasToUpdate = new HashMap<>();
	private volatile FocusEventMsg focusEvent;
	private volatile AccessibilityMsg accessible;
	private AtomicBoolean clientReadyToReceive = new AtomicBoolean(true);
	private final Long ackTimeout = Long.getLong(Constants.PAINT_ACK_TIMEOUT, 5000);
	private long lastReadyStateTime;
	private JFileChooser fileChooserDialog;
	private JDialog clipboardDialog;
	private ScheduledExecutorService executorService;

	private Object accessibilityLock = new Object();
	private boolean accessibilityUpdateScheduled;
	private Component accessibilityComponent;
	private Integer accessibilityX;
	private Integer accessibilityY;

	public void clientReadyToReceive() {
		synchronized (webPaintLock) {
			clientReadyToReceive.set(true);
		}
	}

	@Override
	public void notifyNewDirtyRegionQueued() {
	}

	@Override
	public RepaintManager getDefaultRepaintManager() {
		return RepaintManager.currentManager(null);
	}

	public void notifyScreenSizeChanged(int oldWidht, int oldHeight, int screenWidth, int screenHeight) {
		clientReadyToReceive();
		Util.resetWindowsPosition(oldWidht, oldHeight);// in case windows moved out of screen by resizing screen.
		notifyWindowRepaintAll();
	}

	public void notifyWindowDecorationUpdated(String guid, Rectangle bounds, Insets insets) {
		if (insets != null && bounds != null) {
			addDirtyArea(guid, new Rectangle(0, 0, bounds.width, insets.top));//top
			addDirtyArea(guid, new Rectangle(0, 0, insets.left, bounds.height));//left
			addDirtyArea(guid, new Rectangle(bounds.width - insets.right, 0, insets.right, bounds.height));//right
			addDirtyArea(guid, new Rectangle(0, bounds.height - insets.bottom, bounds.width, insets.bottom));//bottom
		}
	}

	public void notifyWindowClosed(String guid) {
		removeDirtyArea(guid);
		AppFrameMsgOut f = new AppFrameMsgOut();
		WindowMsg fdEvent = new WindowMsg();
		fdEvent.setId(guid);
		f.setClosedWindow(fdEvent);
		Logger.debug("WebPaintDispatcher:notifyWindowClosed", guid);
		sendObject(f);
	}

	@SuppressWarnings("restriction")
	public void notifyWindowRepaintAll() {
		notifyBackgroundAreaVisible(new Rectangle(Util.getWebToolkit().getScreenSize()));
		if (Util.isDD()) {
			Services.getDirectDrawService().resetCache();
			Util.repaintAllWindow();
		} else {
			for (Window w : Util.getAllWindows()) {
				if (w.isShowing()) {
					addDirtyArea(w);
				}
			}
		}

	}

	public void notifyActionEvent(String windowId, String actionName, String data, byte[] binaryData) {
		AppFrameMsgOut f = new AppFrameMsgOut();

		ActionEventMsgOut actionEvent = new ActionEventMsgOut();
		actionEvent.setWindowId(windowId);
		actionEvent.setActionName(actionName);
		actionEvent.setData(data);
		actionEvent.setBinaryData(binaryData);

		f.setActionEvent(actionEvent);

		Logger.debug("WebPaintDispatcher:notifyActionEvent", f);
		sendObject(f);
	}


	public void notifyOpenLinkAction(URI uri) {
		AppFrameMsgOut f = new AppFrameMsgOut();
		LinkActionMsg linkAction = new LinkActionMsg(LinkActionMsg.LinkActionType.url, uri.toString());
		f.setLinkAction(linkAction);
		Logger.info("WebPaintDispatcher:notifyOpenLinkAction", uri);
		sendObject(f);
	}

	public void notifyCopyEvent(WebswingClipboardData data) {
		AppFrameMsgOut f = new AppFrameMsgOut();
		CopyEventMsg copyEvent;
		copyEvent = new CopyEventMsg(data.getText(), data.getHtml(), data.getImg(), data.getFiles(), false);
		f.setCopyEvent(copyEvent);
		Logger.debug("WebPaintDispatcher:notifyCopyEvent", f);
		sendObject(f);
	}

	public void notifyFileDialogActive(Window window) {
		setFileChooserDialog(Util.discoverFileChooser(window));
		notifyFileDialogActive();
	}

	public void notifyFileDialogActive() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(this::notifyFileDialogActive);
		} else {
			if (getFileChooserDialog() != null) {
				AppFrameMsgOut f = new AppFrameMsgOut();
				FileDialogEventMsg fdEvent = new FileDialogEventMsg();
				f.setFileDialogEvent(fdEvent);
				FileDialogEventMsg.FileDialogEventType fileChooserEventType = Util.getFileChooserEventType(getFileChooserDialog());
				if (fileChooserEventType == FileDialogEventMsg.FileDialogEventType.AutoUpload && getFileChooserDialog().getFileSelectionMode() == JFileChooser.DIRECTORIES_ONLY) {
					//open dialog with auto upload enabled will automatically select the transfer folder
					SwingUtilities.invokeLater(() -> {
						try {
							getFileChooserDialog().setSelectedFile(new File(getFileChooserDialog().getCurrentDirectory().getCanonicalPath()));
							getFileChooserDialog().approveSelection();
						} catch (IOException e) {
							getFileChooserDialog().cancelSelection();
						}
					});
					return;
				}
				fdEvent.setEventType(fileChooserEventType);
				if (FileDialogEventMsg.FileDialogEventType.AutoUpload == fileChooserEventType || FileDialogEventMsg.FileDialogEventType.AutoSave == fileChooserEventType) {
					fdEvent.setAllowDelete(false);
					fdEvent.setAllowDownload(false);
					fdEvent.setAllowUpload(false);
					if (FileDialogEventMsg.FileDialogEventType.AutoUpload == fileChooserEventType) {
						getFileChooserDialog().setCurrentDirectory(Util.getTimestampedTransferFolder("autoupload"));
					}
					Window d = SwingUtilities.getWindowAncestor(getFileChooserDialog());
					d.setBounds(0, 0, 1, 1);
				}
				fdEvent.setSelection(Util.getFileChooserSelection(getFileChooserDialog()));
				fdEvent.addFilter(getFileChooserDialog().getChoosableFileFilters());
				fdEvent.setMultiSelection(getFileChooserDialog().isMultiSelectionEnabled());
				Logger.info("WebPaintDispatcher:notifyFileTransferBarActive " + fileChooserEventType.name());
				sendObject(f);
			}
		}
	}

	public void notifyFileDialogHidden() {
		AppFrameMsgOut f = new AppFrameMsgOut();
		FileDialogEventMsg fdEvent = new FileDialogEventMsg();
		fdEvent.setEventType(FileDialogEventMsg.FileDialogEventType.Close);
		f.setFileDialogEvent(fdEvent);
		Logger.info("WebPaintDispatcher:notifyFileTransferBarHidden " + FileDialogEventMsg.FileDialogEventType.Close.name());
		validateSelection(getFileChooserDialog());
		if (Boolean.getBoolean(Constants.SWING_START_SYS_PROP_ALLOW_AUTO_DOWNLOAD)) {
			if (getFileChooserDialog() != null && getFileChooserDialog().getDialogType() == JFileChooser.SAVE_DIALOG) {
				try {
					Field resultValueField = JFileChooser.class.getDeclaredField("returnValue");
					resultValueField.setAccessible(true);
					if (resultValueField.get(getFileChooserDialog()).equals(JFileChooser.APPROVE_OPTION)) {
						File saveFile = getFileChooserDialog().getSelectedFile();
						if (saveFile != null) {
							OpenFileResultMsgInternal msg = new OpenFileResultMsgInternal();
							msg.setClientId(System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID));
							msg.setFile(saveFile);
							msg.setWaitForFile(true);
							if (saveFile.exists()) {
								msg.setOverwriteDetails(saveFile.length() + "|" + saveFile.lastModified());
							}
							sendObject(msg);
						}
					}
				} catch (Exception e) {
					Logger.warn("Save file dialog's file monitoring failed: " + e.getMessage());
				}
			}
		}
		setFileChooserDialog(null);
		sendObject(f);
	}

	public void notifyDownloadSelectedFile() {
		if (getFileChooserDialog() != null && Boolean.getBoolean(Constants.SWING_START_SYS_PROP_ALLOW_DOWNLOAD)) {
			File file = getFileChooserDialog().getSelectedFile();
			if (file != null && file.exists() && !file.isDirectory() && file.canRead()) {
				OpenFileResultMsgInternal f = new OpenFileResultMsgInternal();
				f.setClientId(System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID));
				f.setFile(file);
				sendObject(f);
			}
		}
	}

	public void notifyDeleteSelectedFile() {
		if (getFileChooserDialog() != null && Boolean.getBoolean(Constants.SWING_START_SYS_PROP_ALLOW_DELETE)) {
			File[] selected = getFileChooserDialog().getSelectedFiles();
			if ((selected == null || selected.length == 0) && getFileChooserDialog().getSelectedFile() != null) {
				selected = new File[] { getFileChooserDialog().getSelectedFile() };
			}
			if (selected != null) {
				for (File f : selected) {
					if (f.exists() && f.canWrite()) {
						boolean deleted=f.delete();
						if(!deleted){
							Logger.info("notifyDeleteSelectedFile: Failed to delete file:"+f.getAbsolutePath());
						}
					}
				}
			}
			getFileChooserDialog().rescanCurrentDirectory();
		}
	}

	@Override
	public void notifyFileRequested(File file,boolean preview) {
		OpenFileResultMsgInternal f = new OpenFileResultMsgInternal();
		f.setClientId(System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID));
		f.setFile(file.getAbsoluteFile());
		f.setPreview(preview);
		sendObject(f);
	}

	public void notifyPrintPdfFile(String id, File f) {
		PrinterJobResultMsgInternal printResult = new PrinterJobResultMsgInternal();
		printResult.setClientId(System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID));
		printResult.setTempFile(true);
		printResult.setId(id);
		printResult.setPdfFile(f);
		sendObject(printResult);
	}

	public void notifyApplicationExiting() {
		notifyApplicationExiting(Integer.getInteger(Constants.SWING_START_SYS_PROP_WAIT_FOR_EXIT, 30000));
	}

	public void notifyApplicationExiting(int waitBeforeKill) {
		ExitMsgInternal f = new ExitMsgInternal();
		f.setWaitForExit(waitBeforeKill);
		sendObject(f);
		getExecutorService().shutdownNow();
	}

	public void notifyUrlRedirect(String url) {
		AppFrameMsgOut result = new AppFrameMsgOut();
		result.setLinkAction(new LinkActionMsg(LinkActionMsg.LinkActionType.redirect, url));
		sendObject(result);
	}

	public void requestBrowserClipboard(PasteRequestContext ctx) {
		AppFrameMsgOut result = new AppFrameMsgOut();
		PasteRequestMsg paste = new PasteRequestMsg();
		paste.setTitle(ctx.getTitle());
		paste.setMessage(ctx.getMessage());
		result.setPasteRequest(paste);
		sendObject(result);

		Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
		setClipboardDialog(new JDialog(window, JDialog.DEFAULT_MODALITY_TYPE));
		getClipboardDialog().setBounds(new Rectangle(0, 0, 1, 1));
		getClipboardDialog().setVisible(true);//this call is blocking until dialog is closed
	}

	public boolean closePasteRequestDialog() {
		if (getClipboardDialog() != null) {
			boolean result = getClipboardDialog().isVisible();
			getClipboardDialog().setVisible(false);
			getClipboardDialog().dispose();
			return result;
		}
		return false;
	}

	public void notifyComponentTreeRequested() {
		if (!Util.isTestMode()) {
			return;
		}
		AppFrameMsgOut f = new AppFrameMsgOut();
		f.setComponentTree(ToolkitUtil.getComponentTree());
		Logger.debug("WebPaintDispatcher:sendComponentTree");
		sendObject(f);
	}

	@Override
	public void notifyWindowSwitchList() {
		AppFrameMsgOut f = new AppFrameMsgOut();
		f.setWindowSwitchList(Util.getWindowSwitchList());
		Logger.debug("WebPaintDispatcher:notifyWindowSwitchList");
		sendObject(f);
	}

	protected ScheduledExecutorService getExecutorService(){
		if(this.executorService==null){
			this.executorService = Executors.newScheduledThreadPool(1, DeamonThreadFactory.getInstance("Webswing Paint Dispatcher"));
		}
		return this.executorService;
	}

	protected void sendObject(Serializable object) {
		Logger.debug("WebPaintDispatcher:sendJsonObject", object);
		Services.getConnectionService().sendObject(object);
	}


	protected boolean isClientReadyToReceiveOrResetAfterTimedOut() {
		synchronized (webPaintLock) {
			if (!clientReadyToReceive.get()) {
				if (System.currentTimeMillis() - lastReadyStateTime > ackTimeout) {
					Logger.debug("paintDispatcher.clientReadyToReceive re-enabled after timeout");
					if (Util.isDD()) {
						Services.getDirectDrawService().resetCache();
					}
					clientReadyToReceive();
					lastReadyStateTime = System.currentTimeMillis();
					return true;
				}
				return false;
			}else{
				lastReadyStateTime = System.currentTimeMillis();
				return true;
			}
		}
	}

	protected boolean isClientReadyToReceive() {
		synchronized (webPaintLock) {
			return clientReadyToReceive.get();
		}
	}

	protected void setClientNotReady() {
		synchronized (webPaintLock) {
			clientReadyToReceive.set(false);
		}
	}

	protected Map<String, Set<Rectangle>> popProcessableDirtyAreas() {
		Map<String, Set<Rectangle>> currentAreasToUpdate;
		currentAreasToUpdate = areasToUpdate;
		areasToUpdate = Util.postponeNonShowingAreas(currentAreasToUpdate);
		return currentAreasToUpdate;
	}

	protected void addDirtyArea(String guid, Rectangle repaintedArea, boolean reset) {
		synchronized (webPaintLock) {
			if (repaintedArea.width > 0 && repaintedArea.height > 0) {
				if (areasToUpdate.containsKey(guid)) {
					Set<Rectangle> rset = areasToUpdate.get(guid);
					if(reset){
						rset.clear();
					}
					rset.add(repaintedArea);
				} else {
					Set<Rectangle> rset = new HashSet<>();
					rset.add(repaintedArea);
					areasToUpdate.put(guid, rset);
				}
				Logger.trace("WebPaintDispatcher:addDirtyArea", guid, repaintedArea);
			}
		}
	}

	protected  void addDirtyArea(String guid, Rectangle repaintedArea) {
		addDirtyArea(guid,repaintedArea,false);
	}

	protected void addDirtyArea(Window w) {
		Rectangle bounds = w.getBounds();
		WebWindowPeer peer = (WebWindowPeer) WebToolkit.targetToPeer(w);
		if (peer != null) {
			addDirtyArea(peer.getGuid(), new Rectangle(0, 0, bounds.width, bounds.height));
		}
	}

	private void removeDirtyArea(String guid) {
		synchronized (webPaintLock) {
			areasToUpdate.remove(guid);
		}
	}

	protected void fillFocusEvent(AppFrameMsgOut json) {
		if (focusEvent != null) {
			json.setFocusEvent(focusEvent);
			focusEvent = null;
		}
	}
	
	public void notifyFocusEvent(FocusEventMsg msg) {
		focusEvent = msg;
		notifyAccessibilityInfoUpdate();
	}
	
	public void notifyAccessibilityInfoUpdate(Component a, int x, int y) {
		if (!Util.isAccessibilityEnabled()) {
			return;
		}
		
		synchronized (accessibilityLock) {
			accessibilityComponent = a;
			accessibilityX = x;
			accessibilityY = y;
			
			if (accessibilityUpdateScheduled) {
				return;
			}
			accessibilityUpdateScheduled = true;
		}
		
		SwingUtilities.invokeLater(() -> {
			processAccessibility();
		});
	}

	public void notifyAccessibilityInfoUpdate() {
		if (!Util.isAccessibilityEnabled()) {
			return;
		}
		
		synchronized (accessibilityLock) {
			accessibilityComponent = null;
			accessibilityX = null;
			accessibilityY = null;
			
			if (accessibilityUpdateScheduled) {
				return;
			}
			accessibilityUpdateScheduled = true;
		}
		
		SwingUtilities.invokeLater(() -> {
			processAccessibility();
		});
	}
	
	private void processAccessibility() {
		synchronized (accessibilityLock) {
			if (accessibilityComponent != null) {
				sendNotifyAccessibilityInfoUpdate(Services.getConnectionService().getAccessibilityInfo(accessibilityComponent, accessibilityX, accessibilityY));
			} else {
				sendNotifyAccessibilityInfoUpdate(Services.getConnectionService().getAccessibilityInfo());
			}
			accessibilityUpdateScheduled = false;
		}
	}
	
	@Override
	public void clearAccessibilityInfoState() {
		accessible = null;
	}
	
	@Override
	public void notifyAccessibilityInfoUpdate(AccessibilityMsg msg) {
		sendNotifyAccessibilityInfoUpdate(msg);
	}
	
	private void sendNotifyAccessibilityInfoUpdate(AccessibilityMsg newAccessible) {
		if (newAccessible == null || (accessible != null && newAccessible.equals(accessible))) {
			return;
		}
		accessible = newAccessible;
		
		AppFrameMsgOut f = new AppFrameMsgOut();
		f.setAccessible(accessible);
		
		Logger.debug("WebPaintDispatcher:sendNotifyAccessibilityInfo", f);
		sendObject(f);
	}

	public JFileChooser getFileChooserDialog() {
		return fileChooserDialog;
	}

	protected void setFileChooserDialog(JFileChooser fileChooserDialog) {
		this.fileChooserDialog = fileChooserDialog;
	}

	protected JDialog getClipboardDialog() {
		return clipboardDialog;
	}

	protected void setClipboardDialog(JDialog clipboardDialog) {
		this.clipboardDialog = clipboardDialog;
	}

	public void notifyCursorUpdate(Cursor cursor, Cursor overridenCursorName, String winId) {
		String webcursorName = null;
		Cursor webcursor = null;
		if (overridenCursorName == null) {
			switch (cursor.getType()) {
			case Cursor.DEFAULT_CURSOR:
				webcursorName = CursorChangeEventMsg.DEFAULT_CURSOR;
				break;
			case Cursor.HAND_CURSOR:
				webcursorName = CursorChangeEventMsg.HAND_CURSOR;
				break;
			case Cursor.CROSSHAIR_CURSOR:
				webcursorName = CursorChangeEventMsg.CROSSHAIR_CURSOR;
				break;
			case Cursor.MOVE_CURSOR:
				webcursorName = CursorChangeEventMsg.MOVE_CURSOR;
				break;
			case Cursor.TEXT_CURSOR:
				webcursorName = CursorChangeEventMsg.TEXT_CURSOR;
				break;
			case Cursor.WAIT_CURSOR:
				webcursorName = CursorChangeEventMsg.WAIT_CURSOR;
				break;
			case Cursor.E_RESIZE_CURSOR:
			case Cursor.W_RESIZE_CURSOR:
				webcursorName = CursorChangeEventMsg.EW_RESIZE_CURSOR;
				break;
			case Cursor.N_RESIZE_CURSOR:
			case Cursor.S_RESIZE_CURSOR:
				webcursorName = CursorChangeEventMsg.NS_RESIZE_CURSOR;
				break;
			case Cursor.NW_RESIZE_CURSOR:
			case Cursor.SE_RESIZE_CURSOR:
				webcursorName = CursorChangeEventMsg.BACKSLASH_RESIZE_CURSOR;
				break;
			case Cursor.NE_RESIZE_CURSOR:
			case Cursor.SW_RESIZE_CURSOR:
				webcursorName = CursorChangeEventMsg.SLASH_RESIZE_CURSOR;
				break;
			case Cursor.CUSTOM_CURSOR:
				webcursorName = cursor.getName();
				break;
			default:
				webcursorName = CursorChangeEventMsg.DEFAULT_CURSOR;
			}
			webcursor = cursor;
		} else {
			webcursor = overridenCursorName;
			webcursorName = overridenCursorName.getName();
		}
		String currentCursor = getCurrentCursor(winId);
		if (currentCursor != null && !currentCursor.equals(webcursorName)) {
			AppFrameMsgOut f = new AppFrameMsgOut();
			CursorChangeEventMsg cursorChange = new CursorChangeEventMsg(webcursorName);
			cursorChange.setWinId(winId);
			if (webcursor instanceof WebCursor) {
				WebCursor c = (WebCursor) webcursor;
				BufferedImage img = c.getImage();
				cursorChange.setB64img(Services.getImageService().getPngImage(img));
				cursorChange.setX(c.getHotSpot() != null ? c.getHotSpot().x : 0);
				cursorChange.setY(c.getHotSpot() != null ? c.getHotSpot().y : 0);
				File file = Util.convertAndSaveCursor(img, cursorChange.getX(), cursorChange.getY());
				if (file != null) {
					cursorChange.setCurFile(file.getAbsolutePath());
				}
			}
			f.setCursorChange(cursorChange);
			setCurrentCursor(winId, webcursorName);
			Logger.debug("WebPaintDispatcher:notifyCursorUpdate", f);
			sendObject(f);
		}
	}

	protected abstract String getCurrentCursor(String winId);

	protected abstract void setCurrentCursor(String winId, String cursor) ;

	protected void validateSelection(JFileChooser fileChooserDialog) {
		if (Boolean.getBoolean(Constants.SWING_START_SYS_PROP_ISOLATED_FS) && fileChooserDialog != null) {

			try {
				if (fileChooserDialog.getSelectedFile() != null) {
					if (!IsolatedFsShellFolderManager.isSubfolderOfRoots(fileChooserDialog.getSelectedFile())) {
						throw new IOException("Invalid selection " + fileChooserDialog.getSelectedFile());
					}
				}
				if (fileChooserDialog.getSelectedFiles() != null && fileChooserDialog.getSelectedFiles().length > 0) {
					for (File selection : fileChooserDialog.getSelectedFiles()) {
						if (!IsolatedFsShellFolderManager.isSubfolderOfRoots(selection)) {
							throw new IOException("Invalid selection " + selection);
						}
					}
				}
			} catch (IOException e) {
				Logger.error("Selection is outside isolated path", e);
				fileChooserDialog.cancelSelection();
			}
		}
	}

	public void notifyAudioEventPlay(String id, byte[] data, Float time, Integer loop) {
		AppFrameMsgOut f = new AppFrameMsgOut();
		f.setAudioEvent(new AudioEventMsgOut(id, AudioEventMsgOut.AudioEventType.play, data, time, loop));

		Logger.debug("WebPaintDispatcher:notifyAudioEvent play");
		sendObject(f);
	}

	public void notifyAudioEventStop(String id) {
		AppFrameMsgOut f = new AppFrameMsgOut();
		f.setAudioEvent(new AudioEventMsgOut(id, AudioEventMsgOut.AudioEventType.stop));

		Logger.debug("WebPaintDispatcher:notifyAudioEvent stop");
		sendObject(f);
	}

	public void notifyAudioEventUpdate(String id, Float time, Integer loop) {
		AppFrameMsgOut f = new AppFrameMsgOut();
		f.setAudioEvent(new AudioEventMsgOut(id, AudioEventMsgOut.AudioEventType.update, time, loop));

		Logger.debug("WebPaintDispatcher:notifyAudioEvent update");
		sendObject(f);
	}

	public void notifyAudioEventDispose(String id) {
		AppFrameMsgOut f = new AppFrameMsgOut();
		f.setAudioEvent(new AudioEventMsgOut(id, AudioEventMsgOut.AudioEventType.dispose));

		Logger.debug("WebPaintDispatcher:notifyAudioEvent dispose");
		sendObject(f);
	}
	
}
