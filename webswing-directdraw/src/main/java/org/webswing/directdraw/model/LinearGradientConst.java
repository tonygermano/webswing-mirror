package org.webswing.directdraw.model;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.LinearGradientPaint;

import org.webswing.directdraw.proto.Directdraw.CyclicMethodProto;
import org.webswing.directdraw.proto.Directdraw.LinearGradientProto;
import org.webswing.directdraw.proto.Directdraw.LinearGradientProto.Builder;

public class LinearGradientConst extends DrawConstant<LinearGradientProto.Builder> {

    private LinearGradientProto.Builder model;

    public LinearGradientConst(LinearGradientPaint lgp) {
        model = LinearGradientProto.newBuilder();
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
    }

    public LinearGradientConst(GradientPaint p) {
        model = LinearGradientProto.newBuilder();
        model.setXStart((int) p.getPoint1().getX());
        model.setYStart((int) p.getPoint1().getY());
        model.setXEnd((int) p.getPoint2().getX());
        model.setYEnd((int) p.getPoint2().getY());
        model.addColors((p.getColor1().getRGB() << 8) | p.getColor1().getAlpha());
        model.addColors((p.getColor2().getRGB() << 8) | p.getColor2().getAlpha());
        model.addFractions(0);
        model.addFractions(1);
        model.setRepeat(p.isCyclic() ? CyclicMethodProto.REPEAT : CyclicMethodProto.NO_CYCLE);
    }

    @Override
    protected Builder getProtoBuilder() {
        return model;
    }

    @Override
    public String getFieldName() {
        return "linearGrad";
    }

}
