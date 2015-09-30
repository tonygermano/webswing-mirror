package org.webswing;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.webswing.directdraw.toolkit.WebGraphics;

public class Tests {

	@SuppressWarnings("unused")
	public static boolean t00DrawLineTest(Graphics2D g, int repeat) {
		if (repeat != 0) {
			return false;
		}
		g.setColor(Color.red);
		g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 2.5f, new float[] { 3, 15, 40, 15 }, 10));
		g.drawPolyline(new int[] { 20, 20, 100, 100 }, new int[] { 5, 50, 50, 95 }, 4);
		g.setColor(Color.green);
		g.setStroke(new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.5f, new float[] { 3, 15, 40, 15 }, 50));
		g.drawPolyline(new int[] { 120, 120, 200, 200 }, new int[] { 5, 50, 50, 95 }, 4);
		g.setColor(Color.blue);
		g.setStroke(new BasicStroke(7, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 2.5f, new float[] { 3, 15, 40, 15 }, 100));
		g.drawPolyline(new int[] { 220, 220, 300, 300 }, new int[] { 5, 50, 50, 95 }, 4);

		g.setColor(Color.green);
		g.setStroke(new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.5f));
		g.drawPolyline(new int[] { 320, 320, 340, 340 }, new int[] { 20, 80, 20, 80 }, 4);
		g.setStroke(new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f));
		g.drawPolyline(new int[] { 360, 360, 380, 380 }, new int[] { 20, 80, 20, 80 }, 4);

		g.setStroke(new ZigzagStroke(new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f), 10, 5));
		g.drawLine(420, 10, 420, 90);
		return true;
	}

	public static boolean t01DrawImageTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}
		BufferedImage image = ImageIO.read(Tests.class.getClassLoader().getResource("ws.png"));
		g.drawImage(image, 10, 10, 180, 80, 25, 25, 100, 100, null);
		g.drawImage(image, 200, 10, 380, 80, 100, 100, 25, 25, null);
		return true;
	}

	public static boolean t02FillRectTest(Graphics2D g, int repeat) {
		if (repeat != 0) {
			return false;
		}
		Color c = Color.ORANGE;
		Color c1 = Color.red;
		Color c2 = Color.green;
		Color c3 = Color.blue;
		Color c4 = Color.yellow;
		g.setColor(c);
		g.draw(new Arc2D.Double(new Rectangle2D.Double(0, 0, 48, 30), 15, 250, Arc2D.OPEN));
		g.draw(new Arc2D.Double(new Rectangle2D.Double(0, 33, 48, 30), 15, 250, Arc2D.CHORD));
		g.draw(new Arc2D.Double(new Rectangle2D.Double(0, 66, 48, 30), 15, 250, Arc2D.PIE));
		g.setColor(c1);
		g.fill(new Arc2D.Double(new Rectangle2D.Double(50, 0, 48, 30), 15, 250, Arc2D.OPEN));
		g.fill(new Arc2D.Double(new Rectangle2D.Double(50, 33, 48, 30), 15, 250, Arc2D.CHORD));
		g.fill(new Arc2D.Double(new Rectangle2D.Double(50, 66, 48, 30), 15, 250, Arc2D.PIE));
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

	public static boolean t03TransformTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}
		BufferedImage image = ImageIO.read(Tests.class.getClassLoader().getResource("ws.png"));
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

	@SuppressWarnings("unused")
	public static boolean t04TexturePaintTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}
		BufferedImage image = ImageIO.read(Tests.class.getClassLoader().getResource("ws.png"));
		g.setPaint(Color.orange);
		g.fillRect(0, 0, 500, 100);
		g.setPaint(new TexturePaint(image, new Rectangle2D.Double(50, 50, 48, 48)));
		g.fill(new RoundRectangle2D.Double(0, 0, 200, 100, 40, 40));
		g.setStroke(new BasicStroke(17, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 2.5f, new float[] { 3, 20, 40, 20 }, 10));
		g.drawPolyline(new int[] { 220, 270, 320, 370, 420, 470 }, new int[] { 5, 95, 5, 95, 5, 95 }, 6);
		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t05LinearGradientTest(Graphics2D g, int repeat) {
		if (repeat != 0) {
			return false;
		}
		g.setPaint(new GradientPaint(new Point2D.Float(0, 0), Color.green, new Point2D.Float(100, 30), Color.blue));
		g.fillRect(0, 0, 100, 100);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, 100, 100);
		float[] fractions = new float[] { 0f, 0.5f, 1f };
		Color[] colors = new Color[] { Color.BLUE, Color.green, Color.yellow };
		g.setPaint(new LinearGradientPaint(new Point2D.Float(200, 100), new Point2D.Float(150, 50), fractions, colors, CycleMethod.REFLECT));
		g.fillRect(100, 0, 100, 100);
		g.setColor(Color.BLACK);
		g.drawRect(100, 0, 100, 100);
		g.setPaint(new LinearGradientPaint(new Point2D.Float(900, 20), new Point2D.Float(900, 0), fractions, colors, CycleMethod.REFLECT));
		g.fillRect(200, 0, 150, 100);
		g.setColor(Color.BLACK);
		g.drawRect(200, 0, 150, 100);
		g.setPaint(new LinearGradientPaint(new Point2D.Float(1050, 0), new Point2D.Float(1000, 0), fractions, colors, CycleMethod.REFLECT));
		g.fillRect(350, 0, 150, 100);
		g.setColor(Color.BLACK);
		g.drawRect(350, 0, 150, 100);
		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t06RadialGradientTest(Graphics2D g, int repeat) {
		if (repeat != 0) {
			return false;
		}
		float[] fractions = new float[] { 0, 0.5f, 1 };
		Color[] colors = new Color[] { Color.white, Color.red, Color.black };
		g.setPaint(new RadialGradientPaint(new Point2D.Float(25, 25), 25, fractions, colors));
		g.fillOval(0, 0, 50, 50);
		g.setPaint(new RadialGradientPaint(new Point2D.Float(25, 75), 25, new Point2D.Float(50, 100), fractions, colors, CycleMethod.NO_CYCLE));
		g.fillOval(0, 50, 50, 50);
		g.setPaint(new RadialGradientPaint(new Point2D.Float(75, 25), 25, new Point2D.Float(85, 35), fractions, colors, CycleMethod.NO_CYCLE));
		g.fillOval(50, 0, 50, 50);
		g.setPaint(new RadialGradientPaint(new Point2D.Float(75, 75), 25, new Point2D.Float(50, 50), fractions, colors, CycleMethod.NO_CYCLE));
		g.fillOval(50, 50, 50, 50);
		g.setPaint(new RadialGradientPaint(new Point2D.Float(150, 50), 25, fractions, colors, CycleMethod.REFLECT));
		g.fillOval(100, 0, 100, 100);
		g.setPaint(new RadialGradientPaint(new Point2D.Float(250, 50), 25, fractions, colors, CycleMethod.REPEAT));
		g.fillOval(200, 0, 100, 100);
		g.setPaint(new RadialGradientPaint(new Point2D.Float(350, 50), 25, new Point2D.Float(340, 35), fractions, colors, CycleMethod.REFLECT));
		g.fillOval(300, 0, 100, 100);
		g.setPaint(new RadialGradientPaint(new Point2D.Float(450, 50), 25, new Point2D.Float(460, 65), fractions, colors, CycleMethod.REPEAT));
		g.fillOval(400, 0, 100, 100);
		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t07ClipTest(Graphics2D g, int repeat) {
		if (repeat != 0) {
			return false;
		}
		g.setColor(Color.orange);
		g.setClip(new Arc2D.Double(new Rectangle2D.Double(0, 0, 98, 48), 15, 250, Arc2D.OPEN));
		g.fillRect(0, 0, 500, 100);
		g.setColor(Color.red);
		g.setClip(new Arc2D.Double(new Rectangle2D.Double(0, 50, 98, 48), 15, 250, Arc2D.PIE));
		g.fillRect(0, 0, 500, 100);

		g.setColor(Color.red);
		g.setClip(new Rectangle2D.Double(100, 0, 98, 98));
		g.fillRect(0, 0, 500, 100);
		g.setColor(Color.green);
		g.clip(new Rectangle2D.Double(100, 20, 98, 48));
		g.fillRect(0, 0, 500, 100);

		g.setClip(new Rectangle2D.Double(200, 0, 98, 98));
		g.fillRect(0, 0, 500, 100);
		g.setColor(Color.red);
		g.clip(new RoundRectangle2D.Double(200, 0, 98, 98, 40, 40));
		g.fillRect(0, 0, 500, 100);
		g.setColor(Color.blue);
		g.clip(new Ellipse2D.Double(200, 0, 248, 98));
		g.fillRect(0, 0, 500, 100);
		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t08StringsTest(Graphics2D vg, int repeat) {
		if (repeat != 0) {
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
		// Font embedded = new Font("Monotype Corsiva", Font.PLAIN, 12);
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

	@SuppressWarnings("unused")
	public static boolean t09JButtonTest(Graphics2D g, int repeat) {
		if (repeat > 0) {
			return false;
		}
		JPanel scrP = new JPanel();
		scrP.add(new JLabel("test"));
		JScrollPane scroll = new JScrollPane(scrP);
		scroll.setPreferredSize(new Dimension(400, 80));
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		printJComponentHelper("javax.swing.plaf.nimbus.NimbusLookAndFeel", g, scroll);
		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t10CopyAreaTest(Graphics2D g, int repeat) {
		if (repeat != 0) {
			return false;
		}
		Color c = Color.ORANGE;
		g.setColor(c);
		g.fill(new Arc2D.Double(new Rectangle2D.Double(0, 0, 48, 30), 15, 250, Arc2D.OPEN));

		g.copyArea(0, 0, 50, 50, 0, 50);

		g.clipRect(50, 0, 25, 25);
		g.copyArea(0, 0, 50, 50, 50, 0);

		g.setClip(0, 0, 500, 100);

		g.translate(25, 25);
		g.copyArea(-25, -25, 50, 50, 100, 0);

		Image i = DrawServlet.getImage(g instanceof WebGraphics);
		Graphics2D gx = (Graphics2D) i.getGraphics();
		gx.setColor(Color.blue);
		gx.fill(new Arc2D.Double(new Rectangle2D.Double(0, 0, 48, 30), 15, 250, Arc2D.OPEN));
		gx.copyArea(10, 10, 50, 50, 0, 50);
		gx.dispose();
		g.translate(150, -25);
		g.drawImage(i, 0, 0, null);
		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t11MultiLevelGraphicsTest(Graphics2D g, int repeat) {
		if (repeat != 0) {
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

	@SuppressWarnings("unused")
	public static boolean t12DrawWebImageTest(Graphics2D g, int repeat) {
		if (repeat != 0) {
			return false;
		}
		Image i = DrawServlet.getImage(g instanceof WebGraphics);
		Graphics2D gx = (Graphics2D) i.getGraphics();
		t02FillRectTest(gx, 0);
		gx.dispose();
		g.setClip(new Arc2D.Double(new Rectangle2D.Double(0, 0, 500, 100), 15, 360, Arc2D.OPEN));
		g.translate(500, 100);
		g.rotate(Math.PI);
		g.drawImage(i, 0, 0, 100, 100, 400, 0, 500, 100, null);
		g.drawImage(i, 100, 0, 200, 100, 300, 0, 400, 100, null);
		g.drawImage(i, 200, 0, 300, 100, 200, 0, 300, 100, null);
		g.drawImage(i, 300, 0, 400, 100, 100, 0, 200, 100, null);
		g.drawImage(i, 400, 0, 500, 100, 0, 0, 100, 100, null);
		g.setColor(new Color(100, 100, 100, 100));
		g.fillRect(0, 0, 500, 100);
		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t13ImageCacheTest(Graphics2D g, int repeat) throws IOException {
		if (repeat > 3) {
			return false;
		}
		switch (repeat) {
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

	@SuppressWarnings("unused")
	public static boolean t14ImageCacheTest(Graphics2D g, int repeat) throws IOException {
		if (repeat > 1) {
			return false;
		}
		BufferedImage image = ImageIO.read(Tests.class.getClassLoader().getResource("ws.png"));

		switch (repeat) {
		case 0:
			g.drawImage(image, 0, 0, null);
			break;
		case 1:
			g.drawImage(image, 200, 0, null);
			break;
		}
		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t15MultipleCopyAreaTestTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}
		BufferedImage image = ImageIO.read(Tests.class.getClassLoader().getResource("ws.png"));
		g.drawImage(image, 0, 0, null);
		g.copyArea(0, 0, 120, 100, 130, 0);
		g.drawImage(image, 0, 30, null);
		g.copyArea(0, 0, 120, 100, 260, 0);
		g.drawImage(image, 0, 60, null);
		g.copyArea(0, 0, 120, 100, 390, 0);
		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t16CompositeModesTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}

		compositeTestOp(g, 0, 0, AlphaComposite.Src);
		compositeTestOp(g, 50, 0, AlphaComposite.SrcOver);
		compositeTestOp(g, 100, 0, AlphaComposite.SrcIn);
		compositeTestOp(g, 150, 0, AlphaComposite.SrcOut);
		compositeTestOp(g, 200, 0, AlphaComposite.SrcAtop);
		compositeTestOp(g, 250, 0, AlphaComposite.Xor);
		
		compositeTestOp(g, 0, 50, AlphaComposite.Dst);
		compositeTestOp(g, 50, 50, AlphaComposite.DstOver);
		compositeTestOp(g, 100, 50, AlphaComposite.DstIn);
		compositeTestOp(g, 150, 50, AlphaComposite.DstOut);
		compositeTestOp(g, 200, 50, AlphaComposite.DstAtop);
		compositeTestOp(g, 250, 50, AlphaComposite.Clear);

		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t17SemitransparentCompositeModesTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}

		compositeTestOp(g, 0, 0, AlphaComposite.SRC, 0.5f);
		compositeTestOp(g, 50, 0, AlphaComposite.SRC_OVER, 0.5f);
		compositeTestOp(g, 100, 0, AlphaComposite.SRC_IN, 0.5f);
		compositeTestOp(g, 150, 0, AlphaComposite.SRC_OUT, 0.5f);
		compositeTestOp(g, 200, 0, AlphaComposite.SRC_ATOP, 0.5f);
		compositeTestOp(g, 250, 0, AlphaComposite.XOR, 0.5f);

		compositeTestOp(g, 0, 50, AlphaComposite.DST, 0.5f);
		compositeTestOp(g, 50, 50, AlphaComposite.DST_OVER, 0.5f);
		compositeTestOp(g, 100, 50, AlphaComposite.DST_IN, 0.5f);
		compositeTestOp(g, 150, 50, AlphaComposite.DST_OUT, 0.5f);
		compositeTestOp(g, 200, 50, AlphaComposite.DST_ATOP, 0.5f);
		compositeTestOp(g, 250, 50, AlphaComposite.CLEAR, 0.5f);

		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t18TransparentCompositeModesTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}

		compositeTestOp(g, 0, 0, AlphaComposite.SRC, 0f);
		compositeTestOp(g, 50, 0, AlphaComposite.SRC_OVER, 0f);
		compositeTestOp(g, 100, 0, AlphaComposite.SRC_IN, 0f);
		compositeTestOp(g, 150, 0, AlphaComposite.SRC_OUT, 0f);
		compositeTestOp(g, 200, 0, AlphaComposite.SRC_ATOP, 0f);
		compositeTestOp(g, 250, 0, AlphaComposite.XOR, 0f);

		compositeTestOp(g, 0, 50, AlphaComposite.DST, 0f);
		compositeTestOp(g, 50, 50, AlphaComposite.DST_OVER, 0f);
		compositeTestOp(g, 100, 50, AlphaComposite.DST_IN, 0f);
		compositeTestOp(g, 150, 50, AlphaComposite.DST_OUT, 0f);
		compositeTestOp(g, 200, 50, AlphaComposite.DST_ATOP, 0f);
		compositeTestOp(g, 250, 50, AlphaComposite.CLEAR, 0f);

		return true;
	}

	private static void compositeTestOp(Graphics2D g, int x, int y, int compositeRule, float alpha) {
		compositeTestOp(g, x, y, AlphaComposite.getInstance(compositeRule, alpha));
	}

	private static void compositeTestOp(Graphics2D g, int x, int y, AlphaComposite c) {
		g.setPaint(new GradientPaint(new Point(x, y), Color.yellow, new Point(x + 50, y + 50), Color.green));
		g.fillRect(x, y, 50, 50);
		
		Image img = DrawServlet.getImage(g instanceof WebGraphics, 50, 50);
		Graphics2D imgGraphics = (Graphics2D) img.getGraphics();
		imgGraphics.setColor(new Color(0, 0, 255));
		imgGraphics.fillRect(0, 0, 25, 25);
		imgGraphics.setComposite(c);
		imgGraphics.setColor(new Color(255, 0, 0));
		imgGraphics.fillOval(10, 10, 25, 25);
		
		g.drawImage(img, x, y, null);
		g.setColor(Color.black);
		g.drawString("" + c.getRule(), x + 30, y + 40);
	}

	@SuppressWarnings("unused")
	public static boolean t19CompositeImagesTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}

		compositeImagesTestOp(g, 0, 0, AlphaComposite.Src);
		compositeImagesTestOp(g, 50, 0, AlphaComposite.SrcOver);
		compositeImagesTestOp(g, 100, 0, AlphaComposite.SrcIn);
		compositeImagesTestOp(g, 150, 0, AlphaComposite.SrcOut);
		compositeImagesTestOp(g, 200, 0, AlphaComposite.SrcAtop);
		compositeImagesTestOp(g, 250, 0, AlphaComposite.Xor);

		compositeImagesTestOp(g, 0, 50, AlphaComposite.Dst);
		compositeImagesTestOp(g, 50, 50, AlphaComposite.DstOver);
		compositeImagesTestOp(g, 100, 50, AlphaComposite.DstIn);
		compositeImagesTestOp(g, 150, 50, AlphaComposite.DstOut);
		compositeImagesTestOp(g, 200, 50, AlphaComposite.DstAtop);
		compositeImagesTestOp(g, 250, 50, AlphaComposite.Clear);

		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t20SemitransparentCompositeImagesTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}

		compositeImagesTestOp(g, 0, 0, AlphaComposite.SRC, 0.5f);
		compositeImagesTestOp(g, 50, 0, AlphaComposite.SRC_OVER, 0.5f);
		compositeImagesTestOp(g, 100, 0, AlphaComposite.SRC_IN, 0.5f);
		compositeImagesTestOp(g, 150, 0, AlphaComposite.SRC_OUT, 0.5f);
		compositeImagesTestOp(g, 200, 0, AlphaComposite.SRC_ATOP, 0.5f);
		compositeImagesTestOp(g, 250, 0, AlphaComposite.XOR, 0.5f);

		compositeImagesTestOp(g, 0, 50, AlphaComposite.DST, 0.5f);
		compositeImagesTestOp(g, 50, 50, AlphaComposite.DST_OVER, 0.5f);
		compositeImagesTestOp(g, 100, 50, AlphaComposite.DST_IN, 0.5f);
		compositeImagesTestOp(g, 150, 50, AlphaComposite.DST_OUT, 0.5f);
		compositeImagesTestOp(g, 200, 50, AlphaComposite.DST_ATOP, 0.5f);
		compositeImagesTestOp(g, 250, 50, AlphaComposite.CLEAR, 0.5f);

		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t21TransparentCompositeImagesTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}

		compositeImagesTestOp(g, 0, 0, AlphaComposite.SRC, 0f);
		compositeImagesTestOp(g, 50, 0, AlphaComposite.SRC_OVER, 0f);
		compositeImagesTestOp(g, 100, 0, AlphaComposite.SRC_IN, 0f);
		compositeImagesTestOp(g, 150, 0, AlphaComposite.SRC_OUT, 0f);
		compositeImagesTestOp(g, 200, 0, AlphaComposite.SRC_ATOP, 0f);
		compositeImagesTestOp(g, 250, 0, AlphaComposite.XOR, 0f);

		compositeImagesTestOp(g, 0, 50, AlphaComposite.DST, 0f);
		compositeImagesTestOp(g, 50, 50, AlphaComposite.DST_OVER, 0f);
		compositeImagesTestOp(g, 100, 50, AlphaComposite.DST_IN, 0f);
		compositeImagesTestOp(g, 150, 50, AlphaComposite.DST_OUT, 0f);
		compositeImagesTestOp(g, 200, 50, AlphaComposite.DST_ATOP, 0f);
		compositeImagesTestOp(g, 250, 50, AlphaComposite.CLEAR, 0f);

		return true;
	}

	private static void compositeImagesTestOp(Graphics2D g, int x, int y, int compositeRule, float alpha) {
		compositeImagesTestOp(g, x, y, AlphaComposite.getInstance(compositeRule, alpha));
	}

	private static void compositeImagesTestOp(Graphics2D g, int x, int y, AlphaComposite c) {
		g.setPaint(new GradientPaint(new Point(x, y), Color.yellow, new Point(x + 50, y + 50), Color.green));
		g.fillRect(x, y, 50, 50);
		
		Image oval = DrawServlet.getImage(g instanceof WebGraphics);
		Graphics2D ovalGraphics = (Graphics2D) oval.getGraphics();
		ovalGraphics.setColor(new Color(255, 0, 0));
		ovalGraphics.fillOval(10, 10, 25, 25);

		Image img = DrawServlet.getImage(g instanceof WebGraphics, 50, 50);
		Graphics2D imgGraphics = (Graphics2D) img.getGraphics();
		imgGraphics.setColor(new Color(0, 0, 255));
		imgGraphics.fillRect(0, 0, 25, 25);
		imgGraphics.setComposite(c);
		imgGraphics.drawImage(oval, 0, 0, null);
		
		g.drawImage(img, x, y, null);
		g.setColor(Color.black);
		g.drawString("" + c.getRule(), x + 30, y + 40);
	}

	@SuppressWarnings("unused")
	public static boolean t22MetalTooltipTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}
		SliderDemo sd = new SliderDemo();
		printJComponentHelper(null, g, sd);
		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t23OverlayedImagesWithTransparencyTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}
		BufferedImage image = ImageIO.read(Tests.class.getClassLoader().getResource("java.png"));
		g.drawImage(image, 0, 0, 100, 100, null);
		g.setColor(Color.yellow);
		g.fill(new Rectangle(50, 0, 100, 100));
		g.drawImage(image, 50, 0, 100, 100, null);

		g.drawImage(image, 200, 0, 100, 100, null);
		g.setColor(new Color(255, 255, 0, 200));
		g.fill(new Rectangle(250, 0, 100, 100));
		g.drawImage(image, 250, 0, 100, 100, null);
		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t24TransparentCompositeOverImageTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}
		BufferedImage image = ImageIO.read(Tests.class.getClassLoader().getResource("ws.png"));
		g.drawImage(image, 0, 0, null);
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g.setColor(Color.yellow);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		
		return true;
	}

	@SuppressWarnings("unused")
	public static boolean t25TransparentFillOverImageTest(Graphics2D g, int repeat) throws IOException {
		if (repeat != 0) {
			return false;
		}
		BufferedImage image = ImageIO.read(Tests.class.getClassLoader().getResource("ws.png"));

		g.setBackground(Color.white);
		g.clearRect(0, 0, image.getWidth() + 100, image.getHeight());
		g.drawImage(image, 50, 0, null);
		g.setColor(new Color(0, 255, 255, 128));
		g.fillRect(0, 0, image.getWidth() + 100, image.getHeight());

		return true;
	}
	
	private static void printJComponentHelper(String laf, Graphics2D g, JComponent c) {
		try {
			UIManager.setLookAndFeel(laf == null ? "javax.swing.plaf.metal.MetalLookAndFeel" : laf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JFrame jf = new JFrame("");
		jf.setSize(500, 100);
		JPanel sd = new JPanel();
		sd.setPreferredSize(new Dimension(500, 100));
		sd.add(c);
		jf.add(sd);
		jf.pack();
		sd.print(g);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("tests");

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel content = new JPanel();
		content.setLayout(new GridBagLayout());
		int y = 0;
		for (String m : DrawServlet.getTestMethods()) {
			Image image = getImage(m, true);
			content.add(new JLabel(new ImageIcon(image)), new GridBagConstraints(0, y, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
			image = getImage(m, false);
			content.add(new JLabel(new ImageIcon(image)), new GridBagConstraints(1, y++, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		}
		frame.getContentPane().add(new JScrollPane(content));
		frame.pack();
		frame.setSize(1280, 700);
		frame.setVisible(true);
	}

	public static Image getImage(String name, boolean web) {
		Image i = DrawServlet.getImage(web);
		Graphics2D g = (Graphics2D) i.getGraphics();
		try {
			Tests.class.getDeclaredMethod(name, Graphics2D.class, int.class).invoke(null, g, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		g.dispose();
		return i;
	}
}
