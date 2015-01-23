package org.webswing.directdraw.model;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.PointsProto;

public class PointsConst extends DrawConstant {

	public PointsConst(DirectDraw context, int... points) {
		super(context);
		PointsProto.Builder model = PointsProto.newBuilder();
		if (points != null) {
			for (int i = 0; i < points.length; i++) {
				model.addPoints(points[i]);
			}
		}
		this.message = model.build();
	}

	@Override
	public String getFieldName() {
		return "points";
	}

	public java.lang.Integer[] getIntArray() {
		return ((PointsProto) message).getPointsList().toArray(new java.lang.Integer[((PointsProto) message).getPointsCount()]);
	}
}
