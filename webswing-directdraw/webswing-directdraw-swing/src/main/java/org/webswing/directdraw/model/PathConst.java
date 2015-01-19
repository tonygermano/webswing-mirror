package org.webswing.directdraw.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.Arrays;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.PathProto;
import org.webswing.directdraw.proto.Directdraw.PathProto.SegmentTypeProto;

public class PathConst extends DrawConstant {

	private Shape shape;

	public PathConst(DirectDraw context, PathIterator pi) {
		super(context);
		PathProto.Builder model = PathProto.newBuilder();
		if (pi != null) {
			model.setWindingOdd(pi.getWindingRule() == PathIterator.WIND_EVEN_ODD ? true : false);
			double[] points = new double[6];
			while (!pi.isDone()) {
				int type = pi.currentSegment(points);
				int pointCount = type == PathIterator.SEG_CLOSE ? 0 : type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_LINETO ? 2 : type == PathIterator.SEG_QUADTO ? 4 : type == PathIterator.SEG_CUBICTO ? 6 : 0;
				model.addType(SegmentTypeProto.valueOf(type));
				for (double point : Arrays.copyOf(points, pointCount)) {
					model.addPoints((int) point);
				}
				pi.next();
			}
		}
		this.message = model.build();
	}

	public PathConst(DirectDraw context, Shape s, AffineTransform t) {
		this(context, s.getPathIterator(t));
		this.shape = t != null ? t.createTransformedShape(s) : s;
	}

	public Shape getShape() {
		return shape;
	}

	@Override
	public Object extractMessage(DirectDraw dd) {
		return super.extractMessage(dd);
	}

	@Override
	public String getFieldName() {
		return "path";
	}

}
