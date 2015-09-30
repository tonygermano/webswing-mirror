package org.webswing.directdraw;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.webswing.directdraw.util.ImageConsumerAdapter;

public class DirectDrawServicesAdapter {

	public byte[] getPngImage(BufferedImage imageContent) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(imageContent, "png", baos);
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getSignature(byte[] data) {
		return Arrays.hashCode(data);
	}

	public String encodeBytes(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	public long computeHash(Image subImage) {
		ImageConsumerAdapter ic = new ImageConsumerAdapter() {
			@Override
			public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {
				hash = hash * 31 + Arrays.hashCode(pixels);
			}
		};
		subImage.getSource().startProduction(ic);
		return ic.getHash();
	}

}
