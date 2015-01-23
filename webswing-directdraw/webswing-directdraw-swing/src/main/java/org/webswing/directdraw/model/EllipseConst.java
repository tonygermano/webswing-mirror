package org.webswing.directdraw.model;

import java.awt.geom.Ellipse2D;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.EllipseProto;

public class EllipseConst extends DrawConstant {

	public EllipseConst(DirectDraw context, Ellipse2D r) {
		super(context);
		EllipseProto.Builder model = EllipseProto.newBuilder();
		model.setX((int) r.getX());
		model.setY((int) r.getY());
		model.setW((int) r.getWidth());
		model.setH((int) r.getHeight());
		this.message = model.build();
	}

	@Override
	public String getFieldName() {
		return "ellipse";
	}

	public Ellipse2D.Float getEllipse(boolean biased) {
		EllipseProto e = (EllipseProto) message;
		float bias = biased ? 0.5f : 0;
		return new Ellipse2D.Float(e.getX() + bias, e.getY() + bias, e.getW(), e.getH());
	}

}
