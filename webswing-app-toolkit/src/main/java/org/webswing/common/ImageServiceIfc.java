package org.webswing.common;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;


public interface ImageServiceIfc {
    String encodeImage(BufferedImage window);

    byte[] getPngImage(BufferedImage image);
    
    WindowDecoratorThemeIfc getWindowDecorationTheme();
    
    Graphics2D createPDFGraphics(OutputStream out, Dimension size);
    void startPagePDFGraphics(Graphics2D pdfGrapthics,Dimension size);
    void endPagePDFGraphics(Graphics2D pdfGrapthics);
    void closePDFGraphics(Graphics2D pdfGrapthics);
    void moveFile(File srcFile,File destFile) throws IOException;
}
