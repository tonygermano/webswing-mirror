package org.webswing.services.impl;

import java.awt.Image;

import org.webswing.directdraw.DirectDraw;
import org.webswing.ext.services.DirectDrawService;
import org.webswing.toolkit.directdraw.WebImage;

import com.google.protobuf.Message;

public class DirectDrawServiceImpl implements DirectDrawService {

    private static DirectDrawServiceImpl impl;
    private DirectDraw dd = new DirectDraw();

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
        return new WebImage(width, height);
    }

    @Override
    public String buildWebImage(Image webImage) {
        if (webImage instanceof WebImage) {
            Message m = ((WebImage) webImage).toMessage(dd);
            return dd.getServices().encodeBytes(m.toByteArray());
        }
        return null;
    }

    @Override
    public void resetCache() {
        dd.resetConstantCache();
    }

}
