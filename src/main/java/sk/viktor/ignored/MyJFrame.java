package sk.viktor.ignored;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import sk.viktor.GraphicsWrapper;


public class MyJFrame extends JFrame {

   private BufferedImage virtualScreen;
    

    @Override
    public Graphics getGraphics() {
        if(virtualScreen==null || virtualScreen.getWidth()!=this.getWidth() ||virtualScreen.getHeight()!=this.getHeight()){
            virtualScreen=new BufferedImage(this.getWidth(),this.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        }
        System.out.println("MyFrame: getGraphics:"+virtualScreen);
        return new GraphicsWrapper((Graphics2D) super.getGraphics(), this);
    }
    
    public BufferedImage getVirtualScreen() {
        return virtualScreen;
    }

    
    public void setVirtualScreen(BufferedImage virtualScreen) {
        this.virtualScreen = virtualScreen;
    }
}
