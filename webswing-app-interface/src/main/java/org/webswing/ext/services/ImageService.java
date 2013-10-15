package org.webswing.ext.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.webswing.common.ImageServiceIfc;

import com.objectplanet.image.PngEncoder;

public class ImageService implements ImageServiceIfc {

    private static ImageService impl;
    private PngEncoder encoder;

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

}
