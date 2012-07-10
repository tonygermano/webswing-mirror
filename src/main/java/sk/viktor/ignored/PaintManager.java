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
    public static Map<String, ChannelBuffer> bufferMap = new HashMap<String, ChannelBuffer>();

    private static List<JComponent> paintStack = new ArrayList<JComponent>();
    private static List<Graphics> graphicsStack = new ArrayList<Graphics>();

    public static void updateComponentImage(String componentId, BufferedImage imageContent) {
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

    public static Graphics wrapGraphics(Graphics g, JComponent c) {
        Graphics result;
        if (g instanceof Graphics2D) {
            if (g instanceof GraphicsWrapper) {
                ((GraphicsWrapper) g).pushWebGraphics();
                result = g;
            } else {
                result = new GraphicsWrapper((Graphics2D) g);
            }
        } else {
            System.out.println("this is not a Graphics2d instance- should not happend");
            result = g;
        }
        paintStack.add(c);
        graphicsStack.add(result);
        System.out.println(paintStack.size() + " wrapped G " + c.getClass().getCanonicalName()+ "clip:" +g.getClip());
        return result;
    }

    public static void paintToWeb(Graphics g, JComponent c) {
        //resolve drawing sequence
        if (paintStack.contains(c)) {
            if (isPaintImmediately()) {
                doPaintImmediate(c);
                graphicsStack.clear();
                paintStack.clear();
            } else {
                for (int i = 0; i < paintStack.size(); i++) {
                    doPaint(graphicsStack.get(i), paintStack.get(i));
                }
                graphicsStack.clear();
                paintStack.clear();
            }
        }
    }

    private static void doPaintImmediate(JComponent c) {
        BufferedImage result=new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        System.out.println("result size "+c.getWidth()+"/"+c.getHeight()+" of component "+c.getClass().getCanonicalName());
        Graphics2D resultGraphics = (Graphics2D) result.getGraphics();
        for (int i = 0; i <graphicsStack.size(); i++) {
            GraphicsWrapper gtw = (GraphicsWrapper)graphicsStack.get(i);
            BufferedImage imageToWrite = gtw.peekWebImage();
            resultGraphics.drawImage(imageToWrite, 0, 0, null);
        }
        updateComponentImage(getComponentIdentity(c), result);
        Point p = getLocation(c);
        client.sendJsonObject(new JsonPaintRequest(getComponentIdentity(c), (int) p.x, (int) p.y));
    }

    protected static void doPaint(Graphics g, JComponent c) {
        if (g instanceof GraphicsWrapper) {
            GraphicsWrapper gw = ((GraphicsWrapper) g);
            BufferedImage img = ((GraphicsWrapper) g).popWebImage();
            if (img != null) {
                updateComponentImage(getComponentIdentity(c), img);
                client.sendJsonObject(new JsonPaintRequest(getComponentIdentity(c), (int) gw.getTransform().getTranslateX(), (int) gw.getTransform().getTranslateY()));
            }
        } else {
            System.out.println("graphics is not wrapped!!");
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
    
    private static String getComponentIdentity(JComponent c){
        return "c" + System.identityHashCode(c);
    }

}
