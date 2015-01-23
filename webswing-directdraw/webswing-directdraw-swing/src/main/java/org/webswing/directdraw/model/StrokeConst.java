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
		model.setWidthX10((int) (r.getLineWidth() * 10));
		model.setMiterLimitX10((int) (r.getMiterLimit() * 10));
		model.setJoin(StrokeJoinProto.valueOf(r.getLineJoin()));
		model.setCap(StrokeCapProto.valueOf(r.getEndCap()));
		model.setDashOffset((int) r.getDashPhase());
		if (r.getDashArray() != null && r.getDashArray().length > 0) {
			for (float d : r.getDashArray()) {
				model.addDashX10((int) (d * 10));
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
		float width = s.getWidthX10() / 10;
		int cap = s.getCap().getNumber();
		int join = s.getJoin().getNumber();
		float miterlimit = s.getMiterLimitX10() / 10;
		float[] dash = s.getDashX10Count() > 0 ? new float[s.getDashX10Count()] : null;
		for (int i = 0; i < s.getDashX10Count(); i++) {
			dash[i] = s.getDashX10(i) / 10;
		}
		float phase = s.getDashOffset();
		return new BasicStroke(width, cap, join, miterlimit, dash, phase);
	}
}
