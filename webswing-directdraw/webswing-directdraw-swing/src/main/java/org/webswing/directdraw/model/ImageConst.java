package org.webswing.directdraw.model;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.proto.Directdraw.ImageProto;

import com.google.protobuf.ByteString;

public class ImageConst extends DrawConstant<ImageProto.Builder> {

    private static MessageDigest x;
    static {
        try {
            x = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private ImageProto.Builder model;
    private RenderedImage img;
    private byte[] hash;

    public ImageConst(BufferedImage img) {
        model = ImageProto.newBuilder();
        byte[] raw = null;
        Object buffer = img.getData().getDataBuffer();
        if (buffer instanceof DataBufferByte) {
            raw = ((DataBufferByte) buffer).getData();
        } else if (buffer instanceof DataBufferInt) {
            int[] rawint = ((DataBufferInt) buffer).getData();
            ByteBuffer byteBuffer = ByteBuffer.allocate(rawint.length * 4);
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            intBuffer.put(rawint);
            raw = byteBuffer.array();
        }
        hash = x.digest(raw);
        this.img = img;
    }

    @Override
    protected ImageProto.Builder getProtoBuilder() {
        return model;
    }

    @Override
    public String getFieldName() {
        return "image";
    }

    @Override
    public Object toMessage(DirectDraw dd) {
        byte[] pngImage = dd.getServices().getPngImage(img);
        try {
            model.setData(ByteString.readFrom(new ByteArrayInputStream(pngImage)));
            model.setHash(dd.getServices().encodeBytes(hash));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.toMessage(dd);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * Arrays.hashCode(hash);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        ImageConst other = (ImageConst) obj;
        if (!Arrays.equals(hash, other.hash))
            return false;
        return true;
    }

}
