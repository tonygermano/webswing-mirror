package org.webswing.directdraw.model;

import java.util.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class PointsConst extends DrawConstant {

    int[] points;
    
	public PointsConst(DirectDraw context, int... points) {
		super(context);
        this.points = points;
	}

	@Override
	public String getFieldName() {
		return "points";
	}

    @Override
    public Object toMessage() {
        PointsProto.Builder model = PointsProto.newBuilder();
        if (points != null) {
            for (int i = 0; i < points.length; i++) {
                model.addPoints(points[i]);
            }
        }
        return model.build();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(points);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || 
            o instanceof PointsConst && Arrays.equals(points, ((PointsConst) o).points);
    }

    public int[] getPoints() {
		return points;
	}
}
