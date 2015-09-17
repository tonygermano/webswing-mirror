package org.webswing.directdraw.model;

import java.awt.*;
import java.awt.MultipleGradientPaint.*;
import java.awt.geom.*;
import java.util.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class LinearGradientConst extends DrawConstant {

    private LinearGradientPaint linearGradientPaint;
    
	public LinearGradientConst(DirectDraw context, LinearGradientPaint linearGradientPaint) {
		super(context);
		this.linearGradientPaint = linearGradientPaint;
	}

	public LinearGradientConst(DirectDraw context, GradientPaint gradientPaint) {
		super(context);
        if (gradientPaint.getPoint1().equals(gradientPaint.getPoint2())) {
            /* such LinearGradientPaint should behave like a single color
             * maybe it would be good to create a separate class for GradientPaint
             * to avoid this workaround as canvas handles zero-vector gradients correctly
             */
            Color color = gradientPaint.getColor1();
            Point2D point = gradientPaint.getPoint1();
            this.linearGradientPaint = new LinearGradientPaint(point, new Point2D.Double(point.getX(), point.getY() + 1),
                new float[] {0f, 1f}, new Color[] {color, color},
                CycleMethod.NO_CYCLE);
        } else {
            this.linearGradientPaint = new LinearGradientPaint(gradientPaint.getPoint1(), gradientPaint.getPoint2(),
                new float[] {0f, 1f}, new Color[] {gradientPaint.getColor1(), gradientPaint.getColor2()},
                gradientPaint.isCyclic() ? CycleMethod.REPEAT : CycleMethod.NO_CYCLE);
        }
	}

	@Override
	public String getFieldName() {
		return "linearGrad";
	}

    @Override
    public Object toMessage() {
        LinearGradientProto.Builder model = LinearGradientProto.newBuilder();
        model.setXStart((int) linearGradientPaint.getStartPoint().getX());
        model.setYStart((int) linearGradientPaint.getStartPoint().getY());
        model.setXEnd((int) linearGradientPaint.getEndPoint().getX());
        model.setYEnd((int) linearGradientPaint.getEndPoint().getY());
        for (Color color : linearGradientPaint.getColors()) {
            model.addColors(ColorConst.toRGBA(color));
        }
        for (float fraction : linearGradientPaint.getFractions()) {
            model.addFractions(fraction);
        }
        model.setRepeat(CyclicMethodProto.valueOf(linearGradientPaint.getCycleMethod().name()));
        return model.build();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + linearGradientPaint.getStartPoint().hashCode();
        result = 31 * result + linearGradientPaint.getEndPoint().hashCode();
        result = 31 * result + Arrays.hashCode(linearGradientPaint.getColors());
        result = 31 * result + Arrays.hashCode(linearGradientPaint.getFractions());
        result = 31 * result + linearGradientPaint.getCycleMethod().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LinearGradientConst)) {
            return false;
        }
        LinearGradientConst other = (LinearGradientConst) o;
        return linearGradientPaint.getStartPoint().equals(other.linearGradientPaint.getStartPoint()) &&
            linearGradientPaint.getEndPoint().equals(other.linearGradientPaint.getEndPoint()) &&
            Arrays.equals(linearGradientPaint.getColors(), other.linearGradientPaint.getColors()) &&
            Arrays.equals(linearGradientPaint.getFractions(), other.linearGradientPaint.getFractions()) &&
            linearGradientPaint.getCycleMethod() == other.linearGradientPaint.getCycleMethod();
    }

    public LinearGradientPaint getLinearGradientPaint() {
		return linearGradientPaint;
	}
}
