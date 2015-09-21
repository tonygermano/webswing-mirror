package org.webswing.directdraw.model;

import java.awt.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class ColorConst extends ImmutableDrawConstantHolder<Color>
{

	public ColorConst(DirectDraw context, Color value) {
		super(context, value);
	}

	@Override
	public String getFieldName() {
		return "color";
	}

    @Override
    public ColorProto toMessage() {
        ColorProto.Builder model = ColorProto.newBuilder();
        model.setRgba(toRGBA(value));
        return model.build();
    }

    public static int toRGBA(Color color) {
        return (color.getRGB() << 8) | color.getAlpha();
    }
}
