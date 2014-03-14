package org.webswing.dispatch;

import java.awt.Rectangle;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.webswing.common.ImageServiceIfc;
import org.webswing.common.ServerConnectionIfc;
import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.toolkit.WebToolkit;
import org.webswing.toolkit.WebWindowPeer;
import org.webswing.toolkit.extra.WindowManager;
import org.webswing.util.Util;

public class WebPaintDispatcher {

    public static final Object webPaintLock = new Object();
    private ServerConnectionIfc serverConnection;

    Map<String, Rectangle> areasToUpdate = new HashMap<String, Rectangle>();

    private ScheduledExecutorService contentSender = Executors.newScheduledThreadPool(1);

    public WebPaintDispatcher(final ServerConnectionIfc serverConnection, final ImageServiceIfc imageService) {
        this.serverConnection = serverConnection;
        Runnable sendUpdate = new Runnable() {

            public void run() {
                try {
                    JsonAppFrame json = new JsonAppFrame();
                    Map<String, BufferedImage> windowImages = new HashMap<String, BufferedImage>();
                    Map<String, List<Rectangle>> windowNonVisibleAreas = new HashMap<String, List<Rectangle>>();
                    Map<String, Rectangle> currentAreasToUpdate = null;
                    synchronized (webPaintLock) {
                        if (areasToUpdate.size() == 0) {
                            return;
                        }
                        currentAreasToUpdate = areasToUpdate;
                        areasToUpdate = Util.postponeNonShowingAreas(currentAreasToUpdate);
                        if (currentAreasToUpdate.size() == 0) {
                            return;
                        }
                        Util.fillJsonWithWindowsData(currentAreasToUpdate, json);
                        Util.extractWindowImages(windowImages, currentAreasToUpdate);
                        WindowManager.getInstance().extractNonVisibleAreas(windowNonVisibleAreas);
                    }
                    Util.encodeWindowImages(windowImages, windowNonVisibleAreas, json, imageService);
                    serverConnection.sendJsonObject(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        contentSender.scheduleWithFixedDelay(sendUpdate, 10, 10, TimeUnit.MILLISECONDS);
    }

    public void notifyShutdown() {
        serverConnection.sendShutdownNotification();
    }

    public void sendJsonObject(Serializable object) {
        serverConnection.sendJsonObject(object);
    }

    public void notifyWindowAreaRepainted(String guid, Rectangle repaintedArea) {
        synchronized (webPaintLock) {
            if (validBounds(repaintedArea)) {
                if (areasToUpdate.containsKey(guid)) {
                    Rectangle r = areasToUpdate.get(guid);
                    Rectangle newR = SwingUtilities.computeUnion(r.x, r.y, r.width, r.height, repaintedArea);
                    areasToUpdate.put(guid, newR);
                } else {
                    areasToUpdate.put(guid, repaintedArea);
                }
            }
        }
    }

    public void notifyWindowBoundsChanged(String guid, Rectangle newBounds) {
        synchronized (webPaintLock) {
            if (validBounds(newBounds)) {
                areasToUpdate.put(guid, newBounds);
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
    }

    public void notifyWindowRepaint(Window w) {
        Rectangle bounds = w.getBounds();
        WebWindowPeer peer = (WebWindowPeer) WebToolkit.targetToPeer(w);
        notifyWindowAreaRepainted(peer.getGuid(), new Rectangle(0, 0, bounds.width, bounds.height));
    }

    public void notifyWindowRepaintAll() {
        for(Window w:Window.getWindows()){
            if(w.isShowing()){
                notifyWindowRepaint(w);
            }
        }
    }
    
    public void notifyBackgroundRepainted(Rectangle toRepaint) {
        notifyWindowAreaRepainted(WebToolkit.BACKGROUND_WINDOW_ID, toRepaint);

    }

}
