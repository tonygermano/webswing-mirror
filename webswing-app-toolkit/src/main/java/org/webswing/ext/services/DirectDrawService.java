package org.webswing.ext.services;

import java.awt.Image;

public interface DirectDrawService {

    Image createImage(int width, int height);

    String buildWebImage(Image webImage);

    void resetCache();
}
