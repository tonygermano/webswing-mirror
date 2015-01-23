package org.webswing.directdraw.model;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Point2D;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.CyclicMethodProto;
import org.webswing.directdraw.proto.Directdraw.LinearGradientProto;

public class LinearGradientConst extends DrawConstant {

	public LinearGradientConst(DirectDraw context, LinearGradientPaint lgp) {
		super(context);
		LinearGradientProto.Builder model = LinearGradientProto.newBuilder();
		model.setXStart((int) lgp.getStartPoint().getX());
		model.setYStart((int) lgp.getStartPoint().getY());
		model.setXEnd((int) lgp.getEndPoint().getX());
		model.setYEnd((int) lgp.getEndPoint().getY());
		for (Color c : lgp.getColors()) {
			model.addColors((c.getRGB() << 8) | c.getAlpha());
		}
		for (float f : lgp.getFractions()) {
			model.addFractions(f);
		}
		model.setRepeat(CyclicMethodProto.valueOf(lgp.getCycleMethod().name()));
		this.message = model.build();
	}

	public LinearGradientConst(DirectDraw context, GradientPaint p) {
		super(context);
		LinearGradientProto.Builder model = LinearGradientProto.newBuilder();
		model.setXStart((int) p.getPoint1().getX());
		model.setYStart((int) p.getPoint1().getY());
		model.setXEnd((int) p.getPoint2().getX());
		model.setYEnd((int) p.getPoint2().getY());
		model.addColors((p.getColor1().getRGB() << 8) | p.getColor1().getAlpha());
		model.addColors((p.getColor2().getRGB() << 8) | p.getColor2().getAlpha());
		model.addFractions(0);
		model.addFractions(1);
		model.setRepeat(p.isCyclic() ? CyclicMethodProto.REPEAT : CyclicMethodProto.NO_CYCLE);
		this.message = model.build();
	}

	@Override
	public String getFieldName() {
		return "linearGrad";
	}

	public LinearGradientPaint getLinearGradientPaint() {
		LinearGradientProto glp = (LinearGradientProto) message;
		Color[] colors = glp.getColorsCount() > 0 ? new Color[glp.getColorsCount()] : null;
		for (int i = 0; i < glp.getColorsCount(); i++) {
			colors[i] = ColorConst.getColor(glp.getColors(i));
		}
		float[] fractions = glp.getFractionsCount() > 0 ? new float[glp.getFractionsCount()] : null;
		for (int i = 0; i < glp.getFractionsCount(); i++) {
			fractions[i] = glp.getFractions(i);
		}
		Point2D end = new Point(glp.getXEnd(), glp.getYEnd());
		Point2D start = new Point(glp.getXStart(), glp.getYStart());
		CycleMethod cycleMethod = CycleMethod.valueOf(glp.getRepeat().name());

		return new LinearGradientPaint(start, end, fractions, colors, cycleMethod);
	}

}
