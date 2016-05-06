package org.webswing.directdraw.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.model.DrawConstant;
import org.webswing.directdraw.model.DrawInstruction;
import org.webswing.directdraw.model.TransformConst;
import org.webswing.directdraw.proto.Directdraw.DrawInstructionProto.InstructionProto;

import sun.java2d.SunGraphics2D;
import sun.java2d.loops.FontInfo;

@SuppressWarnings("restriction")
public class DirectDrawUtils {

	public static final Properties windowsFonts = new Properties();
	public static final Properties webFonts = new Properties();
	private static final String DELIMITER = "|";
	private static SunGraphics2D sgHelper;

	static {
		sgHelper = (SunGraphics2D) new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();
		sgHelper.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		// logical fonts
		windowsFonts.setProperty("Dialog", "Arial");
		windowsFonts.setProperty("DialogInput", "Courier New");
		windowsFonts.setProperty("Serif", "Times New Roman");
		windowsFonts.setProperty("SansSerif", "Arial");
		windowsFonts.setProperty("Monospaced", "Courier New");
		webFonts.setProperty("Arial", "Dialog");
		webFonts.setProperty("Courier New", "DialogInput");
		webFonts.setProperty("Times New Roman", "Serif");
		webFonts.setProperty("Arial", "SansSerif");
		webFonts.setProperty("Courier New", "Monospaced");
	}

	public static FontInfo getFontInfo(Font font,AffineTransform transform){
		sgHelper.setFont(font);
		sgHelper.setTransform(transform);
		return sgHelper.getFontInfo();
	}

	/**
	 * there is a bug in the jdk 1.6 which makes Font.getAttributes() not work correctly. The method does not return all values. What we dow here is using the old JDK 1.5 method.
	 * 
	 * @param font font
	 *
	 * @return Attributes of font
	 */
	//	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<? extends Attribute, ?> getAttributes(Font font) {
		Map<Attribute, Object> result = new HashMap<Attribute, Object>(7, (float) 0.9);
		result.put(TextAttribute.TRANSFORM, font.getTransform());
		result.put(TextAttribute.FAMILY, font.getName());
		result.put(TextAttribute.SIZE, font.getSize2D());
		result.put(TextAttribute.WEIGHT, (font.getStyle() & Font.BOLD) != 0 ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
		result.put(TextAttribute.POSTURE, (font.getStyle() & Font.ITALIC) != 0 ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);
		result.put(TextAttribute.SUPERSCRIPT, 0);
		result.put(TextAttribute.WIDTH, 1f);
		return result;
	}

	public static BufferedImage createBufferedImage(Image image, ImageObserver observer, Color bkg) {
		if ((bkg == null) && ((image instanceof BufferedImage))) {
			return (BufferedImage) image;
		}
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(observer), image.getHeight(observer), bkg == null ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);

