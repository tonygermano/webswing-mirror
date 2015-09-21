package org.webswing.services.impl.ddutil;

import java.awt.image.*;
import java.nio.*;

import net.jpountz.xxhash.*;
import org.webswing.directdraw.*;
import org.webswing.directdraw.util.*;
import org.webswing.services.impl.*;

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
	public long computeHash(BufferedImage subImage) {
		final StreamingXXHash64 shash = hashfactory.newStreamingHash64(seed);
		final ByteBuffer byteBuffer = ByteBuffer.allocate(subImage.getWidth() * 4);
		final IntBuffer intBuffer = byteBuffer.asIntBuffer();
		ImageConsumerAdapter ic = new ImageConsumerAdapter() {
			@Override
			public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {
				intBuffer.rewind();
				intBuffer.put(pixels);
				shash.update(byteBuffer.array(), 0, pixels.length * 4);
			}
		};
		subImage.getSource().startProduction(ic);
		return shash.getValue();
	}

}
