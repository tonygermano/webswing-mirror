package org.webswing.directdraw.model;

import java.awt.*;
import java.util.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;
import org.webswing.directdraw.proto.Directdraw.FontProto.*;
import org.webswing.directdraw.util.*;

public class FontConst extends DrawConstant {

	private static Map<Font, String> families = new HashMap<Font, String>();

    private Font font;
    
	public FontConst(DirectDraw context, Font font) {
		super(context);
		this.font = font;
	}

	@Override
	public String getFieldName() {
		return "font";
	}

    @Override
    public Object toMessage() {
        FontProto.Builder model = FontProto.newBuilder();
        String family = getFamily(font);
        model.setFamily(DirectDrawUtils.windowsFonts.getProperty(family, family));
        model.setSize(font.getSize());
        model.setStyle(StyleProto.valueOf(font.getStyle()));
        return model.build();
    }

    @Override
    public int hashCode() {
        return font.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o == this ||
            o instanceof FontConst && font.equals(((FontConst) o).font);
    }

    public Font getFont() {
		return font;
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