		Graphics g = bufferedImage.getGraphics();
		if (bkg == null) {
			g.drawImage(image, 0, 0, observer);
		} else {
			g.drawImage(image, 0, 0, bkg, observer);
		}
		return bufferedImage;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static BufferedImage createBufferedImage(RenderedImage img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		ColorModel cm = img.getColorModel();
		int width = img.getWidth();
		int height = img.getHeight();
		WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		Hashtable properties = new Hashtable();
		String[] keys = img.getPropertyNames();
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				properties.put(keys[i], img.getProperty(keys[i]));
			}
		}
		BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
		img.copyData(raster);
		return result;
	}

	public static void optimizeInstructions(DirectDraw ctx, List<DrawInstruction> instructions) {

		// step 1. group consequent transformations
		AffineTransform mergedTx = null;
		DrawConstant<?> mergedStroke = null;
		DrawConstant<?> mergedComposite = null;
		DrawConstant<?> mergedPaint = null;
		DrawConstant<?> mergedFont = null;
		DrawInstruction graphicsCreate = null;
		Map<Integer, DrawInstruction> graphicsCreateMap = new HashMap<Integer, DrawInstruction>();
		List<DrawInstruction> newInstructions = new ArrayList<DrawInstruction>();
		for (DrawInstruction current : instructions) {
			if (current.getInstruction().equals(InstructionProto.TRANSFORM)) {
				if (mergedTx == null) {
					mergedTx = ((TransformConst) current.getArg(0)).getValue();
				} else {
					mergedTx.concatenate(((TransformConst) current.getArg(0)).getValue());
				}
			} else if (current.getInstruction().equals(InstructionProto.SET_STROKE)) {
				mergedStroke = current.getArg(0);
			} else if (current.getInstruction().equals(InstructionProto.SET_COMPOSITE)) {
				mergedComposite = current.getArg(0);
			} else if (current.getInstruction().equals(InstructionProto.SET_PAINT)) {
				mergedPaint = current.getArg(0);
			} else if (current.getInstruction().equals(InstructionProto.SET_FONT)) {
				mergedFont = current.getArg(0);
			} else if (current.getInstruction().equals(InstructionProto.GRAPHICS_CREATE) || current.getInstruction().equals(InstructionProto.GRAPHICS_SWITCH)) {
				boolean isGraphicsCreateInst = current.getInstruction().equals(InstructionProto.GRAPHICS_CREATE);
				if (graphicsCreate != null) {
					graphicsCreate = createGraphics(ctx, graphicsCreate.getArg(0), mergedTx, mergedStroke, mergedComposite, mergedPaint, mergedFont);
					graphicsCreateMap.put(graphicsCreate.getArg(0).getId(), graphicsCreate);
				}
				graphicsCreate = isGraphicsCreateInst ? current : graphicsCreateMap.get(current.getArg(0).getId());
				if (graphicsCreate != null) {
					mergedTx = ((TransformConst) graphicsCreate.getArg(1)).getValue();
					mergedStroke = graphicsCreate.getArg(2);
					mergedComposite = graphicsCreate.getArg(3);
					mergedPaint = graphicsCreate.getArg(4);
					mergedFont = graphicsCreate.getArg(5);
				} else {// if graphics switch instruction and the create instruction already in result array
					// then add all status change for old graphics and add the switch inst
					setGraphicsStatus(ctx, newInstructions, mergedTx, mergedStroke, mergedComposite, mergedPaint, mergedFont);
					mergedTx = null;
					mergedStroke = null;
					mergedComposite = null;
					mergedPaint = null;
					mergedFont = null;
					newInstructions.add(current);
				}
			} else {
				if (graphicsCreate != null) {
					graphicsCreate = createGraphics(ctx, graphicsCreate.getArg(0), mergedTx, mergedStroke, mergedComposite, mergedPaint, mergedFont);
					newInstructions.add(graphicsCreate);
					graphicsCreateMap.remove(graphicsCreate.getArg(0).getId());
					graphicsCreate = null;
				} else {
					setGraphicsStatus(ctx, newInstructions, mergedTx, mergedStroke, mergedComposite, mergedPaint, mergedFont);
				}
				mergedTx = null;
				mergedStroke = null;
				mergedComposite = null;
				mergedPaint = null;
				mergedFont = null;
				newInstructions.add(current);
			}
		}
		// if transform is last instruction, it will be omitted from the result
		instructions.clear();
		instructions.addAll(newInstructions);
	}

	private static DrawInstruction createGraphics(DirectDraw ctx, DrawConstant<?> id, AffineTransform transform, DrawConstant<?> stroke, DrawConstant<?> composite, DrawConstant<?> paint, DrawConstant<?> font) {
		return ctx.getInstructionFactory().createGraphics(id, new TransformConst(ctx, transform), stroke, composite, paint, font);
	}

	private static void setGraphicsStatus(DirectDraw ctx, List<DrawInstruction> instructions, AffineTransform transform, DrawConstant<?> stroke, DrawConstant<?> composite, DrawConstant<?> paint, DrawConstant<?> font) {
		if (transform != null) {
			instructions.add(ctx.getInstructionFactory().transform(transform));
		}
		if (stroke != null) {
			instructions.add(new DrawInstruction(InstructionProto.SET_STROKE, stroke));
		}
		if (composite != null) {
			instructions.add(new DrawInstruction(InstructionProto.SET_COMPOSITE, composite));
		}
		if (paint != null) {
			instructions.add(new DrawInstruction(InstructionProto.SET_PAINT, paint));
		}
		if (font != null) {
			instructions.add(new DrawInstruction(InstructionProto.SET_FONT, font));
		}
	}

	public static int hashCode(double value) {
		long bits = Double.doubleToLongBits(value);
		return (int) (bits ^ (bits >>> 32));
	}

	public static int hashCode(float value) {
		return Float.floatToIntBits(value);
	}

	public static int hashCode(boolean value) {
		return value ? 1231 : 1237;
	}

	public static String fontInfoDescriptor(Font f,FontInfo fi) {
		StringBuilder sb= new StringBuilder(f.getFontName());
		sb.append(DELIMITER).append(f.getStyle());
		sb.append(DELIMITER).append(fi.devTx);
		sb.append(DELIMITER).append(fi.glyphTx);
		sb.append(DELIMITER).append(fi.pixelHeight);
		sb.append(DELIMITER);
		return sb.toString();
	}
	
	public static byte[] toPNG(DirectDraw ctx, byte[] gray, int w, int h) {
		int[] imagePixels = new int[w * h];
		for (int i = 0; i < imagePixels.length; i++) {
			imagePixels[i] = gray[i] << 24;
		}
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, w, h, imagePixels, 0, w);
		image.getRGB(0, 0, w, h, null, 0, w);
		return ctx.getServices().getPngImage(image);

	}
}
