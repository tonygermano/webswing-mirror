package org.webswing.directdraw;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.codec.binary.Base64;

public class DirectDrawServicesAdapter {

    public byte[] getPngImage(BufferedImage imageContent) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            ImageIO.write(imageContent, "png", ios);
            byte[] result = baos.toByteArray();
            baos.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getSignature(byte[] data) {
    	return Arrays.hashCode(data);
    }

    public String encodeBytes(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }
}
