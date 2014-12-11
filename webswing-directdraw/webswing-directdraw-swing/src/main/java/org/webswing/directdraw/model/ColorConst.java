package org.webswing.directdraw.model;

import java.awt.Color;

import org.webswing.directdraw.proto.Directdraw.ColorProto;

public class ColorConst extends DrawConstant {

	public ColorConst(Color c) {
		ColorProto.Builder model = ColorProto.newBuilder();
		int rgba = (c.getRGB() << 8) | c.getAlpha();
		model.setRgba(rgba);
		this.message=model.build();
	}

	@Override
	public String getFieldName() {
		return "color";
	}

}
