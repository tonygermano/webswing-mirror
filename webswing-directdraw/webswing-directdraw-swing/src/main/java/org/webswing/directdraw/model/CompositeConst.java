package org.webswing.directdraw.model;

import java.awt.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;
import org.webswing.directdraw.proto.Directdraw.CompositeProto.*;

public class CompositeConst extends ImmutableDrawConstantHolder<AlphaComposite>
{

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
