package org.webswing.directdraw.util;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.text.AttributedCharacterIterator.*;
import java.util.*;
import java.util.List;

import org.webswing.directdraw.*;
import org.webswing.directdraw.model.*;
import org.webswing.directdraw.proto.Directdraw.DrawInstructionProto.*;

public class DirectDrawUtils {

	public static final Properties windowsFonts = new Properties();
	public static final Properties webFonts = new Properties();
	static {
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

	/**
	 * there is a bug in the jdk 1.6 which makes Font.getAttributes() not work correctly. The method does not return all values. What we dow here is using the old JDK 1.5 method.
	 * 
	 * @param font
	 *            font
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

	public static DrawConstant[] concat(DrawConstant[] a, DrawConstant[] b) {
		int aLen = a.length;
		int bLen = b.length;
		DrawConstant[] c = new DrawConstant[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

	public static void optimizeInstructions(DirectDraw ctx, List<DrawInstruction> instructions) {

		// step 1. group consequent transformations
		AffineTransform mergedTx = null;
		StrokeConst mergedStroke = null;
		CompositeConst mergedComposite = null;
		DrawConstant[] mergedPaint = null;
		DrawInstruction graphicsCreate = null;
		Map<Integer, DrawInstruction> graphicsCreateMap = new HashMap<Integer, DrawInstruction>();
		List<DrawInstruction> newInstructions = new ArrayList<DrawInstruction>();
		for (DrawInstruction current : instructions ) {
			if (current.getInstruction().equals(InstructionProto.TRANSFORM)) {
				if (mergedTx == null) {
					mergedTx = ((TransformConst) current.getArgs()[0]).getAffineTransform();
				} else {
					mergedTx.concatenate(((TransformConst) current.getArgs()[0]).getAffineTransform());
				}
			} else if (current.getInstruction().equals(InstructionProto.SET_STROKE)) {
				mergedStroke = ((StrokeConst) current.getArgs()[0]);
			} else if (current.getInstruction().equals(InstructionProto.SET_COMPOSITE)) {
				mergedComposite = ((CompositeConst) current.getArgs()[0]);
			} else if (current.getInstruction().equals(InstructionProto.SET_PAINT)) {
				mergedPaint = current.getArgs();
			} else if (current.getInstruction().equals(InstructionProto.GRAPHICS_CREATE) || current.getInstruction().equals(InstructionProto.GRAPHICS_SWITCH)) {
				boolean isGraphicsCreateInst = current.getInstruction().equals(InstructionProto.GRAPHICS_CREATE);
				if (graphicsCreate != null) {
					graphicsCreate.setArgs(concat(new DrawConstant[] { graphicsCreate.getArgs()[0], new TransformConst(ctx, mergedTx), mergedStroke, mergedComposite }, mergedPaint));
					graphicsCreateMap.put(graphicsCreate.getArgs()[0].getAddress(), graphicsCreate);
				}
				graphicsCreate = isGraphicsCreateInst ? current : graphicsCreateMap.get(current.getArgs()[0].getAddress());
				if (graphicsCreate != null) {
					mergedTx = ((TransformConst) graphicsCreate.getArgs()[1]).getAffineTransform();
					mergedStroke = ((StrokeConst) graphicsCreate.getArgs()[2]);
					mergedComposite = ((CompositeConst) graphicsCreate.getArgs()[3]);
					mergedPaint = Arrays.copyOfRange(graphicsCreate.getArgs(), 4, graphicsCreate.getArgs().length);
				} else {// if graphisc switch instruction and the create instruction already in result array
						// then add all status change for old graphics and add the switch inst
					setGraphicsStatus(ctx, newInstructions, mergedTx, mergedStroke, mergedComposite, mergedPaint);
					mergedTx = null;
					mergedStroke = null;
					mergedComposite = null;
					mergedPaint = null;
					newInstructions.add(current);
				}
			} else {
				if (graphicsCreate != null) {
					graphicsCreate.setArgs(concat(new DrawConstant[] { graphicsCreate.getArgs()[0], new TransformConst(ctx, mergedTx), mergedStroke, mergedComposite }, mergedPaint));
					newInstructions.add(graphicsCreate);
					graphicsCreateMap.remove(graphicsCreate.getArgs()[1].getAddress());
					graphicsCreate = null;
				} else {
					setGraphicsStatus(ctx, newInstructions, mergedTx, mergedStroke, mergedComposite, mergedPaint);
				}
				mergedTx = null;
				mergedStroke = null;
				mergedComposite = null;
				mergedPaint = null;
				newInstructions.add(current);
			}
		}
		// if transform is last instruction, it will be omited from the result
		instructions.clear();
		instructions.addAll(newInstructions);
	}

	private static void setGraphicsStatus(DirectDraw ctx, List<DrawInstruction> newInstructions, AffineTransform mergedTx, StrokeConst mergedStroke, CompositeConst mergedComposite, DrawConstant[] mergedPaint) {
		if (mergedTx != null) {
			if (!mergedTx.isIdentity()) {
				newInstructions.add(ctx.getInstructionFactory().transform(mergedTx));
			}
		}
		if (mergedStroke != null) {
			newInstructions.add(new DrawInstruction(InstructionProto.SET_STROKE, mergedStroke));
		}
		if (mergedComposite != null) {
			newInstructions.add(new DrawInstruction(InstructionProto.SET_COMPOSITE, mergedComposite));
		}
		if (mergedPaint != null) {
			newInstructions.add(new DrawInstruction(InstructionProto.SET_PAINT, mergedPaint));
		}
	}
}
