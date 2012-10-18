package sk.viktor.ignored;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;

import sk.viktor.GraphicsWrapper;
import sun.java2d.SunGraphics2D;

import com.corundumstudio.socketio.SocketIOClient;

public class PaintManager {

    public static SocketIOClient client;
    public static Map<String, Long> currentPaintRequestSeq = new HashMap<String, Long>();
    public static Map<String, ChannelBuffer> bufferMap = new HashMap<String, ChannelBuffer>();


    public static void updateComponentImage(String componentId, BufferedImage imageContent) {
        try {
            synchronized (bufferMap) {
                if (!bufferMap.containsKey(componentId)) {
                    bufferMap.put(componentId, ChannelBuffers.dynamicBuffer());
                }
            }
            System.out.println("printing image :"+getObjectIdentity(imageContent));
            ChannelBuffer buffer = bufferMap.get(componentId);
            OutputStream os = new ChannelBufferOutputStream(buffer);
            ImageIO.write(imageContent, "png", ImageIO.createImageOutputStream(os));
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Graphics beforePaintInterceptor(Graphics g, JComponent c) {
        Graphics result;
        if (g instanceof Graphics2D) {
            if (g instanceof GraphicsWrapper) {
                result = g;
            } else {
                result = new GraphicsWrapper((Graphics2D) g, getObjectIdentity(c));
            }
        } else {
            try {
                throw new Exception("this is not a Graphics2d instance- should not happend");
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = g;
        }
        return result;
    }

    public static void afterPaintInterceptor(Graphics g, JComponent c) {
        if (g instanceof GraphicsWrapper) {
            GraphicsWrapper gw = (GraphicsWrapper) g;
            if (gw.getComponentId().equals(getObjectIdentity(c))) {
                if (isPaintImmediately()) {
                    doPaintImmediate(gw,c);
                } else  {
                    doPaint(gw, c);
                }
            }
        } else {
            try {
                throw new Exception("afterPaintInterceptor: g is not wrapped. something is wrong");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void directPaintToWeb(BufferedImage img, int x, int y) {
        System.out.println("DIRECT PAINT -------");
        if (img != null) {
            long seq=nextSeq(client);
            String identity= getObjectIdentity(img)+seq;
            updateComponentImage(identity, img);
            client.sendJsonObject(new JsonPaintRequest(seq, identity, x, y));
        }
        System.out.println("DIRECT PAINT ----END---");
    }

    private static void doPaintImmediate(GraphicsWrapper gw,JComponent c) {
        Point p = getLocation(c);
        long seq=nextSeq(client);
        String identity= getObjectIdentity(c)+seq;
        updateComponentImage(identity, gw.getImg());
        client.sendJsonObject(new JsonPaintRequest(seq, identity, (int) p.x, (int) p.y));
    }

    protected static void doPaint(GraphicsWrapper gw, JComponent c) {
        BufferedImage img = gw.getImg();
        if (img != null) {
            long seq=nextSeq(client);
            String identity= getObjectIdentity(c)+seq;
            updateComponentImage(identity, img);
            client.sendJsonObject(new JsonPaintRequest(seq, identity, (int) gw.getTransform().getTranslateX(), (int) gw.getTransform().getTranslateY()));
        }
    }

    private static Point getLocation(JComponent c) {
        Point result = new Point();
        JComponent rootJ = c;
        int xOffset = 0, yOffset = 0;
        while (rootJ != null && rootJ.getParent() instanceof JComponent) {
            if (rootJ instanceof JComponent) {
                xOffset += rootJ.getX();
                yOffset += rootJ.getY();
                rootJ = (JComponent) rootJ.getParent();
            }
        }
        result.setLocation(xOffset, yOffset);
        return result;
    }

    private static boolean isPaintImmediately() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : trace) {
            if (e.getClassName().equals("javax.swing.JComponent") && e.getMethodName().equals("paintImmediately")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isForceDoubleBufferedPainting() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : trace) {
            if (e.getClassName().equals("javax.swing.JComponent") && e.getMethodName().equals("paintForceDoubleBuffered")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPaintDoubleBufferedPainting() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : trace) {
            if (e.getClassName().equals(" javax.swing.RepaintManager.PaintManager") && e.getMethodName().equals("paintDoubleBuffered")) {
                return true;
            }
        }
        return false;
    }

    private static String getObjectIdentity(Object c) {
        return "c" + System.identityHashCode(c);
    }

    private static long nextSeq(SocketIOClient client) {
        String id = client.getSessionId().toString();
        long result = 0;
        if (currentPaintRequestSeq.containsKey(id)) {
            result = currentPaintRequestSeq.get(id);
            currentPaintRequestSeq.put(id, result + 1);
        } else {
            currentPaintRequestSeq.put(id, 1L);
        }
        return result;
    }

}
