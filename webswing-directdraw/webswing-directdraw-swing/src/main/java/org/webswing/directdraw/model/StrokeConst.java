package org.webswing.directdraw.model;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.StrokeProto;
import org.webswing.directdraw.proto.Directdraw.StrokeProto.StrokeCapProto;
import org.webswing.directdraw.proto.Directdraw.StrokeProto.StrokeJoinProto;

public class StrokeConst extends DrawConstant {

	public StrokeConst(DirectDraw context, BasicStroke r) {
		super(context);
		StrokeProto.Builder model = StrokeProto.newBuilder();
		model.setWidth(r.getLineWidth());
		model.setMiterLimit(r.getMiterLimit());
		model.setJoin(StrokeJoinProto.valueOf(r.getLineJoin()));
		model.setCap(StrokeCapProto.valueOf(r.getEndCap()));
		model.setDashOffset(r.getDashPhase());
		if (r.getDashArray() != null && r.getDashArray().length > 0) {
			for (float d : r.getDashArray()) {
				model.addDash(d);
			}
		}
		this.message = model.build();
	}

	@Override
	public String getFieldName() {
		return "stroke";
	}

	public Stroke getStroke() {
		StrokeProto s = (StrokeProto) message;
		float width = s.getWidth();
		int cap = s.getCap().getNumber();
		int join = s.getJoin().getNumber();
		float miterlimit = s.getMiterLimit();
		float[] dash = s.getDashCount() > 0 ? new float[s.getDashCount()] : null;
		for (int i = 0; i < s.getDashCount(); i++) {
			dash[i] = s.getDash(i);
		}
		float phase = s.getDashOffset();
		return new BasicStroke(width, cap, join, miterlimit, dash, phase);
	}
}
