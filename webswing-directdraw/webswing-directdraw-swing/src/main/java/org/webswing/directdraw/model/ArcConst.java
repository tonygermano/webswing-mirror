package org.webswing.directdraw.model;

import java.awt.geom.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;
import org.webswing.directdraw.proto.Directdraw.ArcProto.*;

public class ArcConst extends DrawConstant {

    private Arc2D arc;
    
	public ArcConst(DirectDraw context, Arc2D arc) {
		super(context);
        this.arc = arc;
	}

	@Override
	public String getFieldName() {
		return "arc";
	}

    @Override
    public Object toMessage() {
        ArcProto.Builder model = ArcProto.newBuilder();
        model.setX((int) arc.getX());
        model.setY((int) arc.getY());
        model.setW((int) arc.getWidth());
        model.setH((int) arc.getHeight());
        model.setStart((int) arc.getAngleStart());
        model.setExtent((int) arc.getAngleExtent());
        model.setType(ArcTypeProto.valueOf(arc.getArcType()));
        return model.build();
    }

    @Override
    public int hashCode() {
        return arc.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o == this ||
            o instanceof ArcConst && arc.equals(((ArcConst) o).arc);
    }

    public Arc2D getArc() {
		return arc;
	}
}
