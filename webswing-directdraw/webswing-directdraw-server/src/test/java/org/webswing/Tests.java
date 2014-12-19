package org.webswing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.webswing.directdraw.toolkit.WebGraphics;

public class Tests {

    public static boolean t00DrawLineTest(Graphics2D g,Integer repeat) {
    	if(repeat!=0){
    		return false;
    	}
        Color c1 = Color.red;
        Color c2 = Color.green;
        Color c3 = Color.blue;
        g.setColor(c1);
        g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 2.5f, new float[] { 3, 15, 40, 15 }, 10));
        g.drawPolyline(new int[] { 20, 20, 100, 100 }, new int[] { 5, 50, 50, 95 }, 4);
        g.setColor(c2);
        g.setStroke(new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.5f, new float[] { 3, 15, 40, 15 }, 50));
        g.drawPolyline(new int[] { 120, 120, 200, 200 }, new int[] { 5, 50, 50, 95 }, 4);
        g.setColor(c3);
        g.setStroke(new BasicStroke(7, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 2.5f, new float[] { 3, 15, 40, 15 }, 100));
        g.drawPolyline(new int[] { 220, 220, 300, 300 }, new int[] { 5, 50, 50, 95 }, 4);

        g.setColor(c2);
        g.setStroke(new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.5f));
        g.drawPolyline(new int[] { 320, 320, 340, 340 }, new int[] { 20, 80, 20, 80 }, 4);
        g.setStroke(new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f));
        g.drawPolyline(new int[] { 360, 360, 380, 380 }, new int[] { 20, 80, 20, 80 }, 4);

        g.setStroke(new ZigzagStroke(new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f), 10, 5));
        g.drawLine(420, 10, 420, 90);
        return true;
    }

    public static boolean t01DrawImageTest(Graphics2D g,Integer repeat) throws IOException {
    	if(repeat!=0){
    		return false;
    	}
        BufferedImage image = ImageIO.read(new File(Tests.class.getClassLoader().getResource("ws.png").getFile()));
        g.drawImage(image, 10, 10, 180, 80, 25, 25, 100, 100, null);
        g.drawImage(image, 200, 10, 380, 80, 100, 100, 25, 25, null);
        return true;
    }

    public static boolean t02FillRectTest(Graphics2D g,Integer repeat) {
    	if(repeat!=0){
    		return false;
    	}
        Color c = Color.ORANGE;
        Color c1 = Color.red;
        Color c2 = Color.green;
        Color c3 = Color.blue;
        Color c4 = Color.yellow;
        g.setColor(c);
        g.draw(new Arc2D.Double(new Rectangle2D.Double(0, 0, 48, 30), 15, 250, 0));
        g.draw(new Arc2D.Double(new Rectangle2D.Double(0, 33, 48, 30), 15, 250, 1));
        g.draw(new Arc2D.Double(new Rectangle2D.Double(0, 66, 48, 30), 15, 250, 2));
        g.setColor(c1);
        g.fill(new Arc2D.Double(new Rectangle2D.Double(50, 0, 48, 30), 15, 250, 0));
        g.fill(new Arc2D.Double(new Rectangle2D.Double(50, 33, 48, 30), 15, 250, 1));
        g.fill(new Arc2D.Double(new Rectangle2D.Double(50, 66, 48, 30), 15, 250, 2));
        g.setColor(c2);
        g.draw(new Rectangle2D.Double(100, 0, 48, 48));
        g.fill(new Rectangle2D.Double(100, 50, 48, 48));
        g.setColor(c3);
        g.draw(new RoundRectangle2D.Double(150, 0, 48, 48, 20, 20));
        g.fill(new RoundRectangle2D.Double(150, 50, 48, 48, 20, 20));
        g.setColor(c4);
        g.draw(new Ellipse2D.Double(200, 0, 148, 48));
        g.fill(new Ellipse2D.Double(200, 50, 148, 48));
        return true;
    }

    public static boolean t03TransformTest(Graphics2D g,Integer repeat) throws IOException {
    	if(repeat!=0){
    		return false;
    	}
        BufferedImage image = ImageIO.read(new File(Tests.class.getClassLoader().getResource("ws.png").getFile()));
        g.rotate(Math.PI / 4, 50, 50);
        g.drawImage(image, 0, 0, 100, 100, null);
        g.rotate(-(Math.PI / 4), 50, 50);
        g.translate(100, 0);
        g.scale(1.5, 1.5);
        g.drawImage(image, 0, 0, 100, 100, null);
        g.scale(0.5, 0.5);
        g.drawImage(image, 0, 0, 100, 100, null);
        g.translate(250, 0);
        g.rotate(-(Math.PI / 4), 50, 100);
        g.drawImage(image, 0, 0, 100, 100, null);
        return true;
    }

    public static boolean t04TexturePaintTest(Graphics2D g,Integer repeat) throws IOException {
    	if(repeat!=0){
    		return false;
    	}
        BufferedImage image = ImageIO.read(new File(Tests.class.getClassLoader().getResource("ws.png").getFile()));
        g.setPaint(Color.orange);
        g.fillRect(0, 0, 500, 100);
        g.setPaint(new TexturePaint(image, new Rectangle2D.Double(50, 50, 48, 48)));
        g.fill(new RoundRectangle2D.Double(0, 0, 200, 100, 40, 40));
        g.setStroke(new BasicStroke(17, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 2.5f, new float[] { 3, 20, 40, 20 }, 10));
        g.drawPolyline(new int[] { 220, 270, 320, 370, 420, 470 }, new int[] { 5, 95, 5, 95, 5, 95 }, 6);
        return true;
    }

    public static boolean t05LinearGradientTest(Graphics2D g,Integer repeat) {
    	if(repeat!=0){
    		return false;
    	}
        g.setPaint(new GradientPaint(new Point2D.Float(0, 0), Color.green, new Point2D.Float(100, 30), Color.blue));
        g.fillRect(0, 0, 100, 100);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 100, 100);
        g.setPaint(new LinearGradientPaint(new Point2D.Float(200, 100), new Point2D.Float(150, 50), new float[] { 0f, 0.5f, 1f }, new Color[] { Color.BLUE, Color.green, Color.yellow }, CycleMethod.REFLECT));
        g.fillRect(100, 0, 100, 100);
        g.setColor(Color.BLACK);
        g.drawRect(100, 0, 100, 100);
        g.setPaint(new LinearGradientPaint(new Point2D.Float(900, 20), new Point2D.Float(900, 0), new float[] { 0f, 0.5f, 1f }, new Color[] { Color.BLUE, Color.green, Color.yellow }, CycleMethod.REFLECT));
        g.fillRect(200, 0, 150, 100);
        g.setColor(Color.BLACK);
        g.drawRect(200, 0, 150, 100);
        g.setPaint(new LinearGradientPaint(new Point2D.Float(1050, 0), new Point2D.Float(1000, 0), new float[] { 0f, 0.5f, 1f }, new Color[] { Color.BLUE, Color.green, Color.yellow }, CycleMethod.REFLECT));
        g.fillRect(350, 0, 150, 100);
        g.setColor(Color.BLACK);
        g.drawRect(350, 0, 150, 100);
        return true;
    }

    public static boolean t06RadialGradientTest(Graphics2D g,Integer repeat) {
    	if(repeat!=0){
    		return false;
    	}
        g.setPaint(new RadialGradientPaint(new Point2D.Float(25, 25), 25, new float[] { 0, 0.5f, 1 }, new Color[] { Color.white, Color.red, Color.black }));
        g.fillOval(0, 0, 50, 50);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(25, 75), 25, new Point2D.Float(50, 100), new float[] { 0, 0.5f, 1 }, new Color[] { Color.white, Color.red, Color.black }, CycleMethod.NO_CYCLE));
        g.fillOval(0, 50, 50, 50);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(75, 25), 25, new Point2D.Float(85, 35), new float[] { 0, 0.5f, 1 }, new Color[] { Color.white, Color.red, Color.black }, CycleMethod.NO_CYCLE));
        g.fillOval(50, 0, 50, 50);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(75, 75), 25, new Point2D.Float(50, 50), new float[] { 0, 0.5f, 1 }, new Color[] { Color.white, Color.red, Color.black }, CycleMethod.NO_CYCLE));
        g.fillOval(50, 50, 50, 50);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(150, 50), 25, new float[] { 0, 0.5f, 1 }, new Color[] { Color.white, Color.red, Color.black }, CycleMethod.REFLECT));
        g.fillOval(100, 0, 100, 100);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(250, 50), 25, new float[] { 0, 0.5f, 1 }, new Color[] { Color.white, Color.red, Color.black }, CycleMethod.REPEAT));
        g.fillOval(200, 0, 100, 100);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(350, 50), 25, new Point2D.Float(340, 35), new float[] { 0, 0.5f, 1 }, new Color[] { Color.white, Color.red, Color.black }, CycleMethod.REFLECT));
        g.fillOval(300, 0, 100, 100);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(450, 50), 25, new Point2D.Float(460, 65), new float[] { 0, 0.5f, 1 }, new Color[] { Color.white, Color.red, Color.black }, CycleMethod.REPEAT));
        g.fillOval(400, 0, 100, 100);
        return true;
    }

    public static boolean t07ClipTest(Graphics2D g,Integer repeat) {
    	if(repeat!=0){
    		return false;
    	}
        Color c = Color.orange;
        Color c1 = Color.red;
        Color c2 = Color.green;
        Color c3 = Color.blue;
        g.setColor(c);
        g.setClip(new Arc2D.Double(new Rectangle2D.Double(0, 0, 98, 48), 15, 250, 0));
        g.fillRect(0, 0, 500, 100);
        g.setColor(c1);
        g.setClip(new Arc2D.Double(new Rectangle2D.Double(0, 50, 98, 48), 15, 250, 2));
        g.fillRect(0, 0, 500, 100);

        g.setColor(c1);
        g.setClip(new Rectangle2D.Double(100, 0, 98, 98));
        g.fillRect(0, 0, 500, 100);
        g.setColor(c2);
        g.clip(new Rectangle2D.Double(100, 20, 98, 48));
        g.fillRect(0, 0, 500, 100);

        g.setClip(new Rectangle2D.Double(200, 0, 98, 98));
        g.fillRect(0, 0, 500, 100);
        g.setColor(c1);
        g.clip(new RoundRectangle2D.Double(200, 0, 98, 98, 40, 40));
        g.fillRect(0, 0, 500, 100);
        g.setColor(c3);
        g.clip(new Ellipse2D.Double(200, 0, 248, 98));
        g.fillRect(0, 0, 500, 100);
        return true;
    }

    public static boolean t08StringsTest(Graphics2D vg,Integer repeat) {
    	if(repeat!=0){
    		return false;
    	}
        vg.setColor(Color.white);
        vg.fillRect(0, 0, 500, 100);

        vg.setColor(Color.black);
        Font serif = new Font("Serif", Font.PLAIN, 12);
        Font sansserif = new Font("SansSerif", Font.PLAIN, 12);
        Font dialog = new Font("Dialog", Font.PLAIN, 12);
        Font monospaced = new Font("Monospaced", Font.PLAIN, 12);

        // Font standard = new Font("TimesRoman", Font.PLAIN, 12);
        // Font standard = new Font("Impact", Font.PLAIN, 12);
        Font standard = serif;

        // Font standard = new Font("Lucida Times Unicode", Font.PLAIN, 12);
        // Font embedded = new Font("ZapfChancery", Font.PLAIN, 12);
        // Font embedded = new Font("Impact", Font.PLAIN, 12);
        Font embedded = new Font("Arial", Font.PLAIN, 12);
        // Font standard = new Font("Lucida Sans", Font.PLAIN, 12);
        //Font embedded = new Font("Monotype Corsiva", Font.PLAIN, 12);
        // Font embedded = new Font("Lucida Sans", Font.PLAIN, 12);
        // Font embedded = new Font("ZapfDingbats", Font.PLAIN, 12);

        String test = "test";
        vg.setFont(standard);
        vg.drawString(test, 10, 10);

        vg.setFont(embedded);
        vg.drawString(test, 10, 25);

        vg.setFont(new Font("Symbol", Font.PLAIN, 12));
        vg.drawString("ABC abc 123 .,!", 10, 40);

        vg.setFont(new Font("ZapfDingbats", Font.PLAIN, 12));
        vg.drawString("ABC abc 123 .,!", 10, 55);

        String ucs = "\u03b1 \u03b2 \u03b3 \u263a \u2665 \u2729 \u270c";
        vg.setFont(serif);
        vg.drawString(ucs, 10, 70);
        vg.setFont(sansserif);
        vg.drawString(ucs, 10, 85);
        vg.setFont(dialog);
        vg.drawString(ucs, 150, 70);
        vg.setFont(monospaced);
        vg.drawString(ucs, 150, 85);

        String text = "Webswing";

        Font font = vg.getFont();
        double fw = 200 / 120.0;
        double fh = 100 / 120.0;
        for (int i = 1; i < 36; i++) {
            AffineTransform t = AffineTransform.getRotateInstance(Math.toRadians(10 * i));
            double s = 1.0 + i / 20.0;
            t.scale(fw / s, fh / s);
            vg.setFont(font.deriveFont(t));
            vg.drawString(text, 300, 40);
        }

        vg.setColor(Color.BLUE);
        vg.setFont(font.deriveFont(AffineTransform.getScaleInstance(fw, fh)));
        vg.drawString(text, 400, 50);
        return true;
    }

    public static boolean t09JButtonTest(Graphics2D g,Integer repeat) {
    	if(repeat!=0){
    		return false;
    	}
        JPanel p = new JPanel();
        JButton b = new JButton("test");
        p.add(b);
        p.setSize(200, 100);
        p.layout();
        b.paint(g);
        return true;
    }

    public static boolean t10CopyAreaTest(Graphics2D g,Integer repeat) {
    	if(repeat!=0){
    		return false;
    	}
        Color c = Color.ORANGE;
        g.setColor(c);
        g.fill(new Arc2D.Double(new Rectangle2D.Double(0, 0, 48, 30), 15, 250, 0));

        g.copyArea(0, 0, 50, 50, 0, 50);

        g.clipRect(50, 0, 25, 25);

        g.copyArea(0, 0, 50, 50, 50, 0);

        g.setClip(0, 0, 500, 100);

        g.copyArea(0, 0, 50, 50, 100, 0);
        return true;
    }

    public static boolean t11MultiLevelGraphicsTest(Graphics2D g,Integer repeat) {
    	if(repeat!=0){
    		return false;
    	}
        g.setColor(Color.black);
        g.fillArc(100, 0, 50, 50, 0, 360);

        Graphics g2 = g.create(250, 50, 250, 50);
        g2.setColor(Color.red);
        g2.fillArc(0, 0, 50, 50, 0, 360);

        g.setColor(Color.orange);
        g.translate(50, -15);
        g.rotate(Math.PI / 4);
        g.scale(2, 2);
        g.fillRect(0, 0, 50, 50);

        g2.setColor(Color.blue);
        g2.translate(10, 10);
        g2.fillRect(0, 0, 20, 20);

        g.setColor(Color.yellow);
        g.translate(25, 25);
        g.scale(0.4, 0.4);
        g.rotate(Math.PI / 8);
        g.translate(10, 10);
        g.fillRect(0, 0, 20, 20);

        g2.dispose();

        g.setColor(Color.red);
        g.fillRect(10, 10, 10, 10);
        return true;
    }

    public static boolean t12ImageDrawImageTest(Graphics2D g,Integer repeat) {
    	if(repeat!=0){
    		return false;
    	}
        Image i = DrawServlet.getImage(g instanceof WebGraphics);
        Graphics2D gx = (Graphics2D) i.getGraphics();
        t02FillRectTest(gx,0);
        gx.dispose();
        g.setClip(new Arc2D.Double(new Rectangle2D.Double(0, 0, 500, 100), 15, 360, 0));
        g.translate(500, 100);
        g.rotate(Math.PI);
        g.drawImage(i, 0, 0, null);
        return true;
    }

    public static boolean t13ImageCacheTest(Graphics2D g,Integer repeat) throws IOException {
    	if(repeat>3){
    		return false;
    	}
    	switch(repeat){
    	case 0:
    		t01DrawImageTest(g, 0);
    		break;
    	case 1:
    		t03TransformTest(g, 0);
    		break;
    	case 2:
    		t01DrawImageTest(g, 0);
    		break;
    	case 3:
    		t03TransformTest(g, 0);
    		break;
    	}
        return true;
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("tests");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel content = new JPanel();
        content.setLayout(new FlowLayout(FlowLayout.LEFT));
        frame.getContentPane().add(content);
        for (String m : DrawServlet.getTestMethods()) {
            JPanel panel = getPanel(m);
            panel.setPreferredSize(new Dimension(500, 100));
            content.add(panel);
        }
        frame.pack();
        frame.setSize(1280, 700);
        frame.setVisible(true);
    }

    public static JPanel getPanel(final String name) {
        JPanel panel = new JPanel() {

            public void paint(java.awt.Graphics g) {
                try {
                    Tests.class.getDeclaredMethod(name, Graphics2D.class).invoke(null, g);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
        return panel;
    }
}
