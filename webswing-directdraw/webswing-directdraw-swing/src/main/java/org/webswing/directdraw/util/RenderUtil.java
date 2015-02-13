package org.webswing.directdraw.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.webswing.directdraw.model.ArcConst;
import org.webswing.directdraw.model.ColorConst;
import org.webswing.directdraw.model.CompositeConst;
import org.webswing.directdraw.model.DrawConstant;
import org.webswing.directdraw.model.DrawInstruction;
import org.webswing.directdraw.model.EllipseConst;
import org.webswing.directdraw.model.FontConst;
import org.webswing.directdraw.model.ImageConst;
import org.webswing.directdraw.model.LinearGradientConst;
import org.webswing.directdraw.model.PathConst;
import org.webswing.directdraw.model.PointsConst;
import org.webswing.directdraw.model.RadialGradientConst;
import org.webswing.directdraw.model.RectangleConst;
import org.webswing.directdraw.model.RoundRectangleConst;
import org.webswing.directdraw.model.StringConst;
import org.webswing.directdraw.model.StrokeConst;
import org.webswing.directdraw.model.TransformConst;
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
			case GRAPHICS_DISPOSE:
				break;
			case GRAPHICS_SWITCH:
				currentg = gmap.get(getConst(0, di, DrawConstant.Integer.class).getAddress());
				break;
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
		Shape clip = getShape(getConst(0, di, DrawConstant.class), false);
		AffineTransform original = g.getTransform();
		g.setTransform(new AffineTransform(1, 0, 0, 1, 0, 0));
		g.setClip(clip);
		if (imageHolder != null) {
			g.drawImage(imageHolder, 0, 0, null);
		} else if (partialImageMap != null && partialImageMap.containsKey(di)) {
			Integer[] points = getConst(1, di, PointsConst.class).getIntArray();
			g.drawImage(partialImageMap.get(di), points[1], points[2], null);
		}
		g.setTransform(original);

	}

	private static void iprtDrawWebImage(Graphics2D g, DrawInstruction di) {
		BufferedImage i = di.getImage().getSnapshot();
		TransformConst t = getConst(0, di, TransformConst.class);
		Rectangle2D crop = getConst(1, di, RectangleConst.class).getRectangle(false);
		Shape clip = getShape(getConst(3, di, DrawConstant.class), false);
		g.setClip(clip);
		AffineTransform original = g.getTransform();
		g.transform(t.getAffineTransform());
		g.drawImage(i, 0, 0, (int) crop.getWidth(), (int) crop.getHeight(), (int) crop.getX(), (int) crop.getY(), (int) crop.getX() + (int) crop.getWidth(), (int) crop.getY() + (int) crop.getHeight(), null);
		g.setTransform(original);
	}

	private static void iprtDrawString(Graphics2D g, DrawInstruction di) {
		StringConst s = getConst(0, di, StringConst.class);
		FontConst f = getConst(1, di, FontConst.class);
		TransformConst t = getConst(2, di, TransformConst.class);
		Shape clip = getShape(getConst(3, di, DrawConstant.class), false);
		g.setClip(clip);
		AffineTransform original = g.getTransform();
		g.transform(t.getAffineTransform());
		g.setFont(f.getFont());
		g.drawString(s.getString(), 0, 0);
		g.setTransform(original);
	}

	private static void iprtCopyArea(Graphics2D g, DrawInstruction di, BufferedImage result) {
		PointsConst p = getConst(0, di, PointsConst.class);
		Integer[] pts = p.getIntArray();
		PathConst clip = getConst(1, di, PathConst.class);
		g.setClip(getShape(clip, false));
		AffineTransform original = g.getTransform();
		g.setTransform(new AffineTransform(1, 0, 0, 1, 0, 0));
		g.clipRect(pts[0], pts[1], pts[2], pts[3]);
		g.translate(pts[4], pts[5]);
		g.drawImage(result, 0, 0, null);
		g.setTransform(original);
	}

	private static void iprtDraw(Graphics2D g, DrawInstruction di) {
		DrawConstant path = getConst(0, di, DrawConstant.class);
		DrawConstant clip = getConst(1, di, DrawConstant.class);
		g.setClip(getShape(clip, false));
		g.draw(getShape(path, true));
	}

	private static void iprtFill(Graphics2D g, DrawInstruction di) {
		DrawConstant path = getConst(0, di, DrawConstant.class);
		DrawConstant clip = getConst(1, di, DrawConstant.class);
		g.setClip(getShape(clip, false));
		g.fill(getShape(path, false));
	}

	private static void iprtSetComposite(Graphics2D currentg, DrawInstruction di) {
		CompositeConst c = getConst(0, di, CompositeConst.class);
		currentg.setComposite(c.getComposite());
	}

	private static void iprtTransform(Graphics2D currentg, DrawInstruction di) {
		TransformConst t = getConst(0, di, TransformConst.class);
		currentg.transform(t.getAffineTransform());
	}

	private static void iprtSetStroke(Graphics2D currentg, DrawInstruction di) {
		StrokeConst s = getConst(0, di, StrokeConst.class);
		currentg.setStroke(s.getStroke());
	}

	private static void iprtSetPaint(Graphics2D currentg, DrawInstruction di) {
		currentg.setPaint(getPaint(0, di));
	}

	private static Graphics2D iprtGraphicsCreate(BufferedImage result, DrawInstruction di, Map<Integer, Graphics2D> gmap) {
		DrawConstant.Integer idConst = getConst(0, di, DrawConstant.Integer.class);
		TransformConst transform = getConst(1, di, TransformConst.class);
		StrokeConst stroke = getConst(2, di, StrokeConst.class);
		CompositeConst composite = getConst(3, di, CompositeConst.class);
		Paint paint = getPaint(4, di);
		Graphics2D g = (Graphics2D) result.getGraphics();
		if (transform != null) {
			g.setTransform(transform.getAffineTransform());
		}
		if (stroke != null) {
			g.setStroke(stroke.getStroke());
		}
		if (composite != null) {
			g.setComposite(composite.getComposite());
		}
		if (paint != null) {
			g.setPaint(paint);
		}
		gmap.put(idConst.getAddress(), g);
		return g;
	}

	@SuppressWarnings("unchecked")
	private static <T> T getConst(int i, DrawInstruction di, Class<T> clazz) {
		if (di.getArgs().length > i && di.getArgs()[i] != DrawConstant.nullConst) {
			return (T) di.getArgs()[i];
		}
		return null;
	}

	private static Paint getPaint(int offset, DrawInstruction di) {
		Paint result = null;
		DrawConstant paintConst = getConst(offset, di, DrawConstant.class);
		if (paintConst instanceof ColorConst) {
			result = ((ColorConst) paintConst).getColor();
		} else if (paintConst instanceof LinearGradientConst) {
			result = ((LinearGradientConst) paintConst).getLinearGradientPaint();
		} else if (paintConst instanceof RadialGradientConst) {
			result = ((RadialGradientConst) paintConst).getRadialGradientPaint();
		} else if (paintConst instanceof ImageConst) {
			RectangleConst rect = getConst(offset + 1, di, RectangleConst.class);
			result = ((ImageConst) paintConst).getTexturePaint(rect.getRectangle(false));
		}
		return result;
	}

	private static Shape getShape(DrawConstant s, boolean biased) {
		Shape shape = null;
		if (s instanceof RectangleConst) {
			return ((RectangleConst) s).getRectangle(biased);
		} else if (s instanceof RoundRectangleConst) {
			return ((RoundRectangleConst) s).getRoundRectangle(biased);
		} else if (s instanceof EllipseConst) {
			return ((EllipseConst) s).getEllipse(biased);
		} else if (s instanceof ArcConst) {
			return ((ArcConst) s).getArc(biased);
		} else if (s instanceof PathConst) {
			return ((PathConst) s).getPath(biased);
		}
		return shape;
	}

}
