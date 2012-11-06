package sk.viktor.ignored;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;

import sk.viktor.ignored.event.model.JsonEvent;
import sk.viktor.ignored.event.model.JsonEventMouse;
import sk.viktor.ignored.model.JsonCopyAreaRequest;
import sk.viktor.ignored.model.JsonPaintRequest;

import com.corundumstudio.socketio.SocketIOClient;

public class PaintManager {

    public static SocketIOClient client;
    private static Map<String, Window> windows=new HashMap<String, Window>();
    public static Map<String, Long> currentPaintRequestSeq = new HashMap<String, Long>();
    public static Map<String, ChannelBuffer> bufferMap = new HashMap<String, ChannelBuffer>();

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

    public static Graphics beforePaintInterceptor(Graphics g, JComponent c) {
        GraphicsWrapper result;
        if (g instanceof GraphicsWrapper) {
            result = (GraphicsWrapper) g;
            if (result.getRootPaintComponent() == null) {
                result.setRootPaintComponent(c);
            }
        } else {
            if(!isPaintDoubleBufferedPainting()){
                System.out.println("something s wrong "+ g);
            }
            return g;
        }
        return result;
    }

    public static void afterPaintInterceptor(Graphics g, JComponent c) {
        if (g instanceof GraphicsWrapper) {
            GraphicsWrapper gw = (GraphicsWrapper) g;
            if (getObjectIdentity(gw.getRootPaintComponent()).equals(getObjectIdentity(c))) {
                doPaint(gw, c);
            }
        }else{
            if(!isPaintDoubleBufferedPainting()){
                System.out.println("something s wrong "+ g);
            }
        }
    }

    public static void doPaint(GraphicsWrapper gw, JComponent c) {
        BufferedImage img = gw.getImg();
        if (img != null) {
            long seq = nextSeq(client);
            String identity = "" + seq;
            updateComponentImage(identity, img);
            client.sendJsonObject(new JsonPaintRequest(seq, identity, 0, 0, gw.getWindowInfo()));
        }
    }

    public static boolean isPaintImmediately() {
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
            if (e.getClassName().equals("javax.swing.RepaintManager$PaintManager") && e.getMethodName().equals("paintDoubleBuffered")) {
                return true;
            }
        }
        return false;
    }

    public static String getObjectIdentity(Object c) {
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

    public static void copyAreaOnWeb(GraphicsWrapper gw,int x, int y, int width, int height, int dx, int dy) {
        long seq = nextSeq(client);
        client.sendJsonObject(new JsonCopyAreaRequest(seq, x, y, dx, dy, width, height,gw.getWindowInfo()));
        
    }

    public static void disposeWindow(Window webWindow) {
        windows.remove(webWindow);
    }

    public static void registerWindow(Window webWindow) {
        windows.put(getObjectIdentity(webWindow),webWindow);
    }
    
    public static void dispatchEvent(JsonEvent event){
        AWTEvent e = null;
        Window w=windows.get(event.getWindowId());
        if(event instanceof JsonEventMouse){
            WebWindow ww= (WebWindow) w;
            int x= ((JsonEventMouse) event).getX()+ww.getFrameTranslation().x;
            int y= ((JsonEventMouse) event).getY()+ww.getFrameTranslation().y;
            long when = System.currentTimeMillis();
            int modifiers= 0;
            int id = 0;
            int buttons = 0;
            switch (((JsonEventMouse) event).getType()) {
                case mousemove:
                    id= MouseEvent.MOUSE_MOVED;
                    buttons= MouseEvent.NOBUTTON;
                    break;
                case mouseup:
                    modifiers=MouseEvent.BUTTON1_DOWN_MASK;
                    id= MouseEvent.MOUSE_RELEASED;
                    buttons= MouseEvent.BUTTON1;
                    break;
                case mousedown:
                    modifiers=MouseEvent.BUTTON1_DOWN_MASK;
                    id= MouseEvent.MOUSE_PRESSED;
                    buttons= MouseEvent.BUTTON1;
                    break;
                case mousedrag:
                    modifiers=MouseEvent.BUTTON1_DOWN_MASK;
                    id= MouseEvent.MOUSE_DRAGGED;
                    buttons= MouseEvent.BUTTON1;
                    break;
                default:
                    break;
            }
            e= new MouseEvent(w, id, when, modifiers, x, y, 0, false, buttons);
        }
        windows.get(event.getWindowId()).dispatchEvent(e);
    }
}
