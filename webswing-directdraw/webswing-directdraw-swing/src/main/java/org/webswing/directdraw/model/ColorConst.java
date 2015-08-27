package org.webswing.directdraw.model;

import java.awt.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class ColorConst extends DrawConstant {

	public ColorConst(DirectDraw context, Color c) {
		super(context);
		ColorProto.Builder model = ColorProto.newBuilder();
		model.setRgba(toRGBA(c));
		this.message = model.build();
	}

	@Override
	public String getFieldName() {
		return "color";
	}

	public Color getColor() {
		return getColor(((ColorProto) message).getRgba());
	}

	public static Color getColor(int rgba) {
		int r = rgba >> 24 & 0x000000FF;
		int g = rgba >> 16 & 0x000000FF;
		int b = rgba >> 8 & 0x000000FF;
		int a = rgba & 0x000000FF;
		return new Color(r, g, b, a);
	}
    
    public static int toRGBA(Color color) {
        return (color.getRGB() << 8) | color.getAlpha();
    }
}
