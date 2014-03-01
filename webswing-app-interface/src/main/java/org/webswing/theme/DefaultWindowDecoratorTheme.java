package org.webswing.theme;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.webswing.common.WindowDecoratorThemeIfc;

public class DefaultWindowDecoratorTheme implements WindowDecoratorThemeIfc {

    Insets insets = new Insets(25, 5, 5, 5);
    Color basicColor = new Color(120, 200, 120);
    Color basicBorder = new Color(60, 150, 160);
    Color basicButton = new Color(70, 170, 70);
    Color basicText = new Color(16, 40, 16);

    Image x;
    Image max;
    Image min;

    public DefaultWindowDecoratorTheme() {
        try {
            x = ImageIO.read(this.getClass().getClassLoader().getResource("img/x2.png"));
            min = ImageIO.read(this.getClass().getClassLoader().getResource("img/min2.png"));
            max = ImageIO.read(this.getClass().getClassLoader().getResource("img/max2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Insets getInsets() {
        return (Insets) insets.clone();
    }

    public Image getWindowDecoration(Object window, int w, int h) {
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = image.getGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, w, h);
        g.setColor(basicColor);
        g.fillRect(0, 0, w, insets.top);
        g.fillRect(0, 0, insets.left, h);
        g.fillRect(w - insets.right, 0, insets.right, h);
        g.fillRect(0, h - insets.bottom, w, insets.bottom);
        g.setColor(basicBorder);
        g.drawRect(0, 0, w - 1, h - 1);
        g.setColor(basicText);
        int offsetx = insets.left + 5;
        int offsety = (insets.top / 4) * 3;
        int offsetyIcon = insets.top / 4;
        if (getIcon(window) != null) {
            g.drawImage(getIcon(window), offsetx, offsetyIcon, 16, 16, null);
            offsetx += 21;
        }
        if (getTitle(window) != null) {
            g.drawString(getTitle(window), offsetx, offsety);
        }

        int buttonOffsetx = w - insets.right - 5 - (3 * 33);
        int buttonOffsetY = (insets.top - 16) / 2;
        g.setColor(basicButton);
        if (!(window instanceof Dialog)) {
            g.fillRect(buttonOffsetx, 1, 32, insets.top - 2);
            g.drawImage(this.min, buttonOffsetx + 8, buttonOffsetY, 16, 16, null);
        }
        buttonOffsetx += 33;
        if (!(window instanceof Dialog)) {
            g.fillRect(buttonOffsetx, 1, 32, insets.top - 2);
            g.drawImage(this.max, buttonOffsetx + 8, buttonOffsetY, 16, 16, null);
        }
        buttonOffsetx += 33;
        g.fillRect(buttonOffsetx, 1, 32, insets.top - 2);
        g.drawImage(this.x, buttonOffsetx + 8, buttonOffsetY, 16, 16, null);
        g.dispose();
        return image;
    }

    private static String getTitle(Object o) {
        if (o instanceof Frame) {
            return ((Frame) o).getTitle();
        } else if (o instanceof Dialog) {
            return ((Dialog) o).getTitle();
        } else {
            return null;
        }
    }

    private static Image getIcon(Object o) {
        if (o instanceof Frame) {
            return ((Frame) o).getIconImage();
        } else if (o instanceof Dialog) {
            List<Image> images = ((Dialog) o).getIconImages();
            if (images.size() > 0) {
                return images.get(0);
            }
        }
        return null;

    }

}
