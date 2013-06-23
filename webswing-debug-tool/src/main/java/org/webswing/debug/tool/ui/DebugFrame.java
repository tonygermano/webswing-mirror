package org.webswing.debug.tool.ui;

import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;


public class DebugFrame extends JFrame {

    private ImagePanel ip= new ImagePanel();

    public DebugFrame() throws IOException {
        setTitle("debug");
        getContentPane().add(ip);
        setVisible(true);
    }
    
    public void drawImage(BufferedImage image){
        ip.drawImage(image);
        Insets i = getInsets();
        setSize(image.getWidth()+i.left+i.right,image.getHeight()+i.top+i.bottom);
    }
}
