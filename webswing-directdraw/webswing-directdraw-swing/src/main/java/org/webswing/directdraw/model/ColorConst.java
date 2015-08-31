package org.webswing.directdraw.model;

import java.awt.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class ColorConst extends DrawConstant {

    private Color color;
    
	public ColorConst(DirectDraw context, Color color) {
		super(context);
        this.color = color;
	}

	@Override
	public String getFieldName() {
		return "color";
	}

    @Override
    public Object toMessage() {
        ColorProto.Builder model = ColorProto.newBuilder();
        model.setRgba(toRGBA(color));
        return model.build();
    }

    @Override
    public int hashCode() {
        return color.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o == this ||
            o instanceof ColorConst && color.equals(((ColorConst) o).color);
    }

    public Color getColor() {
		return color;
	}

    public static int toRGBA(Color color) {
        return (color.getRGB() << 8) | color.getAlpha();
    }
}
