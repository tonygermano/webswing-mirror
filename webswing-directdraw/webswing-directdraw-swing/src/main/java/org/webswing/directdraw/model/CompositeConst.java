package org.webswing.directdraw.model;

import java.awt.AlphaComposite;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.CompositeProto;
import org.webswing.directdraw.proto.Directdraw.CompositeProto.CompositeTypeProto;

public class CompositeConst extends DrawConstant {

	public CompositeConst(DirectDraw context, AlphaComposite composite) {
		super(context);
		CompositeProto.Builder model = CompositeProto.newBuilder();
		model.setType(CompositeTypeProto.valueOf(composite.getRule()));
		if (composite.getAlpha() != 1) {
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
		float alpha = c.hasAlpha() ? c.getAlpha() : 1;
		return AlphaComposite.getInstance(c.getType().getNumber(), alpha);
	}
}
