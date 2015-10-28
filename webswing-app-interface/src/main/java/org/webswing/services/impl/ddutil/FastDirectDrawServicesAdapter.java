package org.webswing.services.impl.ddutil;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import net.jpountz.xxhash.StreamingXXHash64;
import net.jpountz.xxhash.XXHashFactory;
import org.webswing.directdraw.DirectDrawServicesAdapter;
import org.webswing.directdraw.util.ImageConsumerAdapter;
import org.webswing.services.impl.ImageServiceImpl;

public class FastDirectDrawServicesAdapter extends DirectDrawServicesAdapter {

	XXHashFactory hashfactory = XXHashFactory.fastestInstance();
	long seed = 12345L;

	@Override
	public byte[] getPngImage(BufferedImage imageContent) {
		return ImageServiceImpl.getInstance().getPngImage(imageContent);
	}

	@Override
	public long getSignature(byte[] data) {
		return hashfactory.hash64().hash(data, 0, data.length, seed);
	}

	@Override
	public long computeHash(Image subImage) {
		final StreamingXXHash64 shash = hashfactory.newStreamingHash64(seed);
		final ByteBuffer byteBuffer = ByteBuffer.allocate(subImage.getWidth(null) * 4);
		final IntBuffer intBuffer = byteBuffer.asIntBuffer();
		ImageConsumerAdapter ic = new ImageConsumerAdapter() {
			@Override
			public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {
				intBuffer.rewind();
				intBuffer.put(pixels, off, scansize);
				shash.update(byteBuffer.array(), 0, scansize * 4);
			}
		};
		subImage.getSource().startProduction(ic);
		return shash.getValue();
	}

}
