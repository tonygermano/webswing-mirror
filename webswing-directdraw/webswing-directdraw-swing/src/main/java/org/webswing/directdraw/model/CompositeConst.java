package org.webswing.directdraw.model;

import java.awt.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;
import org.webswing.directdraw.proto.Directdraw.CompositeProto.*;

public class CompositeConst extends DrawConstant {

	public CompositeConst(DirectDraw context, AlphaComposite composite) {
		super(context);
		CompositeProto.Builder model = CompositeProto.newBuilder();
		model.setType(CompositeTypeProto.valueOf(composite.getRule()));
		if (composite.getAlpha() != 1f) {
			model.setAlpha(composite.getAlpha());
		}
		this.message = model.build();
	}

	@Override
	public String getFieldName() {
		return "composite";
	}

	public AlphaComposite getComposite() {
		CompositeProto c = (CompositeProto) message;
		return AlphaComposite.getInstance(c.getType().getNumber(), c.getAlpha());
	}
}
