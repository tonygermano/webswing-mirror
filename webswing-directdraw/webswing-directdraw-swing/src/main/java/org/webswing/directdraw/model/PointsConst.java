package org.webswing.directdraw.model;

import org.webswing.directdraw.proto.Directdraw.PointsProto;

public class PointsConst extends DrawConstant {

	public PointsConst(int... points) {
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

}
