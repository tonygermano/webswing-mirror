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

	public static void optimizeInstructions(DirectDraw ctx, List<DrawInstruction> instructions) {

		// step 1. group consequent transformations
		AffineTransform mergedTx = null;
		DrawConstant mergedStroke = null;
		DrawConstant mergedComposite = null;
		DrawConstant mergedPaint = null;
		DrawConstant mergedFont = null;
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
				mergedStroke = current.getArgs()[0];
			} else if (current.getInstruction().equals(InstructionProto.SET_COMPOSITE)) {
				mergedComposite = current.getArgs()[0];
			} else if (current.getInstruction().equals(InstructionProto.SET_PAINT)) {
                mergedPaint = current.getArgs()[0];
            } else if (current.getInstruction().equals(InstructionProto.SET_FONT)) {
                mergedFont = current.getArgs()[0];
			} else if (current.getInstruction().equals(InstructionProto.GRAPHICS_CREATE) || current.getInstruction().equals(InstructionProto.GRAPHICS_SWITCH)) {
				boolean isGraphicsCreateInst = current.getInstruction().equals(InstructionProto.GRAPHICS_CREATE);
				if (graphicsCreate != null) {
					graphicsCreate.setArgs(new DrawConstant[] { graphicsCreate.getArgs()[0], new TransformConst(ctx, mergedTx), mergedStroke, mergedComposite, mergedPaint, mergedFont });
					graphicsCreateMap.put(graphicsCreate.getArgs()[0].getId(), graphicsCreate);
				}
				graphicsCreate = isGraphicsCreateInst ? current : graphicsCreateMap.get(current.getArgs()[0].getId());
				if (graphicsCreate != null) {
					mergedTx = ((TransformConst) graphicsCreate.getArgs()[1]).getAffineTransform();
					mergedStroke = graphicsCreate.getArgs()[2];
					mergedComposite = graphicsCreate.getArgs()[3];
					mergedPaint = graphicsCreate.getArgs()[4];
					mergedFont = graphicsCreate.getArgs()[5];
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
					graphicsCreate.setArgs(new DrawConstant[] { graphicsCreate.getArgs()[0], new TransformConst(ctx, mergedTx), mergedStroke, mergedComposite , mergedPaint, mergedFont });
					newInstructions.add(graphicsCreate);
					graphicsCreateMap.remove(graphicsCreate.getArgs()[0].getId());
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

	private static void setGraphicsStatus(DirectDraw ctx, List<DrawInstruction> newInstructions, AffineTransform mergedTx, DrawConstant mergedStroke, DrawConstant mergedComposite, DrawConstant mergedPaint, DrawConstant mergedFont) {
		if (mergedTx != null) {
            newInstructions.add(ctx.getInstructionFactory().transform(mergedTx));
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
        if (mergedFont != null) {
            newInstructions.add(new DrawInstruction(InstructionProto.SET_FONT, mergedFont));
        }
	}

    public static int hashCode(double value) {
        long bits = Double.doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
    }

    public static int hashCode(float value) {
        return Float.floatToIntBits(value);
    }
}
