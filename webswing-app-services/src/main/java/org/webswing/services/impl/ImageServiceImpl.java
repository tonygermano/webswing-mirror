package org.webswing.services.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;

import org.apache.commons.codec.binary.Base64;
import org.webswing.common.WindowDecoratorTheme;
import org.webswing.ext.services.ImageService;
import org.webswing.toolkit.util.Util;
import org.webswing.util.AppLogger;

import com.objectplanet.image.PngEncoder;

public class ImageServiceImpl implements ImageService {

	private static ImageServiceImpl impl;
	private Map<Integer, PngEncoder> encoders;
	private ByteArrayOutputStream encoderBuffer = new ByteArrayOutputStream();
	private WindowDecoratorTheme windowDecorationTheme;

	public static ImageServiceImpl getInstance() {
		if (impl == null) {
			impl = new ImageServiceImpl();
		}
		return impl;
	}

	public ImageServiceImpl() {
		try {
			ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			IIORegistry.getDefaultInstance().registerApplicationClasspathSpis();
			Thread.currentThread().setContextClassLoader(currentContextClassLoader);
			encoders = new HashMap<Integer, PngEncoder>();
		} catch (Exception e) {
			AppLogger.warn("ImageService:Library for fast image encoding not found. Download the library from http://objectplanet.com/pngencoder/");
		}
	}

	public byte[] getPngImage(BufferedImage image) {
		try {
			PngEncoder encoder = getEncoder(image);
			if (encoder != null) {
				byte[] result;
				synchronized (encoderBuffer) {
					encoder.encode(image, encoderBuffer);
					result = encoderBuffer.toByteArray();
					encoderBuffer.reset();
				}
				return result;
			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image, "png", baos);
				return baos.toByteArray();
			}
		} catch (IOException e) {
			AppLogger.error("ImageService:Writing image interrupted:" + e.getMessage(), e);
		}
		return null;
	}

	public PngEncoder getEncoder(BufferedImage image) {
		int type;
		switch (image.getType()) {
		case BufferedImage.TYPE_BYTE_BINARY:
		case BufferedImage.TYPE_BYTE_INDEXED:
			type = image.getColorModel().hasAlpha() ? PngEncoder.COLOR_INDEXED_ALPHA : PngEncoder.COLOR_INDEXED;
			break;

		case BufferedImage.TYPE_INT_RGB:
		case BufferedImage.TYPE_INT_ARGB:
		case BufferedImage.TYPE_INT_ARGB_PRE:
		case BufferedImage.TYPE_3BYTE_BGR:
		case BufferedImage.TYPE_4BYTE_ABGR:
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			type = image.getColorModel().hasAlpha() ? PngEncoder.COLOR_TRUECOLOR_ALPHA : PngEncoder.COLOR_TRUECOLOR;
			break;

		default:
			return null;
		}
		PngEncoder encoder = encoders.get(type);
		if (encoder == null) {
			encoders.put(type, encoder = new PngEncoder(type, PngEncoder.BEST_SPEED));
		}
		return encoder;
	}

	public WindowDecoratorTheme getWindowDecorationTheme() {
		if (windowDecorationTheme == null) {
			this.windowDecorationTheme = Util.instantiateClass(WindowDecoratorTheme.class, WindowDecoratorTheme.DECORATION_THEME_IMPL_PROP, WindowDecoratorTheme.DECORATION_THEME_IMPL_DEFAULT, ImageServiceImpl.class.getClassLoader());
			if (windowDecorationTheme == null) {
				System.exit(1);
			}
		}
		return windowDecorationTheme;
	}

	@Override
	public Image readFromDataUrl(String dataUrl) {
		String encodingPrefix = "base64,";
		int contentStartIndex = dataUrl.indexOf(encodingPrefix) + encodingPrefix.length();
		byte[] imageData = Base64.decodeBase64(dataUrl.substring(contentStartIndex));

		// create BufferedImage from byteArray
		BufferedImage inputImage = null;
		try {
			inputImage = ImageIO.read(new ByteArrayInputStream(imageData));
		} catch (IOException e) {
			AppLogger.error("ImageService: reading image from dataUrl failed", e);
		}
		return inputImage;
	}
}
