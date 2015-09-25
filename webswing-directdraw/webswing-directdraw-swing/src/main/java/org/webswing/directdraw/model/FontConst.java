package org.webswing.directdraw.model;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.FontProto;
import org.webswing.directdraw.proto.Directdraw.FontProto.StyleProto;
import org.webswing.directdraw.util.DirectDrawUtils;

public class FontConst extends ImmutableDrawConstantHolder<Font> {

	private static Map<Font, String> families = new HashMap<Font, String>();

	public FontConst(DirectDraw context, Font value) {
		super(context, value);
	}

	@Override
	public String getFieldName() {
		return "font";
	}

	@Override
	public FontProto toMessage() {
		FontProto.Builder model = FontProto.newBuilder();
		String family = getFamily(value);
		model.setFamily(DirectDrawUtils.windowsFonts.getProperty(family, family));
		model.setSize(value.getSize());
		model.setStyle(StyleProto.valueOf(value.getStyle()));
		if (value.isTransformed()) {
			model.setTransform(new TransformConst(getContext(), value.getTransform()).toMessage());
		}
		return model.build();
	}

	private static String getFamily(Font font) {
		String family = families.get(font);
		if (family == null) {
			families.put(font, family = font.getFamily());
		}
		return family;
	}
}
