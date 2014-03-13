package org.webswing.theme;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.webswing.common.WindowActionType;
import org.webswing.common.WindowDecoratorThemeIfc;

public class DefaultWindowDecoratorTheme implements WindowDecoratorThemeIfc {

    Insets insets = new Insets(25, 5, 5, 5);
    Color basicColor = new Color(120, 200, 120);
    Color basicBorder = new Color(60, 150, 160);
    Color basicButton = new Color(70, 170, 70);
    Color basicText = new Color(16, 40, 16);
    Color backgroundColor = new Color(255, 255, 255);
    int buttonWidth = 32;
    int buttonMargin = 1;
    int headerMargin = 5;
    BufferedImage backgroundImage;
    Image x;
    Image max;
    Image min;

    public DefaultWindowDecoratorTheme() {
        try {
            x = ImageIO.read(DefaultWindowDecoratorTheme.class.getClassLoader().getResource("img/x2.png"));
            min = ImageIO.read(DefaultWindowDecoratorTheme.class.getClassLoader().getResource("img/min2.png"));
            max = ImageIO.read(DefaultWindowDecoratorTheme.class.getClass().getClassLoader().getResource("img/max2.png"));
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
        int offsetx = insets.left + headerMargin;
        int offsety = (insets.top / 4) * 3;
        int offsetyIcon = insets.top / 4;
        if (getIcon(window) != null) {
            g.drawImage(getIcon(window), offsetx, offsetyIcon, 16, 16, null);
            offsetx += 21;
        }
        if (getTitle(window) != null) {
            g.drawString(getTitle(window), offsetx, offsety);
        }

        int buttonOffsetx = w - insets.right - headerMargin - (3 * (buttonWidth + buttonMargin));
        int buttonOffsetY = (insets.top - 16) / 2;
        g.setColor(basicButton);
        if (!(window instanceof Dialog)) {
            g.fillRect(buttonOffsetx, 1, buttonWidth, insets.top - 2);
            g.drawImage(this.min, buttonOffsetx + (buttonWidth / 4), buttonOffsetY, buttonWidth / 2, buttonWidth / 2, null);
        }
        buttonOffsetx += (buttonWidth + buttonMargin);
        if (!(window instanceof Dialog)) {
            g.fillRect(buttonOffsetx, 1, buttonWidth, insets.top - 2);
            g.drawImage(this.max, buttonOffsetx + (buttonWidth / 4), buttonOffsetY, buttonWidth / 2, buttonWidth / 2, null);
        }
        buttonOffsetx += (buttonWidth + buttonMargin);
        g.fillRect(buttonOffsetx, 1, buttonWidth, insets.top - 2);
        g.drawImage(this.x, buttonOffsetx + (buttonWidth / 4), buttonOffsetY, buttonWidth / 2, buttonWidth / 2, null);
        g.dispose();
        return image;
    }

    public Image repaintBackground(Rectangle sub) {
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        if (backgroundImage == null || screenHeight != backgroundImage.getHeight() || screenWidth != backgroundImage.getWidth()) {
            backgroundImage = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics g = backgroundImage.getGraphics();
            g.setColor(backgroundColor);
            g.fillRect(0, 0, screenWidth, screenHeight);
            g.dispose();
        }
        BufferedImage result = new BufferedImage(sub.width, sub.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = result.getGraphics();
        g.drawImage(backgroundImage, 0, 0, sub.width, sub.height, sub.x, sub.y, sub.x + sub.width, sub.y + sub.height, null);
        g.dispose();
        return result;
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

    public WindowActionType getAction(Window w, MouseEvent e) {
        Rectangle eventPoint = new Rectangle(e.getX(), e.getY(), 0, 0);
        Insets i = w.getInsets();
        int buttonsoffsetx = w.getWidth() - i.right - headerMargin - (3 * (buttonWidth + buttonMargin));
        int buttonsWidth = 3 * (buttonWidth + buttonMargin);
        int buttonsHeigth = i.top;
        Rectangle buttonsArea = new Rectangle(buttonsoffsetx, 0, buttonsWidth, buttonsHeigth);
        if (SwingUtilities.isRectangleContainingRectangle(buttonsArea, eventPoint)) {
            //buttons
            int minOffsetx = w.getWidth() - i.right - headerMargin - (3 * (buttonWidth + buttonMargin));
            int maxOffsetx = w.getWidth() - i.right - headerMargin - (2 * (buttonWidth + buttonMargin));
            int closeOffsetx = w.getWidth() - i.right - headerMargin - (1 * (buttonWidth + buttonMargin));

            if(e.getX()>minOffsetx && e.getX()<minOffsetx+buttonWidth){
                return WindowActionType.minimize;
            }
            if(e.getX()>maxOffsetx && e.getX()<maxOffsetx+buttonWidth){
                return WindowActionType.maximize;
            }
            if(e.getX()>closeOffsetx && e.getX()<closeOffsetx+buttonWidth){
                return WindowActionType.close;
            }
        }

        //resize
        if(e.getX() > (w.getWidth() -10) && e.getY() > (w.getHeight() -10)){
            return WindowActionType.resizeUni;
        }
        if (e.getX() > (w.getWidth() - i.right)) {
            return WindowActionType.resizeRight;
        }
        if (e.getY() > (w.getHeight() - i.bottom)) {
            return WindowActionType.resizeBottom;
        }

        if (e.getY() < i.top) {
            //move
            return WindowActionType.move;
        }
        return WindowActionType.empty;

    }

}
