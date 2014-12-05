package org.webswing.directdraw.model;

import java.awt.geom.Arc2D;

import org.webswing.directdraw.proto.Directdraw.ArcProto;
import org.webswing.directdraw.proto.Directdraw.ArcProto.ArcTypeProto;

public class ArcConst extends DrawConstant<ArcProto.Builder> {

    private ArcProto.Builder model;

    public ArcConst(Arc2D r) {
        model = ArcProto.newBuilder();
        model.setX((int) r.getX());
        model.setY((int) r.getY());
        model.setW((int) r.getWidth());
        model.setH((int) r.getHeight());
        model.setStart((int) r.getAngleStart());
        model.setExtent((int) r.getAngleExtent());
        model.setType(ArcTypeProto.valueOf(r.getArcType()));
    }

    @Override
    protected ArcProto.Builder getProtoBuilder() {
        return model;
    }

    @Override
    public String getFieldName() {
        return "arc";
    }

}
