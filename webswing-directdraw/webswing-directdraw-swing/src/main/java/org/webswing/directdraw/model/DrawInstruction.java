package org.webswing.directdraw.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.DrawInstructionProto;
import org.webswing.directdraw.proto.Directdraw.DrawInstructionProto.InstructionProto;
import org.webswing.directdraw.toolkit.WebGraphics;
import org.webswing.directdraw.toolkit.WebImage;

public class DrawInstruction {

	private InstructionProto instruction;
	private DrawConstant[] args;
	private WebImage image;

	private DrawInstruction(InstructionProto type, DrawConstant... args) {
		instruction = type;
		ArrayList<DrawConstant> constants = new ArrayList<DrawConstant>();
		for (DrawConstant c : args) {
			if (c != null) {
				constants.add(c);
			}
		}
		this.args = constants.toArray(new DrawConstant[constants.size()]);
	}

	private DrawInstruction(WebImage image, DrawConstant... args) {
		instruction = InstructionProto.DRAW_WEBIMAGE;
		this.image = image;
		ArrayList<DrawConstant> constants = new ArrayList<DrawConstant>();
		for (DrawConstant c : args) {
			if (c != null) {
				constants.add(c);
			}
		}
		this.args = constants.toArray(new DrawConstant[constants.size()]);
	}

	public DrawConstant[] getArgs() {
		return args;
	}

	public WebImage getImage() {
		return image;
	}

	public InstructionProto getInstruction() {
		return instruction;
	}

	public DrawInstructionProto toMessage(DirectDraw dd) {
		DrawInstructionProto.Builder builder = DrawInstructionProto.newBuilder();
		builder.setInst(instruction);
		for (DrawConstant c : args) {
			builder.addArgs(c.getAddress());
		}
		if (image != null) {
			builder.setWebImage(image.toMessage(dd).toByteString());
		}
		return builder.build();
	}

	public static DrawInstruction draw(Shape s, Shape clip) {
		return new DrawInstruction(InstructionProto.DRAW, toPathConst(s), toPathConst(clip));
	}

	public static DrawInstruction fill(Shape s, Shape clip) {
		return new DrawInstruction(InstructionProto.FILL, toPathConst(s), toPathConst(clip));
	}

	public static DrawInstruction drawImage(Image image, AffineTransform xform, Rectangle2D.Float crop, Color bkg, Shape clip) {
		DrawConstant transformConst = xform != null ? new TransformConst(xform) : DrawConstant.nullConst;
		DrawConstant cropConst = crop != null ? new RectangleConst(crop) : DrawConstant.nullConst;
		DrawConstant bkgConst = bkg != null ? new ColorConst(bkg) : DrawConstant.nullConst;
		if (image instanceof BufferedImage) {
			return new DrawInstruction(InstructionProto.DRAW_IMAGE, new ImageConst((BufferedImage) image), transformConst, cropConst, bkgConst, toPathConst(clip));
		} else if (image instanceof WebImage) {
			return new DrawInstruction((WebImage) image, transformConst, cropConst, bkgConst, toPathConst(clip));
		}
		return null;
	}

	public static DrawInstruction drawString(String s, double x, double y, Font font, Shape clip) {
		return new DrawInstruction(InstructionProto.DRAW_STRING, new StringConst(s), new FontConst(font), new TransformConst(font, x, y), toPathConst(clip));
	}

	public static DrawInstruction copyArea(int x, int y, int width, int height, int dx, int dy, Shape clip) {
		return new DrawInstruction(InstructionProto.COPY_AREA, new PointsConst(x, y, width, height, dx, dy), toPathConst(clip));
	}

	public static DrawInstruction createGraphics(WebGraphics g, WebGraphics parent) {
		return new DrawInstruction(InstructionProto.GRAPHICS_CREATE, new DrawConstant.Integer(g.getId()), parent != null ? new DrawConstant.Integer(parent.getId()) : null);
	}

	public static DrawInstruction disposeGraphics(WebGraphics g) {
		return new DrawInstruction(InstructionProto.GRAPHICS_DISPOSE, new DrawConstant.Integer(g.getId()));
	}

	public static DrawInstruction switchGraphics(WebGraphics g) {
		return new DrawInstruction(InstructionProto.GRAPHICS_SWITCH, new DrawConstant.Integer(g.getId()));
	}

	public static DrawInstruction transform(AffineTransform at) {
		return new DrawInstruction(InstructionProto.TRANSFORM, new TransformConst(at));
	}

	public static DrawInstruction setPaint(Paint p) {
		if (p instanceof Color) {
			return new DrawInstruction(InstructionProto.SET_PAINT, new ColorConst((Color) p));
		} else if (p instanceof GradientPaint) {
			return new DrawInstruction(InstructionProto.SET_PAINT, new LinearGradientConst((GradientPaint) p));
		} else if (p instanceof LinearGradientPaint) {
			return new DrawInstruction(InstructionProto.SET_PAINT, new LinearGradientConst((LinearGradientPaint) p));
		} else if (p instanceof RadialGradientPaint) {
			return new DrawInstruction(InstructionProto.SET_PAINT, new RadialGradientConst((RadialGradientPaint) p));
		} else if (p instanceof TexturePaint) {
			TexturePaint t = (TexturePaint) p;
			return new DrawInstruction(InstructionProto.SET_PAINT, new ImageConst(t.getImage()), new RectangleConst(t.getAnchorRect()));
		}
		return new DrawInstruction(InstructionProto.SET_PAINT);
	}

	public static DrawInstruction setFont(Font f) {
		return new DrawInstruction(InstructionProto.SET_FONT, new FontConst(f));
	}

	public static DrawInstruction setStroke(BasicStroke stroke) {
		return new DrawInstruction(InstructionProto.SET_STROKE, new StrokeConst(stroke));
	}

	private static DrawConstant toPathConst(Shape s) {
		if (s == null) {
			return DrawConstant.nullConst;
		}
		if (s instanceof Rectangle2D) {
			return new RectangleConst((Rectangle2D) s);
		} else if (s instanceof RoundRectangle2D) {
			return new RoundRectangleConst((RoundRectangle2D) s);
		} else if (s instanceof Ellipse2D) {
			return new EllipseConst((Ellipse2D) s);
		} else if (s instanceof Arc2D) {
			return new ArcConst((Arc2D) s);
		} else {
			return new PathConst(s.getPathIterator(null));
		}
	}

}
