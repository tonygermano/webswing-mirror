package org.webswing.directdraw.model;

import java.awt.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;
import org.webswing.directdraw.proto.Directdraw.StrokeProto.*;

public class StrokeConst extends DrawConstant {

    private BasicStroke stroke;
    
	public StrokeConst(DirectDraw context, BasicStroke stroke) {
		super(context);
        this.stroke = stroke;
	}

	@Override
	public String getFieldName() {
		return "stroke";
	}

    @Override
    public Object toMessage() {
        StrokeProto.Builder model = StrokeProto.newBuilder();
        model.setWidth(stroke.getLineWidth());
        model.setMiterLimit(stroke.getMiterLimit());
        model.setJoin(StrokeJoinProto.valueOf(stroke.getLineJoin()));
        model.setCap(StrokeCapProto.valueOf(stroke.getEndCap()));
        model.setDashOffset(stroke.getDashPhase());
        if (stroke.getDashArray() != null && stroke.getDashArray().length > 0) {
            for (float d : stroke.getDashArray()) {
                model.addDash(d);
            }
        }
        return model.build();
    }

    @Override
    public int hashCode() {
        return stroke.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o == this ||
            o instanceof StrokeConst && stroke.equals(((StrokeConst) o).stroke);
    }

    public BasicStroke getStroke() {
		return stroke;
	}
}
