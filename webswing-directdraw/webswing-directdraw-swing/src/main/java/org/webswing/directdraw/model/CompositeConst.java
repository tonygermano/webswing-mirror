package org.webswing.directdraw.model;

import java.awt.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;
import org.webswing.directdraw.proto.Directdraw.CompositeProto.*;

public class CompositeConst extends DrawConstant {

    private AlphaComposite composite;
    
	public CompositeConst(DirectDraw context, AlphaComposite composite) {
		super(context);
		this.composite = composite;
	}

	@Override
	public String getFieldName() {
		return "composite";
	}

    @Override
    public Object toMessage() {
        CompositeProto.Builder model = CompositeProto.newBuilder();
        model.setType(CompositeTypeProto.valueOf(composite.getRule()));
        if (composite.getAlpha() != 1f) {
            model.setAlpha(composite.getAlpha());
        }
        return model.build();
    }

    @Override
    public int hashCode() {
        return composite.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o == this ||
            o instanceof CompositeConst && composite.equals(((CompositeConst) o).composite);
    }

    public AlphaComposite getComposite() {
		return composite;
	}
}
