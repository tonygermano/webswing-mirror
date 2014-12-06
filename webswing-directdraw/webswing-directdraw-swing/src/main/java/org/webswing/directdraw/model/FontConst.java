package org.webswing.directdraw.model;

import java.awt.Font;

import org.webswing.directdraw.proto.Directdraw.FontProto;
import org.webswing.directdraw.proto.Directdraw.FontProto.Builder;
import org.webswing.directdraw.proto.Directdraw.FontProto.StyleProto;
import org.webswing.directdraw.util.DirectDrawUtils;

public class FontConst extends DrawConstant<FontProto.Builder> {

    private FontProto.Builder model;

    public FontConst(Font f) {
        model = FontProto.newBuilder();
        model.setFamily(DirectDrawUtils.windowsFonts.getProperty(f.getFamily(), f.getFamily()));
        model.setSize(f.getSize());
        model.setStyle(StyleProto.valueOf(f.getStyle()));
    }

    @Override
    protected Builder getProtoBuilder() {
        return model;
    }

    @Override
    public String getFieldName() {
        return "font";
    }
}
