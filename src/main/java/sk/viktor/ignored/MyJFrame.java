package sk.viktor.ignored;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import sk.viktor.GraphicsWrapper;


public class MyJFrame extends JFrame {

    
    private MyImage bufferImage;
    
    private BufferedImage virtualScreen;
    
    @Override
    public Image createImage(int width, int height) {
         if(bufferImage==null){
             bufferImage=new MyImage(this.getWidth(),this.getHeight());
         }
         return bufferImage; 
    }

//    @Override
//    public Graphics getGraphics() {
//        if(virtualScreen==null || virtualScreen.getWidth()!=this.getWidth() ||virtualScreen.getHeight()!=this.getHeight()){
//            virtualScreen=new BufferedImage(this.getWidth(),this.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
//        }
//        return new GraphicsWrapper((Graphics2D) super.getGraphics(),this.getWidth(),this.getHeight(), this);
//    }
    
}
