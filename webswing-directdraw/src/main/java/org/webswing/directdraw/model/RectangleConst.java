package org.webswing.directdraw.model;

import java.awt.geom.Rectangle2D;

import org.webswing.directdraw.proto.Directdraw.RectangleProto;
import org.webswing.directdraw.proto.Directdraw.RectangleProto.Builder;

public class RectangleConst extends DrawConstant<RectangleProto.Builder> {

    private RectangleProto.Builder model;

    public RectangleConst(Rectangle2D r) {
        model = RectangleProto.newBuilder();
        model.setX((int) r.getX());
        model.setY((int) r.getY());
        model.setW((int) r.getWidth());
        model.setH((int) r.getHeight());
    }

    @Override
    protected Builder getProtoBuilder() {
        return model;
    }

    @Override
    public String getFieldName() {
        return "rectangle";
    }

}
