package org.webswing.directdraw.util;

import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.webswing.directdraw.model.DrawInstruction;
import org.webswing.directdraw.toolkit.WebImage;

public class RenderUtil {

	public static BufferedImage render(WebImage webImage, BufferedImage imageHolder, Map<DrawInstruction, BufferedImage> partialImageMap, List<WebImage> chunks, List<DrawInstruction> newInstructions, Dimension size) {
		BufferedImage result = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) result.getGraphics();
		if (chunks != null) {
			for (WebImage chunk : chunks) {
				chunk.getSnapshot(result);
			}
		}
		render(result, webImage, imageHolder, partialImageMap, chunks, newInstructions, size);
		g.dispose();
		return result;
	}

	public static BufferedImage render(BufferedImage result, WebImage webImage, BufferedImage imageHolder, Map<DrawInstruction, BufferedImage> partialImageMap, List<WebImage> chunks, List<DrawInstruction> newInstructions, Dimension size) {
		Map<Integer, Graphics2D> gmap = new HashMap<Integer, Graphics2D>();
		Graphics2D currentg = null;
		for (DrawInstruction di : newInstructions) {
			switch (di.getInstruction()) {
				case GRAPHICS_CREATE:
					currentg = iprtGraphicsCreate(result, di, gmap);
					break;
				case GRAPHICS_DISPOSE: {
					gmap.remove(getValue(0, di, Integer.class));
					break;
				}
				case GRAPHICS_SWITCH: {
					currentg = gmap.get(getValue(0, di, Integer.class));
					break;
				}
				case FILL:
					iprtFill(currentg, di);
					break;
				case DRAW:
					iprtDraw(currentg, di);
					break;
				case DRAW_STRING:
					iprtDrawString(currentg, di);
					break;
				case DRAW_WEBIMAGE:
					iprtDrawWebImage(currentg, di);
					break;
				case DRAW_IMAGE:
					iprtDrawImage(currentg, di, imageHolder, partialImageMap);
					break;
				case COPY_AREA:
					iprtCopyArea(currentg, di, result);
					break;
				case SET_COMPOSITE:
					iprtSetComposite(currentg, di);
					break;
				case SET_FONT:
					iprtSetFont(currentg, di);
					break;
				case SET_PAINT:
					iprtSetPaint(currentg, di);
					break;
				case SET_STROKE:
					iprtSetStroke(currentg, di);
					break;
				case TRANSFORM:
					iprtTransform(currentg, di);
					break;
			}
		}

		for (Graphics2D g2d : gmap.values()) {
			g2d.dispose();
		}

		return result;
	}

	private static void iprtDrawImage(Graphics2D g, DrawInstruction di, BufferedImage imageHolder, Map<DrawInstruction, BufferedImage> partialImageMap) {
		AffineTransform original = g.getTransform();
		g.setTransform(new AffineTransform(1, 0, 0, 1, 0, 0));
		g.setClip(getShape(0, di));
		if (imageHolder != null) {
			g.drawImage(imageHolder, 0, 0, null);
		} else if (partialImageMap != null && partialImageMap.containsKey(di)) {
			int[] points = getValue(1, di);
			g.drawImage(partialImageMap.get(di), points[1], points[2], null);
		}
		g.setTransform(original);

	}

	private static void iprtDrawWebImage(Graphics2D g, DrawInstruction di) {
		BufferedImage i = di.getImage();
		AffineTransform t = getValue(0, di);
		Rectangle2D crop = getValue(1, di);
		Shape clip = getShape(3, di);
		g.setClip(clip);
		AffineTransform original = g.getTransform();
		if (t != null) {
			g.transform(t);
		}
		g.drawImage(i, 0, 0, (int) crop.getWidth(), (int) crop.getHeight(), (int) crop.getX(), (int) crop.getY(), (int) crop.getMaxX(), (int) crop.getMaxY(), null);
		g.setTransform(original);
	}

	private static void iprtDrawString(Graphics2D g, DrawInstruction di) {
		int[] points = getValue(1, di);
		Shape clip = getShape(2, di);
		g.setClip(clip);
		g.drawString(getValue(0, di, String.class), points[0], points[1]);
	}

	private static void iprtCopyArea(Graphics2D g, DrawInstruction di, BufferedImage result) {
		int[] points = getValue(0, di);
		g.setClip(getShape(1, di));
		AffineTransform original = g.getTransform();
		g.setTransform(new AffineTransform(1, 0, 0, 1, 0, 0));
		g.clipRect(points[0], points[1], points[2], points[3]);
		g.translate(points[4], points[5]);
		g.drawImage(result, 0, 0, null);
		g.setTransform(original);
	}

	private static void iprtDraw(Graphics2D g, DrawInstruction di) {
		g.setClip(getShape(1, di));
		g.draw(getShape(0, di));
	}

	private static void iprtFill(Graphics2D g, DrawInstruction di) {
		g.setClip(getShape(1, di));
		g.fill(getShape(0, di));
	}

	private static void iprtSetComposite(Graphics2D currentg, DrawInstruction di) {
		currentg.setComposite(getValue(0, di, Composite.class));
	}

	private static void iprtSetFont(Graphics2D currentg, DrawInstruction di) {
		currentg.setFont(getValue(0, di, Font.class));
	}

	private static void iprtTransform(Graphics2D currentg, DrawInstruction di) {
		currentg.transform(getValue(0, di, AffineTransform.class));
	}

	private static void iprtSetStroke(Graphics2D currentg, DrawInstruction di) {
		currentg.setStroke(getValue(0, di, Stroke.class));
	}

	private static void iprtSetPaint(Graphics2D currentg, DrawInstruction di) {
		currentg.setPaint(getPaint(0, di));
	}

	private static Graphics2D iprtGraphicsCreate(BufferedImage result, DrawInstruction di, Map<Integer, Graphics2D> gmap) {
		AffineTransform transform = getValue(1, di);
		Stroke stroke = getValue(2, di);
		Composite composite = getValue(3, di);
		Paint paint = getPaint(4, di);
		Font font = getValue(5, di);
		Graphics2D g = (Graphics2D) result.getGraphics();
		if (transform != null) {
			g.setTransform(transform);
		}
		if (stroke != null) {
			g.setStroke(stroke);
		}
		if (composite != null) {
			g.setComposite(composite);
		}
		if (paint != null) {
			g.setPaint(paint);
		}
		if (font != null) {
			g.setFont(font);
		}
		gmap.put(getValue(0, di, Integer.class), g);
		return g;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private static <T> T getValue(int index, DrawInstruction instruction, Class<? extends T> cls) {
		return (T) instruction.getArg(index).getValue();
	}

	@SuppressWarnings("unchecked")
	private static <T> T getValue(int index, DrawInstruction instruction) {
		return (T) instruction.getArg(index).getValue();
	}

	private static Paint getPaint(int index, DrawInstruction instruction) {
		return getValue(index, instruction);
	}

	public static Shape getShape(int index, DrawInstruction instruction) {
		return getValue(index, instruction);
	}

}
