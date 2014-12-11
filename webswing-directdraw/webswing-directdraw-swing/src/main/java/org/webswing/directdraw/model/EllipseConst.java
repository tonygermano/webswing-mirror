package org.webswing.directdraw.model;

import java.awt.geom.Ellipse2D;

import org.webswing.directdraw.proto.Directdraw.EllipseProto;

public class EllipseConst extends DrawConstant {


    public EllipseConst(Ellipse2D r) {
    	EllipseProto.Builder model = EllipseProto.newBuilder();
        model.setX((int) r.getX());
        model.setY((int) r.getY());
        model.setW((int) r.getWidth());
        model.setH((int) r.getHeight());
        this.message = model.build();
    }

    @Override
    public String getFieldName() {
        return "ellipse";
    }

}
