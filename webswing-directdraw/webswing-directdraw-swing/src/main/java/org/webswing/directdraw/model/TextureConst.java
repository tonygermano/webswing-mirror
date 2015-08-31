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
    
    private TexturePaint texturePaint;
    private ImageConst texture;
    
    public TextureConst(DirectDraw context, TexturePaint texturePaint) {
        super(context);
        this.texturePaint = texturePaint;
        this.texture = new ImageConst(getContext(), texturePaint.getImage());
    }

    @Override
    public String getFieldName() {
        return "texture";
    }

    @Override
    public Object toMessage() {
        TextureProto.Builder model = TextureProto.newBuilder();
        model.setImage((ImageProto) texture.toMessage());
        model.setAnchor((RectangleProto) new RectangleConst(getContext(), texturePaint.getAnchorRect()).toMessage());
        return model.build();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + texture.hashCode();
        result = 31 * result + texturePaint.getAnchorRect().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TextureConst)) {
            return false;
        }
        TextureConst other = (TextureConst) o;
        return texture.equals(other.texture) &&
            texturePaint.getAnchorRect().equals(other.texturePaint.getAnchorRect());
    }

    public TexturePaint getTexturePaint() {
        return texturePaint;
    }
}
