package org.webswing.directdraw.model;

import java.awt.Color;

import org.webswing.directdraw.proto.Directdraw.ColorProto;
import org.webswing.directdraw.proto.Directdraw.ColorProto.Builder;

public class ColorConst extends DrawConstant<ColorProto.Builder> {

    ColorProto.Builder model;

    public ColorConst(Color c) {
        this.model = ColorProto.newBuilder();
        int rgba = (c.getRGB() << 8) | c.getAlpha();
        model.setRgba(rgba);
    }

    @Override
    protected Builder getProtoBuilder() {
        return model;
    }

    @Override
    public String getFieldName() {
        return "color";
    }

}
