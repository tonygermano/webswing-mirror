package org.webswing.ext.services;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.webswing.common.ImageServiceIfc;
import org.webswing.common.WindowDecoratorThemeIfc;
import org.webswing.util.Logger;

import com.objectplanet.image.PngEncoder;

public class ImageService implements ImageServiceIfc {

    private static ImageService impl;
    private PngEncoder encoder;
    private WindowDecoratorThemeIfc windowDecorationTheme;

    public static ImageService getInstance() {
        if (impl == null) {
            impl = new ImageService();
        }
        return impl;
    }

    public ImageService() {
        try {
            ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            IIORegistry.getDefaultInstance().registerApplicationClasspathSpis();
            Thread.currentThread().setContextClassLoader(currentContextClassLoader);
            
            encoder = new PngEncoder(PngEncoder.COLOR_TRUECOLOR_ALPHA, PngEncoder.BEST_SPEED);
        } catch (Exception e) {
            Logger.warn("ImageService:Library for fast image encoding not found. Download the library from http://objectplanet.com/pngencoder/");
        }
    }

    public String encodeImage(BufferedImage window) {
        return Base64.encodeBase64String(getPngImage(window));
    }

    public byte[] getPngImage(BufferedImage imageContent) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (encoder != null) {
                encoder.encode(imageContent, baos);
            } else {
                ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
                ImageIO.write(imageContent, "png", ios);
            }
            byte[] result = baos.toByteArray();
            baos.close();
            return result;
        } catch (IOException e) {
            Logger.error("ImageService:Writing image interupted:" + e.getMessage(),e);
        }
        return null;
    }

    public WindowDecoratorThemeIfc getWindowDecorationTheme() {
        if (windowDecorationTheme == null) {
            String implClassName = System.getProperty(WindowDecoratorThemeIfc.DECORATION_THEME_IMPL_PROP, WindowDecoratorThemeIfc.DECORATION_THEME_IMPL_DEFAULT);
            Class<?> implclass = null;
            try {
                implclass = ImageService.class.getClassLoader().loadClass(implClassName);
            } catch (ClassNotFoundException e) {
                Logger.error("ImageService: WindowDecoratorTheme class not found",e);
                try {
                    implclass = ImageService.class.getClassLoader().loadClass(WindowDecoratorThemeIfc.DECORATION_THEME_IMPL_DEFAULT);
                } catch (ClassNotFoundException e1) {
                    Logger.fatal("ImageService: Fatal error:Default decoration theme not found.");
                    System.exit(1);
                }
            }
            if (WindowDecoratorThemeIfc.class.isAssignableFrom(implclass)) {
                try {
                    WindowDecoratorThemeIfc theme = (WindowDecoratorThemeIfc) implclass.newInstance();
                    this.windowDecorationTheme = theme;
                } catch (Exception e) {
                    Logger.fatal("ImageService: exception when creating instance of "+implclass.getCanonicalName(),e);
                    System.exit(1);
                }
            } else {
                Logger.fatal("ImageService: Fatal error: Decoration theme not instance of WindowDecoratorThemeIfc:" + implclass.getCanonicalName());
                System.exit(1);
            }
        }
        return windowDecorationTheme;
    }

    @Override
    public Graphics2D createPDFGraphics(OutputStream out, Dimension size) {
        PDFGraphics2D graphics = new PDFGraphics2D(out, size);
        graphics.setMultiPage(true);
        graphics.startExport();
        return graphics;
    }

    @Override
    public void startPagePDFGraphics(Graphics2D pdfGrapthics,Dimension size) {
        try {
            ((PDFGraphics2D)pdfGrapthics).openPage(size,"");
        } catch (IOException e) {
            ((PDFGraphics2D)pdfGrapthics).endExport();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endPagePDFGraphics(Graphics2D pdfGrapthics) {
        try {
            ((PDFGraphics2D)pdfGrapthics).closePage();
        } catch (IOException e) {
            ((PDFGraphics2D)pdfGrapthics).endExport();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closePDFGraphics(Graphics2D pdfGrapthics) {
        ((PDFGraphics2D)pdfGrapthics).endExport();
    }

    @Override
    public void moveFile(File srcFile, File destFile) throws IOException {
            FileUtils.moveFile(srcFile, destFile);
    }
}
