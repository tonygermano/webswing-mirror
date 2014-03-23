package org.webswing.common;

import java.awt.image.BufferedImage;
import java.util.List;


public interface ImageServiceIfc {
    String encodeImage(BufferedImage window);

    byte[] getPngImage(BufferedImage image);
    
    WindowDecoratorThemeIfc getWindowDecorationTheme();
    
    byte[] generatePDF(List<BufferedImage> pages);
}
