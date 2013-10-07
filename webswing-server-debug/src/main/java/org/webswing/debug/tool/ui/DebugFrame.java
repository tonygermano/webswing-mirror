package org.webswing.debug.tool.ui;

import java.awt.Insets;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;



public class DebugFrame extends JFrame {

    private ImagePanel ip= new ImagePanel();

    public DebugFrame() throws IOException {
        setTitle("debug");
        getContentPane().add(ip);
        Insets i = getInsets();
        setSize(1000+i.left+i.right,800+i.top+i.bottom);
        setVisible(true);
    }
    
    public Point getImagePanelPosition(){
        return SwingUtilities.convertPoint(ip, new Point(0,0), this);
    }
    
    public void drawImage(BufferedImage image,int x,int y){
        ip.drawImage(image,x,y);

    }
    
    public void drawImage(BufferedImage image){
        drawImage(image,0,0);
    }
}
