package org.webswing.directdraw.model;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

import com.google.protobuf.*;
import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class ImageConst extends DrawConstant {

	public ImageConst(DirectDraw context, BufferedImage img, Long hash) {
		super(context);
		ImageProto.Builder model = ImageProto.newBuilder();
		byte[] imgData = context.getServices().getPngImage(img);
		if (hash == null) {
			this.hash = context.getServices().computeHash(img);
		} else {
			this.hash = hash;
		}
		try {
			model.setData(ByteString.readFrom(new ByteArrayInputStream(imgData)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.message = model.build();
	}

	@Override
	public String getFieldName() {
		return "image";
	}
    
    public static BufferedImage getImage(ImageProto i) {
        try {
            return ImageIO.read(i.getData().newInput());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
