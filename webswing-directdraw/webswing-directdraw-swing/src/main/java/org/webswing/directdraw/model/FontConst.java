package org.webswing.directdraw.model;

import java.awt.Font;

import org.webswing.directdraw.proto.Directdraw.FontProto;
import org.webswing.directdraw.proto.Directdraw.FontProto.StyleProto;
import org.webswing.directdraw.util.DirectDrawUtils;

public class FontConst extends DrawConstant {

	public FontConst(Font f) {
		FontProto.Builder model = FontProto.newBuilder();
		model.setFamily(DirectDrawUtils.windowsFonts.getProperty(f.getFamily(), f.getFamily()));
		model.setSize(f.getSize());
		model.setStyle(StyleProto.valueOf(f.getStyle()));
		this.message = model.build();
	}

	@Override
	public String getFieldName() {
		return "font";
	}
}
