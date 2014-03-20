package org.webswing.dispatch;

import java.awt.Rectangle;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.webswing.common.ImageServiceIfc;
import org.webswing.common.ServerConnectionIfc;
import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.model.s2c.JsonLinkAction;
import org.webswing.model.s2c.JsonLinkAction.JsonLinkActionType;
import org.webswing.toolkit.WebToolkit;
import org.webswing.toolkit.WebWindowPeer;
import org.webswing.toolkit.extra.WindowManager;
import org.webswing.util.Util;

public class WebPaintDispatcher {

    public static final Object webPaintLock = new Object();
    private ServerConnectionIfc serverConnection;

    private volatile Map<String, Set<Rectangle>> areasToUpdate = new HashMap<String,Set<Rectangle>>();
    private volatile boolean clientReadyToReceive = true;
    private long lastReadyStateTime;

    private ScheduledExecutorService contentSender = Executors.newScheduledThreadPool(1);

    public WebPaintDispatcher(final ServerConnectionIfc serverConnection, final ImageServiceIfc imageService) {
        this.serverConnection = serverConnection;
        Runnable sendUpdate = new Runnable() {

            public void run() {
                try {
                    JsonAppFrame json;
                    Map<String,  Map<Integer,BufferedImage>> windowImages;
                    Map<String, List<Rectangle>> windowNonVisibleAreas;
                    Map<String, Set<Rectangle>> currentAreasToUpdate = null;
                    synchronized (webPaintLock) {
                        if (clientReadyToReceive) {
                            lastReadyStateTime = System.currentTimeMillis();
                        }
                        if (areasToUpdate.size() == 0 || !clientReadyToReceive) {
                            if (!clientReadyToReceive && (System.currentTimeMillis() - lastReadyStateTime) > 2000) {
                                clientReadyToReceive = true;
                            }
                            return;
                        }
                        json = new JsonAppFrame();
                        windowImages = new HashMap<String, Map<Integer,BufferedImage>>();
                        windowNonVisibleAreas = new HashMap<String, List<Rectangle>>();
                        currentAreasToUpdate = areasToUpdate;
                        areasToUpdate = Util.postponeNonShowingAreas(currentAreasToUpdate);
                        if (currentAreasToUpdate.size() == 0) {
                            return;
                        }
                        WindowManager.getInstance().extractNonVisibleAreas(windowNonVisibleAreas);
                        Util.fillJsonWithWindowsData(currentAreasToUpdate,windowNonVisibleAreas, json);
                        Util.extractWindowImages(windowImages, json);
                        clientReadyToReceive = false;
                    }
                    Util.encodeWindowImages(windowImages,json, imageService);
                    serverConnection.sendJsonObject(json);
                } catch (Exception e) {
                    e.printStackTrace();
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
        serverConnection.sendShutdownNotification();
    }

    public void sendJsonObject(Serializable object) {
        serverConnection.sendJsonObject(object);
    }

    public void notifyWindowAreaRepainted(String guid, Rectangle repaintedArea) {
        synchronized (webPaintLock) {
            if (validBounds(repaintedArea)) {
                if (areasToUpdate.containsKey(guid)) {
                    Set<Rectangle> rset = areasToUpdate.get(guid);
                    rset.add(repaintedArea);
                } else {
                    Set<Rectangle> rset= new HashSet<Rectangle>();  
                    rset.add(repaintedArea);
                    areasToUpdate.put(guid, rset);
                }
            }
        }
    }

    public void notifyWindowBoundsChanged(String guid, Rectangle newBounds) {
        synchronized (webPaintLock) {
            if (validBounds(newBounds)) {
                Set<Rectangle> rset;
                if (areasToUpdate.containsKey(guid)) {
                    rset=areasToUpdate.get(guid);
                    rset.clear();
                }else{
                    rset= new HashSet<Rectangle>(); 
                    areasToUpdate.put(guid, rset);
                }
                rset.add(newBounds);
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
        serverConnection.sendJsonObject(f);
    }

    public void resetWindowsPosition() {
        for (Window w : Window.getWindows()) {
            WebWindowPeer peer=(WebWindowPeer) WebToolkit.targetToPeer(w);
            Rectangle b = w.getBounds();
            peer.setBounds(b.x, b.y, b.width, b.height, 0);
        }
    }

}
