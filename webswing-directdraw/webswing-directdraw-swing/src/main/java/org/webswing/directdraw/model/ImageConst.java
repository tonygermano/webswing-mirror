package org.webswing.directdraw.model;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import com.google.protobuf.*;
import org.webswing.directdraw.*;
import org.webswing.directdraw.proto.Directdraw.*;

public class ImageConst extends DrawConstant {

    private byte[] image;
    private long hash;
    
	public ImageConst(DirectDraw context, BufferedImage image) {
		super(context);
        this.image = context.getServices().getPngImage(image);
        this.hash = context.getServices().computeHash(image);
	}

	@Override
	public String getFieldName() {
		return "image";
	}

    @Override
    public Object toMessage() {
        ImageProto.Builder model = ImageProto.newBuilder();
        try {
            model.setData(ByteString.readFrom(new ByteArrayInputStream(image)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model.build();
    }

    @Override
    public int hashCode() {
        return (int) hash;
    }

    @Override
    public boolean equals(Object o) {
        return o == this ||
            o instanceof ImageConst && Arrays.equals(image, ((ImageConst) o).image);
    }
}
