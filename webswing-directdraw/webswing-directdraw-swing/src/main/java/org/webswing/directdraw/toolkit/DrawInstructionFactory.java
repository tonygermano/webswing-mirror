package org.webswing.directdraw.toolkit;

import java.awt.*;
import java.awt.geom.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.model.*;
import org.webswing.directdraw.proto.Directdraw.DrawInstructionProto.*;

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

    public DrawInstruction drawImage(Shape clip) {
        return new DrawInstruction(InstructionProto.DRAW_IMAGE, toPathConst(clip), /*placeholder*/ DrawConstant.nullConst);
    }

	public DrawInstruction drawWebImage(WebImage image, AffineTransform xform, Rectangle2D.Float crop, Color bkg, Shape clip) {
		DrawConstant transformConst = xform != null ? new TransformConst(ctx, xform) : DrawConstant.nullConst;
		DrawConstant cropConst = crop != null ? new RectangleConst(ctx, crop) : DrawConstant.nullConst;
		DrawConstant bkgConst = bkg != null ? new ColorConst(ctx, bkg) : DrawConstant.nullConst;
		return new DrawInstruction(image, transformConst, cropConst, bkgConst, toPathConst(clip));
	}

	public DrawInstruction drawString(String s, double x, double y, Shape clip) {
		return new DrawInstruction(InstructionProto.DRAW_STRING, new StringConst(ctx, s), new PointsConst(ctx, (int) x, (int) y), toPathConst(clip));
	}

	public DrawInstruction copyArea(int destX, int destY, int width, int height, int absDx, int absDy, Shape clip) {
		return new DrawInstruction(InstructionProto.COPY_AREA, new PointsConst(ctx, destX, destY, width, height, absDx, absDy), toPathConst(clip));
	}

	public DrawInstruction createGraphics(WebGraphics g) {
		DrawConstant gid = new DrawConstant.IntegerConst(g.getId());
		DrawConstant transformConst =  new TransformConst(ctx, g.getTransform());
		DrawConstant compositeConst = g.getComposite() instanceof AlphaComposite ? new CompositeConst(ctx, (AlphaComposite) g.getComposite()) : DrawConstant.nullConst;
		DrawConstant strokeConst = g.getStroke() instanceof BasicStroke ? new StrokeConst(ctx, (BasicStroke) g.getStroke()) : DrawConstant.nullConst;
		DrawConstant paintConst = getPaintConstant(g.getPaint());
        DrawConstant fontConst = new FontConst(ctx, g.getFont());
		return new DrawInstruction(InstructionProto.GRAPHICS_CREATE, gid, transformConst, strokeConst, compositeConst, paintConst, fontConst);
	}

	public DrawInstruction disposeGraphics(WebGraphics g) {
		return new DrawInstruction(InstructionProto.GRAPHICS_DISPOSE, new DrawConstant.IntegerConst(g.getId()));
	}

	public DrawInstruction switchGraphics(WebGraphics g) {
		return new DrawInstruction(InstructionProto.GRAPHICS_SWITCH, new DrawConstant.IntegerConst(g.getId()));
	}

	public DrawInstruction transform(AffineTransform at) {
		return new DrawInstruction(InstructionProto.TRANSFORM, new TransformConst(ctx, at));
	}

	public DrawInstruction setPaint(Paint p) {
		return new DrawInstruction(InstructionProto.SET_PAINT, getPaintConstant(p));
	}
    
    protected DrawConstant getPaintConstant(Paint p)
    {
        if (p instanceof Color) {
            return new ColorConst(ctx, (Color) p);
        } else if (p instanceof GradientPaint) {
            return new LinearGradientConst(ctx, (GradientPaint) p);
        } else if (p instanceof LinearGradientPaint) {
            return new LinearGradientConst(ctx, (LinearGradientPaint) p);
        } else if (p instanceof RadialGradientPaint) {
            return new RadialGradientConst(ctx, (RadialGradientPaint) p);
        } else if (p instanceof TexturePaint) {
            return new TextureConst(ctx, (TexturePaint) p);
        }
        throw new UnsupportedOperationException();
    }

	public DrawInstruction setFont(Font font) {
		return new DrawInstruction(InstructionProto.SET_FONT, new FontConst(ctx, font));
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
			return new PathConst(ctx, s);
		}
	}

	public DrawInstruction setComposite(AlphaComposite ac) {
		return new DrawInstruction(InstructionProto.SET_COMPOSITE, new CompositeConst(ctx, ac));
	}
}
