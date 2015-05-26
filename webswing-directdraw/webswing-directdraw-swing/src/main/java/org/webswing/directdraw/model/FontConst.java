package org.webswing.directdraw.model;

import java.awt.Font;
import java.util.*;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.FontProto;
import org.webswing.directdraw.proto.Directdraw.FontProto.StyleProto;
import org.webswing.directdraw.util.DirectDrawUtils;

public class FontConst extends DrawConstant {

	private static Map<Font, String> families = new HashMap<Font, String>();

	public FontConst(DirectDraw context, Font f) {
		super(context);
		FontProto.Builder model = FontProto.newBuilder();
		String family = getFamily(f);
		model.setFamily(DirectDrawUtils.windowsFonts.getProperty(family, family));
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

	private static String getFamily(Font font) {
		String family = families.get(font);
		if (family == null)
		{
			families.put(font, family = font.getFamily());
		}
		return family;
	}
}
