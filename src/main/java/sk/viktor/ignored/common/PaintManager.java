package sk.viktor.ignored.common;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;

import sk.viktor.SwingClassloader;
import sk.viktor.ignored.model.c2s.JsonConnectionHandshake;
import sk.viktor.ignored.model.c2s.JsonEventMouse;
import sk.viktor.ignored.model.s2c.JsonCopyAreaRequest;
import sk.viktor.ignored.model.s2c.JsonPaintRequest;
import sk.viktor.util.Util;

import com.corundumstudio.socketio.SocketIOClient;

public class PaintManager {

    private static Map<String, PaintManager> instances= new HashMap<String, PaintManager>();
    private String clientId;
    private SocketIOClient client;
    private Map<String, Window> windows = new HashMap<String, Window>();
    private Long currentPaintRequestSeq = 0L;
    public Map<String, ChannelBuffer> bufferMap = new HashMap<String, ChannelBuffer>();

    public PaintManager(String clientId, SocketIOClient client) {
        this.clientId = clientId;
        this.client = client;
    }

    public void updateComponentImage(String componentId, BufferedImage imageContent) {
        try {
            synchronized (bufferMap) {
                if (!bufferMap.containsKey(componentId)) {
                    bufferMap.put(componentId, ChannelBuffers.dynamicBuffer());
                }
            }
            ChannelBuffer buffer = bufferMap.get(componentId);
            OutputStream os = new ChannelBufferOutputStream(buffer);
            ImageIO.write(imageContent, "png", ImageIO.createImageOutputStream(os));
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Graphics beforePaintInterceptor(Graphics g, JComponent c) {
        GraphicsWrapper result;
        if (g instanceof GraphicsWrapper) {
            result = (GraphicsWrapper) g;
            if (result.getRootPaintComponent() == null) {
                result.setRootPaintComponent(c);
            }
        } else {
            return g;
        }
        return result;
    }

    public static void afterPaintInterceptor(Graphics g, JComponent c) {
        if (g instanceof GraphicsWrapper) {
            GraphicsWrapper gw = (GraphicsWrapper) g;
            if (Util.getObjectIdentity(gw.getRootPaintComponent()).equals(Util.getObjectIdentity(c))) {
                getInstance(Util.resolveClientId(c)).doPaint(gw, c);
            }
        }
    }

    public void doPaint(GraphicsWrapper gw, JComponent c) {
        BufferedImage img = gw.getImg();
        if (img != null) {
            long seq = nextSeq(client);
            String identity = "" + seq;
            updateComponentImage(identity, img);
            client.sendJsonObject(new JsonPaintRequest(Util.resolveClientId(c), seq, identity, 0, 0, gw.getWindowInfo()));
        }
    }

    private long nextSeq(SocketIOClient client) {
        long result = 0;
        result = currentPaintRequestSeq;
        currentPaintRequestSeq = result + 1;
        return result;
    }

    public void copyAreaOnWeb(GraphicsWrapper gw, int x, int y, int width, int height, int dx, int dy) {
        long seq = nextSeq(client);
        client.sendJsonObject(new JsonCopyAreaRequest(gw.getWebWindow().getClientId(), seq, x, y, dx, dy, width, height, gw.getWindowInfo()));

    }

    public void disposeWindow(Window webWindow) {
        windows.remove(webWindow);
    }

    public void registerWindow(Window webWindow) {
        windows.put(Util.getObjectIdentity(webWindow), webWindow);
    }

    public void dispatchEvent(JsonEventMouse event) {
        AWTEvent e = null;
        Window w = windows.get(event.windowId);
        WebWindow ww = (WebWindow) w;
        int x = event.x + ww.getFrameTranslation().x;
        int y = event.y + ww.getFrameTranslation().y;
        long when = System.currentTimeMillis();
        int modifiers = Util.getMouseModifiersAWTFlag(event.button);
        int id = 0;
        int buttons = Util.getMouseButtonsAWTFlag(event.button);
        switch (event.type) {
            case mousemove:
                id = event.button == 1 ? MouseEvent.MOUSE_DRAGGED : MouseEvent.MOUSE_MOVED;
                break;
            case mouseup:
                id = MouseEvent.MOUSE_RELEASED;
                break;
            case mousedown:
                id = MouseEvent.MOUSE_PRESSED;
                break;
            default:
                break;
        }
        e = new MouseEvent(w, id, when, modifiers, x, y, 0, false, buttons);
        windows.get(event.windowId).dispatchEvent(e);
    }

    public static PaintManager getInstance(String clientId) {
        if (clientId != null) {
            return instances.get(clientId);
        }
        try {
            throw new Exception("no clientID");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clientConnected(SocketIOClient client, JsonConnectionHandshake handshake) {
        if (!instances.containsKey(handshake.clientId)) {
            instances.put(handshake.clientId, new PaintManager(handshake.clientId, client));
            try {
                SwingClassloader cl = new SwingClassloader(handshake.clientId);
                Class<?> clazz = cl.loadClass("com.sun.swingset3.SwingSet3");
                //Class<?> clazz = cl.loadClass("sk.viktor.Ceiling");
                // Get a class representing the type of the main method's argument
                Class mainArgType[] = { (new String[0]).getClass() };
                String progArgs[] = new String[0];

                // Find the standard main method in the class
                Method main = clazz.getMethod("main", mainArgType);

                // Create a list containing the arguments -- in this case,
                // an array of strings
                Object argsArray[] = { progArgs };

                // Call the method
                main.invoke(null, argsArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            instances.get(handshake.clientId).currentPaintRequestSeq = 0L;
        }
    }
}
