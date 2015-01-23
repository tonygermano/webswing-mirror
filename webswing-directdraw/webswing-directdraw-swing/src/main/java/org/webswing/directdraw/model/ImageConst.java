package org.webswing.directdraw.model;

import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.ImageProto;

import com.google.protobuf.ByteString;

public class ImageConst extends DrawConstant {

	private long hash;

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
	protected long getHash() {
		return hash;
	}

	@Override
	public String getFieldName() {
		return "image";
	}

	public TexturePaint getTexturePaint(Rectangle2D anchor) {
		ImageProto ip = (ImageProto) message;
		BufferedImage img = null;
		try {
			img = ImageIO.read(ip.getData().newInput());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new TexturePaint(img, anchor);
	}

}
