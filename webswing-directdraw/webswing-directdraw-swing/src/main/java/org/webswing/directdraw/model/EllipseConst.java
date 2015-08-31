package org.webswing.directdraw.model;

import java.awt.geom.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class EllipseConst extends DrawConstant {

    private Ellipse2D ellipse;
    
	public EllipseConst(DirectDraw context, Ellipse2D ellipse) {
		super(context);
        this.ellipse = ellipse;
	}

	@Override
	public String getFieldName() {
		return "ellipse";
	}

    @Override
    public Object toMessage() {
        EllipseProto.Builder model = EllipseProto.newBuilder();
        model.setX((int) ellipse.getX());
        model.setY((int) ellipse.getY());
        model.setW((int) ellipse.getWidth());
        model.setH((int) ellipse.getHeight());
        return model.build();
    }

    @Override
    public int hashCode() {
        return ellipse.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o == this ||
            o instanceof EllipseConst && ellipse.equals(((EllipseConst) o).ellipse);
    }

    public Ellipse2D getEllipse() {
		return ellipse;
	}
}
