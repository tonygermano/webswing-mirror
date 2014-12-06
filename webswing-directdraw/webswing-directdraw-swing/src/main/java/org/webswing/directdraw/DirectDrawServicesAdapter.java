package org.webswing.directdraw;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.codec.binary.Base64;

public class DirectDrawServicesAdapter {

    public byte[] getPngImage(RenderedImage imageContent) {
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

    public byte[] getSignature(byte[] data) {
        try {
            return MessageDigest.getInstance("MD5").digest(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String encodeBytes(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }
}
