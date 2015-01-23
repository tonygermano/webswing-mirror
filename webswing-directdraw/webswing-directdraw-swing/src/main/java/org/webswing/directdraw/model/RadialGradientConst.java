package org.webswing.directdraw.model;

import java.awt.Color;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.awt.RadialGradientPaint;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.CyclicMethodProto;
import org.webswing.directdraw.proto.Directdraw.RadialGradientProto;

public class RadialGradientConst extends DrawConstant {

	public RadialGradientConst(DirectDraw context, RadialGradientPaint rgp) {
		super(context);
		RadialGradientProto.Builder model = RadialGradientProto.newBuilder();
		model.setXCenter((int) rgp.getCenterPoint().getX());
		model.setYCenter((int) rgp.getCenterPoint().getY());
		model.setXFocus((int) rgp.getFocusPoint().getX());
		model.setYFocus((int) rgp.getFocusPoint().getY());
		model.setRadius((int) rgp.getRadius());
		for (Color c : rgp.getColors()) {
			model.addColors((c.getRGB() << 8) | c.getAlpha());
		}
		for (float f : rgp.getFractions()) {
			model.addFractions(f);
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
		Color[] colors = gp.getColorsCount() > 0 ? new Color[gp.getColorsCount()] : null;
		for (int i = 0; i < gp.getColorsCount(); i++) {
			colors[i] = ColorConst.getColor(gp.getColors(i));
		}
		float[] fractions = gp.getFractionsCount() > 0 ? new float[gp.getFractionsCount()] : null;
		for (int i = 0; i < gp.getFractionsCount(); i++) {
			fractions[i] = gp.getFractions(i);
		}
		CycleMethod cycleMethod = CycleMethod.valueOf(gp.getRepeat().name());
		return new RadialGradientPaint(center, radius, focus, fractions, colors, cycleMethod);
	}
}
