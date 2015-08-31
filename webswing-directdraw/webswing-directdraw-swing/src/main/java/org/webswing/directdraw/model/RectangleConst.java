package org.webswing.directdraw.model;

import java.awt.geom.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class RectangleConst extends DrawConstant {

    private Rectangle2D rectangle;
    
	public RectangleConst(DirectDraw context, Rectangle2D rectangle) {
		super(context);
        this.rectangle = rectangle;
	}

	@Override
	public String getFieldName() {
		return "rectangle";
	}

    @Override
    public Object toMessage() {
        RectangleProto.Builder model = RectangleProto.newBuilder();
        model.setX((int) rectangle.getX());
        model.setY((int) rectangle.getY());
        model.setW((int) rectangle.getWidth());
        model.setH((int) rectangle.getHeight());
        return model.build();
    }

    @Override
    public int hashCode() {
        return rectangle.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o == this ||
            o instanceof RectangleConst && rectangle.equals(((RectangleConst) o).rectangle);
    }

    public Rectangle2D getRectangle() {
        return rectangle;
	}
}
