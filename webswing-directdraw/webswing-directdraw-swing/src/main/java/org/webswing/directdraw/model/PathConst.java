package org.webswing.directdraw.model;

import java.awt.*;
import java.awt.geom.*;
import java.awt.geom.Path2D.Float;
import java.util.List;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;
import org.webswing.directdraw.proto.Directdraw.PathProto.*;

public class PathConst extends DrawConstant {

	private Shape shape;

	public PathConst(DirectDraw context, PathIterator pi) {
		super(context);
		PathProto.Builder model = PathProto.newBuilder();
		if (pi != null) {
			model.setWindingOdd(pi.getWindingRule() == PathIterator.WIND_EVEN_ODD);
			double[] points = new double[6];
			while (!pi.isDone()) {
				int type = pi.currentSegment(points);
				int pointCount = type == PathIterator.SEG_CLOSE ? 0 : type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_LINETO ? 2 : type == PathIterator.SEG_QUADTO ? 4 : type == PathIterator.SEG_CUBICTO ? 6 : 0;
				model.addType(SegmentTypeProto.valueOf(type));
				for (int i = 0; i < pointCount; i ++) {
					model.addPoints((int) points[i]);
				}
				pi.next();
			}
		}
		this.message = model.build();
	}

	public PathConst(DirectDraw context, Shape s) {
		this(context, s.getPathIterator(null));
		this.shape = s;
	}

	public Shape getShape() {
		return shape;
	}

	@Override
	public String getFieldName() {
		return "path";
	}

	public Path2D.Float getPath(boolean biased) {
		PathProto p = (PathProto) message;
		float bias = biased ? 0.5f : 0;
		Float path = new Path2D.Float(p.getWindingOdd() ? PathIterator.WIND_EVEN_ODD : PathIterator.WIND_NON_ZERO);
		List<java.lang.Integer> pts = p.getPointsList();
		int offset = 0;
		for (SegmentTypeProto type : p.getTypeList()) {
			int pointCount = type == SegmentTypeProto.CLOSE ? 0 : type == SegmentTypeProto.MOVE || type == SegmentTypeProto.LINE ? 2 : type == SegmentTypeProto.QUAD ? 4 : type == SegmentTypeProto.CUBIC ? 6 : 0;
			switch (type) {
			case MOVE:
				path.moveTo(pts.get(offset) + bias, pts.get(offset + 1) + bias);
				break;
			case LINE:
				path.lineTo(pts.get(offset) + bias, pts.get(offset + 1) + bias);
				break;
			case QUAD:
				path.quadTo(pts.get(offset) + bias, pts.get(offset + 1) + bias, pts.get(offset + 2), pts.get(offset + 3));
				break;
			case CUBIC:
				path.curveTo(pts.get(offset) + bias, pts.get(offset + 1) + bias, pts.get(offset + 2), pts.get(offset + 3), pts.get(offset + 4), pts.get(offset + 5));
				break;
			case CLOSE:
				path.closePath();
				break;
			}
			offset += pointCount;
		}
		return path;
	}
}
