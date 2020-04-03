package org.webswing.dispatch;

import org.webswing.model.s2c.AppFrameMsgOut;
import org.webswing.model.s2c.WindowDockMsg;
import org.webswing.toolkit.WebComponentPeer;
import org.webswing.toolkit.WebToolkit;
import org.webswing.toolkit.WebWindowPeer;
import org.webswing.toolkit.api.component.HtmlPanel;
import org.webswing.toolkit.extra.WebRepaintManager;
import org.webswing.toolkit.util.Logger;
import org.webswing.toolkit.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CwmPaintDispatcher extends AbstractPaintDispatcher {

	private Map<Container, Void> registeredContainers = new WeakHashMap<>();
	private Map<HtmlPanel, Void> registeredHtmlPanels = new WeakHashMap<>();
	private RepaintManager defaultRepaintManager;
	private AtomicBoolean sendUpdateScheduled = new AtomicBoolean(false);


	public CwmPaintDispatcher() {
		Runnable readyToReceiveMonitor= () -> {
			try {
				isClientReadyToReceiveOrResetAfterTimedOut();
			} catch (Exception e) {
				Logger.error("readyToReceiveMonitor:failed",e);
			}
		};
		getExecutorService().scheduleWithFixedDelay(readyToReceiveMonitor, 1, 1, TimeUnit.SECONDS);
		
		// set JPopupMenu and JTooltip to be rendered as heavyweight component to get its own canvas, this is needed when JPopupMenu opens over an HtmlPanel
		// this must be set prior to JPopupMenu instantiation
		PopupFactory.setSharedInstance(new PopupFactory(){
			public Popup getPopup(Component owner, Component contents,
					int x, int y) throws IllegalArgumentException {
				try {
					Field popupTypeField = PopupFactory.class.getDeclaredField("popupType");
					popupTypeField.setAccessible(true);
					popupTypeField.set(this,2);
				} catch (Exception e) {
					Logger.warn("Failed to force Heavyweight popup for CWM mode.",e);
				}
				return super.getPopup(owner, contents, x, y);
			}
		});
	}

	@Override
	public void clientReadyToReceive() {
		if(!isClientReadyToReceive()){
			WebRepaintManager.processDirtyComponents();
		}
		super.clientReadyToReceive();
	}

	@Override
	protected void addDirtyArea(String guid, Rectangle repaintedArea, boolean reset) {
		synchronized (webPaintLock) {
			super.addDirtyArea(guid, repaintedArea, reset);
			scheduleSendUpdate();
		}
	}

	private void scheduleSendUpdate() {
		synchronized (webPaintLock) {
			if (!sendUpdateScheduled.get()) {
				sendUpdateScheduled.set(true);
				SwingUtilities.invokeLater(this::sendUpdate);
			}
		}
	}

	@Override
	public void notifyNewDirtyRegionQueued() {
		SwingUtilities.invokeLater(() -> {
			synchronized (webPaintLock) {
				if(isClientReadyToReceive() ){
					WebRepaintManager.processDirtyComponents();
				}
			}
		});
	}

	@Override
	public RepaintManager getDefaultRepaintManager() {
		if(defaultRepaintManager==null) {
			defaultRepaintManager=new RepaintManager() {
				@Override
				public void paintDirtyRegions() {
					sendUpdateScheduled.set(true);
					super.paintDirtyRegions();
					sendUpdate();
				}
			};
		}
		return defaultRepaintManager;
	}

	private void sendUpdate() {
		try {
			AppFrameMsgOut json;
			Map<String, Map<Integer, BufferedImage>> windowImages = null;
			Map<String, Image> windowWebImages = null;
			Map<String, Set<Rectangle>> currentAreasToUpdate;
			synchronized (Util.getWebToolkit().getTreeLock()) {
				synchronized (webPaintLock) {
					currentAreasToUpdate = popProcessableDirtyAreas();

					json = Util.fillWithCompositingWindowsData(currentAreasToUpdate);

					if (!currentAreasToUpdate.isEmpty()) {
						if (Util.isDD()) {
							windowWebImages = Util.extractWindowWebImages(currentAreasToUpdate.keySet(), new HashMap<>());
						} else {
							windowImages = Util.extractWindowImages(json, new HashMap<>());
						}
					}

					fillFocusEvent(json);
					setClientNotReady();
					sendUpdateScheduled.set(false);
				}
			}
			Logger.trace("contentSender:paintJson", json);
			if (!currentAreasToUpdate.isEmpty()) {
				if (Util.isDD()) {
					Logger.trace("contentSender:pngWebImageEncodingStart", json.hashCode());
					Util.encodeWindowWebImages(windowWebImages, json);
					Logger.trace("contentSender:pngWebImageEncodingDone", json.hashCode());
				} else {
					Logger.trace("contentSender:pngEncodingStart", json.hashCode());
					Util.encodeWindowImages(windowImages, json);
					Logger.trace("contentSender:pngEncodingDone", json.hashCode());
				}
			}
			json.setDirectDraw(Util.isDD());
			json.setCompositingWM(Util.isCompositingWM());
			json.setSendTimestamp("" + System.currentTimeMillis());
			sendObject(json);
		} catch (Throwable e) {
			Logger.error("contentSender:error", e);
		}
	}

	public void notifyWindowAreaRepainted(String guid, Rectangle repaintedArea) {
		addDirtyArea(guid, repaintedArea);
	}

	public void notifyWindowAreaVisible(String guid, Rectangle visibleArea) {
		//not needed in CWM
	}

	public void notifyWindowBoundsChanged(String guid, Rectangle newBounds) {
		//not needed in CWM
	}

	public void notifyWindowActivated(Window activeWindow) {
		WebComponentPeer activeWindowPeer = (WebComponentPeer) WebToolkit.targetToPeer(activeWindow);
		activeWindowPeer.updateWindowDecorationImage();
	}

	public void notifyWindowDeactivated(Window oldActiveWindow) {
		WebComponentPeer oldActiveWindowPeer = (WebComponentPeer) WebToolkit.targetToPeer(oldActiveWindow);
		oldActiveWindowPeer.updateWindowDecorationImage();
	}

	public void notifyWindowZOrderChanged(Window w) {
		scheduleSendUpdate();
	}

	public void notifyWindowMaximized(JFrame target) {
		scheduleSendUpdate();
	}

	public void notifyBackgroundAreaVisible(Rectangle toRepaint) {
		//not needed in CWM
	}

	public void notifyWindowMoved(Window w, int zIndex, Rectangle from, Rectangle to) {
		scheduleSendUpdate();
	}
	
	public void notifyWindowDockAction(String windowId) {
		AppFrameMsgOut f = new AppFrameMsgOut();
		
		WindowDockMsg dockMsg = new WindowDockMsg();
		dockMsg.setWindowId(windowId);
		
		f.setDockAction(dockMsg);
		
		Logger.debug("WebPaintDispatcher:notifyWindowDock", f);
		sendObject(f);
	}

	public void registerWebContainer(Container container) {
		synchronized (CwmPaintDispatcher.webPaintLock) {
			registeredContainers.put(container, null);
		}
	}

	public Map<Window, List<Container>> getRegisteredWebContainersAsMap() {
		synchronized (CwmPaintDispatcher.webPaintLock) {
			return Util.toWindowMapSynced(registeredContainers.keySet());
		}
	}

	public void registerHtmlPanel(HtmlPanel hp) {
		synchronized (CwmPaintDispatcher.webPaintLock) {
			registeredHtmlPanels.put(hp, null);
		}
	}

	public Map<Window, List<HtmlPanel>> getRegisteredHtmlPanelsAsMap() {
		synchronized (CwmPaintDispatcher.webPaintLock) {
			return Util.toWindowMapSynced(registeredHtmlPanels.keySet());
		}
	}

	public HtmlPanel findHtmlPanelById(String id) {
		synchronized (CwmPaintDispatcher.webPaintLock) {
			for (HtmlPanel hp : registeredHtmlPanels.keySet()) {
				if (id.equals(System.identityHashCode(hp) + "")) {
					return hp;
				}
			}
		}
		return null;
	}

	@Override
	protected String getCurrentCursor(String winId) {
		WebWindowPeer peer = Util.findWindowPeerById(winId);
		return peer == null ? null : peer.getCurrentCursor();
	}

	@Override
	protected void setCurrentCursor(String winId, String cursor) {
		WebWindowPeer peer = Util.findWindowPeerById(winId);
		if (peer != null) {
			peer.setCurrentCursor(cursor);
		}
	}
}
