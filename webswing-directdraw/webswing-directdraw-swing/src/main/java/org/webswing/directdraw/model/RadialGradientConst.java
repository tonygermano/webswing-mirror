package org.webswing.directdraw.model;

import java.awt.*;
import java.awt.MultipleGradientPaint.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class RadialGradientConst extends DrawConstant {

	public RadialGradientConst(DirectDraw context, RadialGradientPaint rgp) {
		super(context);
		RadialGradientProto.Builder model = RadialGradientProto.newBuilder();
		model.setXCenter((int) rgp.getCenterPoint().getX());
		model.setYCenter((int) rgp.getCenterPoint().getY());
		model.setXFocus((int) rgp.getFocusPoint().getX());
		model.setYFocus((int) rgp.getFocusPoint().getY());
		model.setRadius((int) rgp.getRadius());
		for (Color color : rgp.getColors()) {
			model.addColors(ColorConst.toRGBA(color));
		}
		for (float fraction : rgp.getFractions()) {
			model.addFractions(fraction);
		}
		model.setRepeat(CyclicMethodProto.valueOf(rgp.getCycleMethod().name()));
		this.message = model.build();
	}

	@Override
	public String getFieldName() {
		return "radialGrad";
	}

	public RadialGradientPaint getRadialGradientPaint() {
		RadialGradientProto gp = (RadialGradientProto) message;
		Point center = new Point(gp.getXCenter(), gp.getYCenter());
		float radius = gp.getRadius();
		Point focus = new Point(gp.getXFocus(), gp.getYFocus());
		Color[] colors = gp.getColorsCount() > 0 ? new Color[gp.getColorsCount()] : new Color[0];
		for (int i = 0; i < gp.getColorsCount(); i++) {
			colors[i] = ColorConst.getColor(gp.getColors(i));
		}
		float[] fractions = gp.getFractionsCount() > 0 ? new float[gp.getFractionsCount()] : new float[0];
		for (int i = 0; i < gp.getFractionsCount(); i++) {
			fractions[i] = gp.getFractions(i);
		}
		CycleMethod cycleMethod = CycleMethod.valueOf(gp.getRepeat().name());
		return new RadialGradientPaint(center, radius, focus, fractions, colors, cycleMethod);
	}
}
