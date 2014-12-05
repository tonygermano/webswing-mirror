package org.webswing.directdraw.model;

import java.awt.geom.Ellipse2D;

import org.webswing.directdraw.proto.Directdraw.EllipseProto;

public class EllipseConst extends DrawConstant<EllipseProto.Builder> {

    private EllipseProto.Builder model;

    public EllipseConst(Ellipse2D r) {
        model = EllipseProto.newBuilder();
        model.setX((int) r.getX());
        model.setY((int) r.getY());
        model.setW((int) r.getWidth());
        model.setH((int) r.getHeight());
    }

    @Override
    protected EllipseProto.Builder getProtoBuilder() {
        return model;
    }

    @Override
    public String getFieldName() {
        return "ellipse";
    }

}
