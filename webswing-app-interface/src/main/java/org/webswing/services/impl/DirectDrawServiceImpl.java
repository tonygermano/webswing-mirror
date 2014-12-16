package org.webswing.services.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;

import net.jpountz.xxhash.XXHashFactory;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.DirectDrawServicesAdapter;
import org.webswing.directdraw.toolkit.WebImage;
import org.webswing.ext.services.DirectDrawService;

public class DirectDrawServiceImpl implements DirectDrawService {

    private static DirectDrawServiceImpl impl;
    private DirectDraw dd = new DirectDraw(new DirectDrawServicesAdapter(){
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
    });

    public static DirectDrawServiceImpl getInstance() {
        if (impl == null) {
            impl = new DirectDrawServiceImpl();
        }
        return impl;
    }

    private DirectDrawServiceImpl() {
    }

    @Override
    public Image createImage(int width, int height) {
        return dd.createImage(width, height);
    }

    @Override
    public Image extractWebImage(Image webImage) {
        if (webImage instanceof WebImage) {
            return ((WebImage) webImage).extractReadOnlyWebImage();
        }
        return null;
    }

    @Override
    public void resetCache() {
        dd.resetConstantCache();
    }

	@Override
	public byte[] buildWebImage(Image webImage) {
		 if (webImage instanceof WebImage) {
	            return ((WebImage) webImage).toMessage(dd).toByteArray();
	        }
		return null;
	}

}
