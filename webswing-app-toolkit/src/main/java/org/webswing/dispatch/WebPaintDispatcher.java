package org.webswing.dispatch;

import java.awt.Rectangle;
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
import org.webswing.toolkit.extra.WindowManager;
import org.webswing.util.Util;

public class WebPaintDispatcher {

    public static final Object webPaintLock = new Object();
    private ServerConnectionIfc serverConnection;
    private ImageServiceIfc imageService;

    Map<String, Rectangle> areasToUpdate = new HashMap<String, Rectangle>();

    private ScheduledExecutorService contentSender = Executors.newScheduledThreadPool(1);

    public WebPaintDispatcher(final ServerConnectionIfc serverConnection, final ImageServiceIfc imageService) {
        this.serverConnection = serverConnection;
        this.imageService = imageService;
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
                        areasToUpdate = new HashMap<String, Rectangle>();
                        Util.fillJsonWithWindowsData(currentAreasToUpdate,json);
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

    public ImageServiceIfc getImageService() {
        return imageService;
    }

    public void notifyWindowAreaRepainted(String guid, Rectangle repaintedArea) {
        synchronized (webPaintLock) {
            if (areasToUpdate.containsKey(guid)) {
                Rectangle r = areasToUpdate.get(guid);
                Rectangle newR = SwingUtilities.computeUnion(r.x, r.y, r.width, r.height, repaintedArea);
                areasToUpdate.put(guid, newR);
            } else {
                areasToUpdate.put(guid, repaintedArea);
            }
        }
    }

    public void notifyWindowBoundsChanged(String guid, Rectangle newBounds) {
        synchronized (webPaintLock) {
            areasToUpdate.put(guid, newBounds);
        }
    }
    
    public void notifyWindowClosed(String guid){
        synchronized (webPaintLock) {
            areasToUpdate.remove(guid);
        }
    }

}
