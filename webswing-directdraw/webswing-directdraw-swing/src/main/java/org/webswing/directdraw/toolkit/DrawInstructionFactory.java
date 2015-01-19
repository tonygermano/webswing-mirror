package org.webswing.directdraw.toolkit;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
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

import org.webswing.directdraw.DirectDraw;
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
import org.webswing.directdraw.proto.Directdraw.DrawInstructionProto.InstructionProto;
import org.webswing.directdraw.util.DirectDrawUtils;

public class DrawInstructionFactory {

	private DirectDraw ctx;

	public DrawInstructionFactory(DirectDraw ctx) {
		this.ctx = ctx;
	}

	public DrawInstruction draw(Shape s, Shape clip) {
		return new DrawInstruction(InstructionProto.DRAW, toPathConst(s), toPathConst(clip));
	}

	public DrawInstruction fill(Shape s, Shape clip) {
		return new DrawInstruction(InstructionProto.FILL, toPathConst(s), toPathConst(clip));
	}

	public DrawInstruction drawImage(WebImage image, AffineTransform xform, Rectangle2D.Float crop, Color bkg, Shape clip) {
		DrawConstant transformConst = xform != null ? new TransformConst(ctx, xform) : DrawConstant.nullConst;
		DrawConstant cropConst = crop != null ? new RectangleConst(ctx, crop) : DrawConstant.nullConst;
		DrawConstant bkgConst = bkg != null ? new ColorConst(ctx, bkg) : DrawConstant.nullConst;
		return new DrawInstruction((WebImage) image, transformConst, cropConst, bkgConst, toPathConst(clip));
	}

	public DrawInstruction drawString(String s, double x, double y, Font font, Shape clip) {
		return new DrawInstruction(InstructionProto.DRAW_STRING, new StringConst(ctx, s), new FontConst(ctx, font), new TransformConst(ctx, font, x, y), toPathConst(clip));
	}

	public DrawInstruction copyArea(int destX, int destY, int width, int height, int absDx, int absDy, Shape clip) {
		return new DrawInstruction(InstructionProto.COPY_AREA, new PointsConst(ctx, destX, destY, width, height, absDx, absDy), toPathConst(clip));
	}

	public DrawInstruction createGraphics(WebGraphics g) {
		DrawConstant gid = new DrawConstant.Integer(g.getId());
		DrawConstant transformConst = g.getTransform() != null ? new TransformConst(ctx, g.getTransform()) : DrawConstant.nullConst;
		DrawConstant compositeConst = g.getComposite() instanceof AlphaComposite ? new CompositeConst(ctx, (AlphaComposite) g.getComposite()) : DrawConstant.nullConst;
		DrawConstant strokeConst = g.getStroke() instanceof BasicStroke ? new StrokeConst(ctx, (BasicStroke) g.getStroke()) : DrawConstant.nullConst;
		DrawInstruction paintinst = setPaint(g.getPaint());
		DrawConstant[] paintConsts = DirectDrawUtils.concat(new DrawConstant[] { gid, transformConst, strokeConst, compositeConst }, paintinst.getArgs());

		return new DrawInstruction(InstructionProto.GRAPHICS_CREATE, paintConsts);
	}

	public DrawInstruction disposeGraphics(WebGraphics g) {
		return new DrawInstruction(InstructionProto.GRAPHICS_DISPOSE, new DrawConstant.Integer(g.getId()));
	}

	public DrawInstruction switchGraphics(WebGraphics g) {
		return new DrawInstruction(InstructionProto.GRAPHICS_SWITCH, new DrawConstant.Integer(g.getId()));
	}

	public DrawInstruction transform(AffineTransform at) {
		return new DrawInstruction(InstructionProto.TRANSFORM, new TransformConst(ctx, at));
	}

	public DrawInstruction setPaint(Paint p) {
		if (p instanceof Color) {
			return new DrawInstruction(InstructionProto.SET_PAINT, new ColorConst(ctx, (Color) p));
		} else if (p instanceof GradientPaint) {
			return new DrawInstruction(InstructionProto.SET_PAINT, new LinearGradientConst(ctx, (GradientPaint) p));
		} else if (p instanceof LinearGradientPaint) {
			return new DrawInstruction(InstructionProto.SET_PAINT, new LinearGradientConst(ctx, (LinearGradientPaint) p));
		} else if (p instanceof RadialGradientPaint) {
			return new DrawInstruction(InstructionProto.SET_PAINT, new RadialGradientConst(ctx, (RadialGradientPaint) p));
		} else if (p instanceof TexturePaint) {
			TexturePaint t = (TexturePaint) p;
			return new DrawInstruction(InstructionProto.SET_PAINT, new ImageConst(ctx, t.getImage(), null, null), new RectangleConst(ctx, t.getAnchorRect()));
		}
		return new DrawInstruction(InstructionProto.SET_PAINT);
	}

	public DrawInstruction setFont(Font f) {
		return new DrawInstruction(InstructionProto.SET_FONT, new FontConst(ctx, f));
	}

	public DrawInstruction setStroke(BasicStroke stroke) {
		return new DrawInstruction(InstructionProto.SET_STROKE, new StrokeConst(ctx, stroke));
	}

	private DrawConstant toPathConst(Shape s) {
		if (s == null) {
			return DrawConstant.nullConst;
		}
		if (s instanceof Rectangle2D) {
			return new RectangleConst(ctx, (Rectangle2D) s);
		} else if (s instanceof RoundRectangle2D) {
			return new RoundRectangleConst(ctx, (RoundRectangle2D) s);
		} else if (s instanceof Ellipse2D) {
			return new EllipseConst(ctx, (Ellipse2D) s);
		} else if (s instanceof Arc2D) {
			return new ArcConst(ctx, (Arc2D) s);
		} else {
			return new PathConst(ctx, s.getPathIterator(null));
		}
	}

	public DrawInstruction setComposite(AlphaComposite ac) {
		return new DrawInstruction(InstructionProto.SET_COMPOSITE, new CompositeConst(ctx, ac));
	}
}
