package org.webswing.dispatch;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.webswing.common.ImageServiceIfc;
import org.webswing.common.ServerConnectionIfc;
import org.webswing.dispatch.update.Update;
import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.toolkit.extra.WindowManager;
import org.webswing.util.Util;

public class WebPaintDispatcher {

    public static final Object webPaintLock = new Object();
    private ServerConnectionIfc serverConnection;
    private ImageServiceIfc imageService;

    private Queue<Update> updateQueue = new ConcurrentLinkedQueue<Update>();

    private ScheduledExecutorService contentSender = Executors.newScheduledThreadPool(1);

    public WebPaintDispatcher(final ServerConnectionIfc serverConnection, final ImageServiceIfc imageService) {
        this.serverConnection = serverConnection;
        this.imageService = imageService;
        Runnable sendUpdate = new Runnable() {

            public void run() {
                try {
                    JsonAppFrame json = new JsonAppFrame();
                    if (updateQueue.size() > 0) {
                        Map<String,BufferedImage> windowImages=new HashMap<String, BufferedImage>();
                        Map<String,List<Rectangle>> windowNonVisibleAreas=new HashMap<String, List<Rectangle>>();
                        synchronized (webPaintLock) {
                            Update update;
                            while ((update = updateQueue.poll()) != null) {
                                update.updateAppFrame(json);
                            }
                            Util.extractWindowImages(windowImages,json);
                            WindowManager.getInstance().extractNonVisibleAreas(windowNonVisibleAreas);
                        }
                        Util.encodeWindowImages(windowImages,windowNonVisibleAreas,json,imageService);
                        serverConnection.sendJsonObject(json);
                    }
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

    public void enqueueUpdate(Update updateGraphics) {
        updateQueue.add(updateGraphics);
    }

}
