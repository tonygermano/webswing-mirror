package org.webswing.directdraw.model;

import java.awt.AlphaComposite;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.CompositeProto;
import org.webswing.directdraw.proto.Directdraw.CompositeProto.CompositeTypeProto;

public class CompositeConst extends ImmutableDrawConstantHolder<AlphaComposite> {

	public CompositeConst(DirectDraw context, AlphaComposite value) {
		super(context, value);
	}

	@Override
	public String getFieldName() {
		return "composite";
	}

	@Override
	public CompositeProto toMessage() {
		CompositeProto.Builder model = CompositeProto.newBuilder();
		model.setType(CompositeTypeProto.valueOf(value.getRule()));
		if (value.getAlpha() != 1f) {
			model.setAlpha(value.getAlpha());
		}
		return model.build();
	}
}
