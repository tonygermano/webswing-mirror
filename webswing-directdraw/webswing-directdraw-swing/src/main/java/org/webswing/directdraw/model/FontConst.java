package org.webswing.directdraw.model;

import java.awt.Font;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.FontProto;
import org.webswing.directdraw.proto.Directdraw.FontProto.StyleProto;
import org.webswing.directdraw.util.DirectDrawUtils;

public class FontConst extends DrawConstant {

	public FontConst(DirectDraw context, Font f) {
		super(context);
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

	public Font getFont() {
		FontProto f = (FontProto) message;
		String name = DirectDrawUtils.webFonts.getProperty(f.getFamily(), f.getFamily());
		int style = f.getStyle().getNumber();
		int size = f.getSize();
		return new Font(name, style, size);
	}
}
