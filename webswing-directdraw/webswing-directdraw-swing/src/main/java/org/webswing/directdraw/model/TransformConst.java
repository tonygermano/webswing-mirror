package org.webswing.directdraw.model;

import java.awt.geom.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class TransformConst extends DrawConstant {

    private AffineTransform transform;
    
	public TransformConst(DirectDraw context, AffineTransform transform) {
		super(context);
		this.transform = transform;
	}

	@Override
	public String getFieldName() {
		return "transform";
	}

    @Override
    public Object toMessage() {
        TransformProto.Builder model = TransformProto.newBuilder();
        if (transform.getScaleX() != 1) {
            model.setM00((float) transform.getScaleX());
        }
        if (transform.getShearY() != 0) {
            model.setM10((float) transform.getShearY());
        }
        if (transform.getShearX() != 0) {
            model.setM01((float) transform.getShearX());
        }
        if (transform.getScaleY() != 1) {
            model.setM11((float) transform.getScaleY());
        }
        if (transform.getTranslateX() != 0) {
            model.setM02((float) transform.getTranslateX());
        }
        if (transform.getTranslateY() != 0) {
            model.setM12((float) transform.getTranslateY());
        }
        return model.build();
    }

    @Override
    public int hashCode() {
        return transform.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o == this ||
            o instanceof TransformConst && transform.equals(((TransformConst) o).transform);
    }

    public AffineTransform getAffineTransform() {
		return transform;
	}
}
