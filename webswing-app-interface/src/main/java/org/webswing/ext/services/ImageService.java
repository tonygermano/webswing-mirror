package org.webswing.ext.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.webswing.common.ImageServiceIfc;
import org.webswing.common.WindowDecoratorThemeIfc;

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
            encoder = new PngEncoder(PngEncoder.COLOR_TRUECOLOR_ALPHA, PngEncoder.BEST_COMPRESSION);
        } catch (Exception e) {
            System.out.println("Library for fast image encoding not found. Download the library from http://objectplanet.com/pngencoder/");
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
            System.out.println("Writing image interupted:" + e.getMessage());
        }
        return null;
    }

    public WindowDecoratorThemeIfc getWindowDecorationTheme(){
        if(windowDecorationTheme==null){
            String implClassName=System.getProperty(WindowDecoratorThemeIfc.DECORATION_THEME_IMPL_PROP,WindowDecoratorThemeIfc.DECORATION_THEME_IMPL_DEFAULT);
            Class<?> implclass = null;
            try {
                implclass= this.getClass().getClassLoader().loadClass(implClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                try {
                    implclass=this.getClass().getClassLoader().loadClass(WindowDecoratorThemeIfc.DECORATION_THEME_IMPL_DEFAULT);
                } catch (ClassNotFoundException e1) {
                    System.err.println("Fatal error:Default decoration theme not found.");
                    System.exit(1);
                }
            }
            if(WindowDecoratorThemeIfc.class.isAssignableFrom(implclass)){
                try {
                    WindowDecoratorThemeIfc theme=(WindowDecoratorThemeIfc) implclass.newInstance();
                    this.windowDecorationTheme=theme;
                } catch (Exception e) {
                    e.printStackTrace();
                } 
            }else{
              System.err.println("Fatal error: Decoration theme not instance of WindowDecoratorThemeIfc:"+implclass.getCanonicalName());
              System.exit(1);
            }
        }
        return windowDecorationTheme;
    }
    
}
