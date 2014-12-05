package org.webswing.directdraw.model;

import org.webswing.directdraw.proto.Directdraw.PointsProto;
import org.webswing.directdraw.proto.Directdraw.PointsProto.Builder;

public class PointsConst extends DrawConstant<PointsProto.Builder> {

    private PointsProto.Builder model;

    public PointsConst(int... points) {
        this.model = PointsProto.newBuilder();
        if (points != null) {
            for (int i = 0; i < points.length; i++) {
                this.model.addPoints(points[i]);
            }
        }
    }

    @Override
    protected Builder getProtoBuilder() {
        return model;
    }

    @Override
    public String getFieldName() {
        return "points";
    }

}
