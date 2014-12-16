package org.webswing.directdraw.model;

import java.awt.geom.Arc2D;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.ArcProto;
import org.webswing.directdraw.proto.Directdraw.ArcProto.ArcTypeProto;

public class ArcConst extends DrawConstant{

    public ArcConst(DirectDraw context,Arc2D r) {
    	super(context);
    	ArcProto.Builder model = ArcProto.newBuilder();
        model.setX((int) r.getX());
        model.setY((int) r.getY());
        model.setW((int) r.getWidth());
        model.setH((int) r.getHeight());
        model.setStart((int) r.getAngleStart());
        model.setExtent((int) r.getAngleExtent());
        model.setType(ArcTypeProto.valueOf(r.getArcType()));
        this.message=model.build();
    }

    @Override
    public String getFieldName() {
        return "arc";
    }

}
