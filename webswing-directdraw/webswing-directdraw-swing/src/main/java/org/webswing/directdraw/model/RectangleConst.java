package org.webswing.directdraw.model;

import java.awt.geom.Rectangle2D;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.RectangleProto;

public class RectangleConst extends DrawConstant {

	public RectangleConst(DirectDraw context,Rectangle2D r) {
    	super(context);
		RectangleProto.Builder model = RectangleProto.newBuilder();
		model.setX((int) r.getX());
		model.setY((int) r.getY());
		model.setW((int) r.getWidth());
		model.setH((int) r.getHeight());
		this.message = model.build();
	}

	@Override
	public String getFieldName() {
		return "rectangle";
	}

}
