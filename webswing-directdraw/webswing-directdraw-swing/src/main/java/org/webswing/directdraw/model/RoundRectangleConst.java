package org.webswing.directdraw.model;

import java.awt.geom.RoundRectangle2D;

import org.webswing.directdraw.proto.Directdraw.RoundRectangleProto;

public class RoundRectangleConst extends DrawConstant {

    public RoundRectangleConst(RoundRectangle2D r) {
    	RoundRectangleProto.Builder model = RoundRectangleProto.newBuilder();
        model.setX((int) r.getX());
        model.setY((int) r.getY());
        model.setW((int) r.getWidth());
        model.setH((int) r.getHeight());
        model.setArcH((int) r.getArcHeight());
        model.setArcW((int) r.getArcWidth());
        this.message = model.build();
    }

    @Override
    public String getFieldName() {
        return "roundRectangle";
    }

}
