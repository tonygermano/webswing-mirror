package org.webswing.directdraw.model;

import java.awt.Font;
import java.awt.geom.AffineTransform;

import org.webswing.directdraw.proto.Directdraw.TransformProto;
import org.webswing.directdraw.proto.Directdraw.TransformProto.Builder;

public class TransformConst extends DrawConstant<TransformProto.Builder> {

	private TransformProto.Builder model;

	public TransformConst(AffineTransform t) {
		this.model = TransformProto.newBuilder();
		double[] matrix = new double[6];
		t.getMatrix(matrix);
		if (matrix[0] != 1) {
			model.setM00((float) matrix[0]);
		}
		if (matrix[1] != 0) {
			model.setM10((float) matrix[1]);
		}
		if (matrix[2] != 0) {
			model.setM01((float) matrix[2]);
		}
		if (matrix[3] != 1) {
			model.setM11((float) matrix[3]);
		}
		if (matrix[4] != 0) {
			model.setM02X2((int) Math.round(matrix[4] * 2));
		}
		if (matrix[5] != 0) {
			model.setM12X2((int) Math.round(matrix[5] * 2));
		}
	}

	public TransformConst(Font font, double x, double y) {
		this(font.getTransform());
		if (x != 0) {
			model.setM02X2((int) Math.round(x * 2));
		}
		if (y != 0) {
			model.setM12X2((int) Math.round(y * 2));
		}
	}

	@Override
	protected Builder getProtoBuilder() {
		return model;
	}

	@Override
	public String getFieldName() {
		return "transform";
	}

}
