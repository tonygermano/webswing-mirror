package org.webswing.debug.tool.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.RepaintManager;

public class ImagePanel extends JPanel {

    private BufferedImage image;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters
        }
    }

    public void drawImage(BufferedImage image){
        this.image=image;
        repaint();
   }
    
}
