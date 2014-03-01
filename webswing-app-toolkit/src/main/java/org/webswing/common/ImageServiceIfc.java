package org.webswing.common;

import java.awt.image.BufferedImage;


public interface ImageServiceIfc {
    String encodeImage(BufferedImage window);

    byte[] getPngImage(BufferedImage image);
    
    WindowDecoratorThemeIfc getWindowDecorationTheme();


}
