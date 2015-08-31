package org.webswing.directdraw.model;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;
import org.webswing.directdraw.proto.Directdraw.PathProto.*;
import org.webswing.directdraw.util.*;

public class PathConst extends DrawConstant {

	private Shape shape;

	public PathConst(DirectDraw context, Shape shape) {
		super(context);
        this.shape = shape;
	}

	@Override
	public String getFieldName() {
		return "path";
	}

    @Override
    public Object toMessage() {
        PathProto.Builder model = PathProto.newBuilder();
        PathIterator iterator = shape.getPathIterator(null);
        if (iterator != null) {
            model.setWindingOdd(iterator.getWindingRule() == PathIterator.WIND_EVEN_ODD);
            double[] points = new double[6];
            while (!iterator.isDone()) {
                int type = iterator.currentSegment(points);
                int pointCount = getPointCount(type);
                model.addType(SegmentTypeProto.valueOf(type));
                for (int i = 0; i < pointCount; i ++) {
                    model.addPoints((int) points[i]);
                }
                iterator.next();
            }
        }
        return model.build();
    }
    
    private int getPointCount(int segmentType) {
        switch (segmentType) {
            case PathIterator.SEG_MOVETO:
            case PathIterator.SEG_LINETO:
                return 2;
            case PathIterator.SEG_QUADTO:
                return 4;
            case PathIterator.SEG_CUBICTO:
                return 6;
            default:
            case PathIterator.SEG_CLOSE:
                return 0;
        }
    }

    @Override
    public int hashCode() {
        int result = 1;
        PathIterator iterator = shape.getPathIterator(null);
        if (iterator != null) {
            result = 31 * result + iterator.getWindingRule();
            double[] points = new double[6];
            while (!iterator.isDone()) {
                int type = iterator.currentSegment(points);
                result = 31 * result + type;
                int pointCount = getPointCount(type);
                for (int i = 0; i < pointCount; i ++) {
                    result = 31 * result + DirectDrawUtils.hashCode(points[i]);
                }
                iterator.next();
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PathConst)) {
            return false;
        }
        PathIterator iterator = shape.getPathIterator(null);
        PathIterator other = ((PathConst) o).shape.getPathIterator(null);
        if (iterator != null && other != null) {
            if (iterator.getWindingRule() != other.getWindingRule()) {
                return false;
            }
            double[] points = new double[6];
            double[] otherPoints = new double[6];
            while (!iterator.isDone() && !other.isDone()) {
                iterator.currentSegment(points);
                other.currentSegment(otherPoints);
                if (!Arrays.equals(points, otherPoints)) {
                    return false;
                }
                iterator.next();
                other.next();
            }
            return iterator.isDone() == other.isDone();
        }
        return iterator == null && other == null;
    }

    public Shape getShape() {
        return shape;
	}
}
