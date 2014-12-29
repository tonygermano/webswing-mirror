package org.webswing.services.impl;

import java.awt.Image;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.toolkit.WebImage;
import org.webswing.ext.services.DirectDrawService;
import org.webswing.services.impl.ddutil.FastDirectDrawServicesAdapter;

public class DirectDrawServiceImpl implements DirectDrawService {

	private static DirectDrawServiceImpl impl;
	protected DirectDraw dd = new DirectDraw(new FastDirectDrawServicesAdapter());

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
		resetCache();
		if (webImage instanceof WebImage) {
			return ((WebImage) webImage).toMessage(dd).toByteArray();
		}
		return null;
	}

	@Override
	public boolean isDirty(Image webImage) {
		if (webImage instanceof WebImage) {
			return ((WebImage) webImage).isDirty();
		}
		return false;
	}

}
