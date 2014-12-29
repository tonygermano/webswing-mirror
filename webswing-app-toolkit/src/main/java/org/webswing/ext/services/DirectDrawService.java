package org.webswing.ext.services;

import java.awt.Image;

public interface DirectDrawService {

    Image createImage(int width, int height);

    Image extractWebImage(Image webImage);

    byte[] buildWebImage(Image webImage);

    void resetCache();

	boolean isDirty(Image windowDecorationImage);
}
