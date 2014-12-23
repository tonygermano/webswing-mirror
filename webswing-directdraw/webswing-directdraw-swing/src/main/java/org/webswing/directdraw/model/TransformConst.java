package org.webswing.directdraw.model;

import java.awt.Font;
import java.awt.geom.AffineTransform;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.TransformProto;

public class TransformConst extends DrawConstant {

	public TransformConst(DirectDraw context, AffineTransform t) {
		super(context);
		TransformProto.Builder model = TransformProto.newBuilder();
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
		this.message = model.build();
	}

	public TransformConst(DirectDraw context, Font font, double x, double y) {
		super(context);
		TransformProto.Builder model = TransformProto.newBuilder();
		AffineTransform t = font.getTransform();
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
		if (x != 0) {
			model.setM02X2((int) Math.round(x * 2));
		}
		if (y != 0) {
			model.setM12X2((int) Math.round(y * 2));
		}
		this.message = model.build();
	}

	@Override
	public String getFieldName() {
		return "transform";
	}

	public AffineTransform getAffineTransform() {
		TransformProto m = (TransformProto) this.message;
		return new AffineTransform(m.hasM00() ? m.getM00() : 1, m.hasM01() ? m.getM01() : 0, m.hasM10() ? m.getM10() : 0, m.hasM11() ? m.getM11() : 1, m.hasM02X2() ? (m.getM02X2() / 2) : 0, m.hasM12X2() ? (m.getM12X2() / 2) : 0);
	}
}
