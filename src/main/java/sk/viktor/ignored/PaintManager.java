package sk.viktor.ignored;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;

import sk.viktor.GraphicsWrapper;

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
        System.out.println(paintStack.size()+ " wrapped G "+ System.identityHashCode(g));
        return result;
    }

    public static void paintToWeb(Graphics g, JComponent c) {
        //resolve drawing sequence
        if (paintStack.contains(c)) {
            if(paintStack.size()>0){
                for(int i =0;i<paintStack.size();i++){
                    doPaint(graphicsStack.get(i), paintStack.get(i));
                }
                graphicsStack.clear();
                paintStack.clear();
            }
            
        }
    }
    
    protected static void doPaint(Graphics g, JComponent c){
      //Image img = RepaintManager.currentManager(c).getVolatileOffscreenBuffer(c, c.getWidth(), c.getHeight());
        System.out.println(c.getClass() + " parent: " + c.getParent().getClass().getCanonicalName());
        if (g instanceof GraphicsWrapper) {
            GraphicsWrapper gw = ((GraphicsWrapper) g);
            BufferedImage img = ((GraphicsWrapper) g).popWebImage();
            //System.out.println(c.getClass() + " graphics x" +transform.getTranslateX()+ " y"+transform.getTranslateY()+ " w"+);
            if (img != null) {
                updateComponentImage("c" + System.identityHashCode(c), img);
                client.sendJsonObject(new JsonPaintRequest("c" + System.identityHashCode(c), (int) gw.getTransform().getTranslateX(), (int) gw.getTransform().getTranslateY()));
            }
        } else {
            System.out.println("graphics is not wrapped!!");
        }
    }
}
