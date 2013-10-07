package org.webswing.debug.tool.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {

    private BufferedImage image=new BufferedImage(1000, 800, BufferedImage.TYPE_4BYTE_ABGR);
    private int x, y;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, x, y, null); // see javadoc for more info on the parameters
        }
    }

    public void drawImage(BufferedImage image, int x, int y) {
        this.image.getGraphics().drawImage(image, x, y, null);
        repaint();
    }

}
