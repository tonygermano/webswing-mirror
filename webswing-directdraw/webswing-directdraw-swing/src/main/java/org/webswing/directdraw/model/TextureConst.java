/**
 * <p>Title: FundCount, LLC</p>
 * <p>Description: FundCount project</p>
 * <p>Copyright: Copyright (c) 2001-2015 FundCount, LLC</p>
 * <p>Company: FundCount, LLC</p>
 */
package org.webswing.directdraw.model;

import java.awt.*;

import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class TextureConst extends DrawConstant {
    
    public TextureConst(DirectDraw context, TexturePaint t) {
        super(context);
        TextureProto.Builder model = TextureProto.newBuilder();
        model.setImage((ImageProto) new ImageConst(context, t.getImage(), null).message);
        model.setAnchor((RectangleProto) new RectangleConst(context, t.getAnchorRect()).message);
        this.message = model.build();
    }

    @Override
    public String getFieldName() {
        return "texture";
    }

    public TexturePaint getTexture() {
        TextureProto t = (TextureProto) message;
        return new TexturePaint(ImageConst.getImage(t.getImage()), RectangleConst.getRectangle(t.getAnchor()));
    }
}
