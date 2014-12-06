package org.webswing.directdraw.model;

import java.awt.geom.PathIterator;
import java.util.Arrays;

import org.webswing.directdraw.proto.Directdraw.PathProto;
import org.webswing.directdraw.proto.Directdraw.PathProto.Builder;
import org.webswing.directdraw.proto.Directdraw.PathProto.SegmentTypeProto;

public class PathConst extends DrawConstant<PathProto.Builder> {

    private PathProto.Builder model;

    public PathConst(PathIterator pi) {
        this.model = PathProto.newBuilder();
        if (pi != null) {
            model.setWindingOdd(pi.getWindingRule() == PathIterator.WIND_EVEN_ODD ? true : false);
            double[] points = new double[6];
            while (!pi.isDone()) {
                int type = pi.currentSegment(points);
                int pointCount = type == PathIterator.SEG_CLOSE ? 0 : type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_LINETO ? 2 : type == PathIterator.SEG_QUADTO ? 4 : type == PathIterator.SEG_CUBICTO ? 6 : 0;
                model.addType(SegmentTypeProto.valueOf(type));
                for(double point :Arrays.copyOf(points, pointCount)){
                	model.addPoints((int) point);
                }
                pi.next();
            }
        }
    }

    @Override
    protected Builder getProtoBuilder() {
        return model;
    }

    @Override
    public String getFieldName() {
        return "path";
    }

}
