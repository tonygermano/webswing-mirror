package org.webswing.directdraw.model;

import java.awt.*;
import java.util.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;
import org.webswing.directdraw.util.*;

public class RadialGradientConst extends DrawConstant {

    private RadialGradientPaint radialGradientPaint;
    
	public RadialGradientConst(DirectDraw context, RadialGradientPaint radialGradientPaint) {
		super(context);
        this.radialGradientPaint = radialGradientPaint;
	}

	@Override
	public String getFieldName() {
		return "radialGrad";
	}

    @Override
    public Object toMessage() {
        RadialGradientProto.Builder model = RadialGradientProto.newBuilder();
        model.setXCenter((int) radialGradientPaint.getCenterPoint().getX());
        model.setYCenter((int) radialGradientPaint.getCenterPoint().getY());
        model.setXFocus((int) radialGradientPaint.getFocusPoint().getX());
        model.setYFocus((int) radialGradientPaint.getFocusPoint().getY());
        model.setRadius((int) radialGradientPaint.getRadius());
        for (Color color : radialGradientPaint.getColors()) {
            model.addColors(ColorConst.toRGBA(color));
        }
        for (float fraction : radialGradientPaint.getFractions()) {
            model.addFractions(fraction);
        }
        model.setRepeat(CyclicMethodProto.valueOf(radialGradientPaint.getCycleMethod().name()));
        return model.build();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + radialGradientPaint.getCenterPoint().hashCode();
        result = 31 * result + radialGradientPaint.getFocusPoint().hashCode();
        result = 31 * result + DirectDrawUtils.hashCode(radialGradientPaint.getRadius());
        result = 31 * result + Arrays.hashCode(radialGradientPaint.getColors());
        result = 31 * result + Arrays.hashCode(radialGradientPaint.getFractions());
        result = 31 * result + radialGradientPaint.getCycleMethod().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RadialGradientConst)) {
            return false;
        }
        RadialGradientConst other = (RadialGradientConst) o;
        return radialGradientPaint.getCenterPoint().equals(other.radialGradientPaint.getCenterPoint()) &&
            radialGradientPaint.getFocusPoint().equals(other.radialGradientPaint.getFocusPoint()) &&
            Float.floatToIntBits(radialGradientPaint.getRadius()) == Float.floatToIntBits(other.radialGradientPaint.getRadius()) &&
            Arrays.equals(radialGradientPaint.getColors(), other.radialGradientPaint.getColors()) &&
            Arrays.equals(radialGradientPaint.getFractions(), other.radialGradientPaint.getFractions()) &&
            radialGradientPaint.getCycleMethod() == other.radialGradientPaint.getCycleMethod();
    }

    public RadialGradientPaint getRadialGradientPaint() {
		return radialGradientPaint;
	}
}
