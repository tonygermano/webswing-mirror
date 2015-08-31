package org.webswing.directdraw.model;

import java.awt.geom.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class RoundRectangleConst extends DrawConstant {

    private RoundRectangle2D roundRectangle;
    
	public RoundRectangleConst(DirectDraw context, RoundRectangle2D roundRectangle) {
		super(context);
        this.roundRectangle = roundRectangle;
	}

	@Override
	public String getFieldName() {
		return "roundRectangle";
	}

    @Override
    public Object toMessage() {
        RoundRectangleProto.Builder model = RoundRectangleProto.newBuilder();
        model.setX((int) roundRectangle.getX());
        model.setY((int) roundRectangle.getY());
        model.setW((int) roundRectangle.getWidth());
        model.setH((int) roundRectangle.getHeight());
        model.setArcH((int) roundRectangle.getArcHeight());
        model.setArcW((int) roundRectangle.getArcWidth());
        return model.build();
    }

    @Override
    public int hashCode() {
        return roundRectangle.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o == this ||
            o instanceof RoundRectangleConst && roundRectangle.equals(((RoundRectangleConst) o).roundRectangle);
    }

    public RoundRectangle2D getRoundRectangle() {
		return roundRectangle;
	}
}
