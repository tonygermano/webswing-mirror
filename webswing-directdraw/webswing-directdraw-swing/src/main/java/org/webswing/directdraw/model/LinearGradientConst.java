package org.webswing.directdraw.model;

import java.awt.*;
import java.awt.MultipleGradientPaint.*;
import java.awt.geom.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class LinearGradientConst extends DrawConstant {

	public LinearGradientConst(DirectDraw context, LinearGradientPaint lgp) {
		super(context);
		LinearGradientProto.Builder model = LinearGradientProto.newBuilder();
		model.setXStart((int) lgp.getStartPoint().getX());
		model.setYStart((int) lgp.getStartPoint().getY());
		model.setXEnd((int) lgp.getEndPoint().getX());
		model.setYEnd((int) lgp.getEndPoint().getY());
		for (Color color : lgp.getColors()) {
			model.addColors(ColorConst.toRGBA(color));
		}
		for (float fraction : lgp.getFractions()) {
			model.addFractions(fraction);
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
		model.addColors(ColorConst.toRGBA(p.getColor1()));
		model.addColors(ColorConst.toRGBA(p.getColor2()));
		model.addFractions(0f);
		model.addFractions(1f);
		model.setRepeat(p.isCyclic() ? CyclicMethodProto.REPEAT : CyclicMethodProto.NO_CYCLE);
		this.message = model.build();
	}

	@Override
	public String getFieldName() {
		return "linearGrad";
	}

	public LinearGradientPaint getLinearGradientPaint() {
		LinearGradientProto glp = (LinearGradientProto) message;
		Color[] colors = glp.getColorsCount() > 0 ? new Color[glp.getColorsCount()] : new Color[0];
		for (int i = 0; i < glp.getColorsCount(); i++) {
			colors[i] = ColorConst.getColor(glp.getColors(i));
		}
		float[] fractions = glp.getFractionsCount() > 0 ? new float[glp.getFractionsCount()] : new float[0];
		for (int i = 0; i < glp.getFractionsCount(); i++) {
			fractions[i] = glp.getFractions(i);
		}
		Point2D end = new Point(glp.getXEnd(), glp.getYEnd());
		Point2D start = new Point(glp.getXStart(), glp.getYStart());
		CycleMethod cycleMethod = CycleMethod.valueOf(glp.getRepeat().name());
		return new LinearGradientPaint(start, end, fractions, colors, cycleMethod);
	}

}
