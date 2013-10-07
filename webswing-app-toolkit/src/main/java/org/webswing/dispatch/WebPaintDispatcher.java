package org.webswing.dispatch;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.webswing.common.ImageServiceIfc;
import org.webswing.common.ServerConnectionIfc;
import org.webswing.dispatch.update.Update;
import org.webswing.model.s2c.JsonAppFrame;

public class WebPaintDispatcher {

    private ServerConnectionIfc serverConnection;
    private ImageServiceIfc imageService;

    private Queue<Update> prepareQueue = new ConcurrentLinkedQueue<Update>();
    private Queue<Update> updateQueue = new ConcurrentLinkedQueue<Update>();

    private ExecutorService contentPreparator = Executors.newSingleThreadExecutor();
    private ScheduledExecutorService contentSender = Executors.newScheduledThreadPool(1);

    public WebPaintDispatcher(final ServerConnectionIfc serverConnection, ImageServiceIfc imageService) {
        this.serverConnection = serverConnection;
        this.imageService = imageService;
        Runnable sendUpdate = new Runnable() {

            public void run() {
                JsonAppFrame json = new JsonAppFrame();
                Update update;
                boolean hasUpdate = false;
                while ((update = updateQueue.poll()) != null) {
                    update.updateAppFrame(json);
                    hasUpdate = true;
                }
                if (hasUpdate) {
                    serverConnection.sendJsonObject(json);
                }
            }
        };
        contentSender.scheduleWithFixedDelay(sendUpdate, 200, 200, TimeUnit.MILLISECONDS);
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

    public void update(Update u) {
        prepareQueue.add(u);
        contentPreparator.execute(new Runnable() {

            public void run() {
                while (prepareQueue.size() > 0) {
                    Update update = prepareQueue.poll();
                    update.prepareUpdate();
                    updateQueue.add(update);
                }
            }
        });
    }
}
