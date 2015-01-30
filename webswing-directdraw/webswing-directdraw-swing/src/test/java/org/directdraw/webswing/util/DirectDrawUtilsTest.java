package org.directdraw.webswing.util;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.model.ColorConst;
import org.webswing.directdraw.model.CompositeConst;
import org.webswing.directdraw.model.DrawInstruction;
import org.webswing.directdraw.model.StrokeConst;
import org.webswing.directdraw.model.TransformConst;
import org.webswing.directdraw.toolkit.DrawInstructionFactory;
import org.webswing.directdraw.toolkit.WebGraphics;
import org.webswing.directdraw.toolkit.WebImage;
import org.webswing.directdraw.util.DirectDrawUtils;

public class DirectDrawUtilsTest {

	DirectDraw dd;
	private DrawInstructionFactory f;

	@Before
	public void setUp() throws Exception {
		dd = new DirectDraw();
		f = dd.getInstructionFactory();
	}

	@Test
	public void testGroupedGraphicsCreate() {
		WebImage webImage = new WebImage(dd, 10, 10);
		List<DrawInstruction> inst = new ArrayList<DrawInstruction>();

		inst.add(f.createGraphics((WebGraphics) webImage.getGraphics()));// merged in one graphics create
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		inst.add(f.setPaint(Color.black));
		inst.add(f.setPaint(Color.blue));
		inst.add(f.setComposite(AlphaComposite.Dst));
		inst.add(f.setComposite(AlphaComposite.Src));
		inst.add(f.setStroke(new BasicStroke(1)));
		inst.add(f.setStroke(new BasicStroke(2)));

		inst.add(f.draw(new Rectangle(), null));// draw

		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));// ingored
		inst.add(f.setPaint(Color.black));
		inst.add(f.setComposite(AlphaComposite.Dst));
		inst.add(f.setStroke(new BasicStroke(1)));

		DirectDrawUtils.optimizeInstructions(dd, inst);
		assertEquals("Count not valid", 2, inst.size());
		assertEquals("Transform not expected", new AffineTransform(1, 0, 0, 1, 20, 20), ((TransformConst) inst.get(0).getArgs()[1]).getAffineTransform());
		assertEquals("Stroke not expected", (int) 2, (int) ((BasicStroke) ((StrokeConst) inst.get(0).getArgs()[2]).getStroke()).getLineWidth());
		assertEquals("Composite not expected", AlphaComposite.Src, ((CompositeConst) inst.get(0).getArgs()[3]).getComposite());
		assertEquals("Color not expected", Color.blue, ((ColorConst) inst.get(0).getArgs()[4]).getColor());
	}

	@Test
	public void testUnusedGraphicsCreate() {
		WebImage webImage = new WebImage(dd, 10, 10);
		List<DrawInstruction> inst = new ArrayList<DrawInstruction>();

		inst.add(f.createGraphics((WebGraphics) webImage.getGraphics()));// ignored because not used
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		inst.add(f.setPaint(Color.black));
		inst.add(f.setPaint(Color.blue));
		inst.add(f.setComposite(AlphaComposite.Dst));
		inst.add(f.setComposite(AlphaComposite.Src));
		inst.add(f.setStroke(new BasicStroke(1)));
		inst.add(f.setStroke(new BasicStroke(2)));

		inst.add(f.createGraphics((WebGraphics) webImage.getGraphics()));// merged in one graphics create
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		inst.add(f.setPaint(Color.blue));
		inst.add(f.setComposite(AlphaComposite.Dst));
		inst.add(f.setStroke(new BasicStroke(2)));

		inst.add(f.draw(new Rectangle(), null));// draw

		inst.add(f.createGraphics((WebGraphics) webImage.getGraphics()));// ingored
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		inst.add(f.setPaint(Color.black));
		inst.add(f.setComposite(AlphaComposite.Dst));
		inst.add(f.setStroke(new BasicStroke(1)));

		DirectDrawUtils.optimizeInstructions(dd, inst);
		assertEquals("Count not valid", 2, inst.size());
		assertEquals("Transform not expected", new AffineTransform(1, 0, 0, 1, 10, 10), ((TransformConst) inst.get(0).getArgs()[1]).getAffineTransform());
		assertEquals("Stroke not expected", (int) 2, (int) ((BasicStroke) ((StrokeConst) inst.get(0).getArgs()[2]).getStroke()).getLineWidth());
		assertEquals("Composite not expected", AlphaComposite.Dst, ((CompositeConst) inst.get(0).getArgs()[3]).getComposite());
		assertEquals("Color not expected", Color.blue, ((ColorConst) inst.get(0).getArgs()[4]).getColor());
	}

	@Test
	public void testMergedPaints() {
		WebImage webImage = new WebImage(dd, 10, 10);
		List<DrawInstruction> inst = new ArrayList<DrawInstruction>();

		inst.add(f.createGraphics((WebGraphics) webImage.getGraphics()));// graphics

		inst.add(f.draw(new Rectangle(), null));// draw

		inst.add(f.setPaint(new TexturePaint(new BufferedImage(10, 10, BufferedImage.TYPE_3BYTE_BGR), new Rectangle())));// merged
		inst.add(f.setPaint(Color.green));

		inst.add(f.draw(new Rectangle(), null));// draw

		DirectDrawUtils.optimizeInstructions(dd, inst);
		assertEquals("Count not valid", 4, inst.size());
		assertEquals("Color not expected", Color.green, ((ColorConst) inst.get(2).getArgs()[0]).getColor());
	}

	@Test
	public void testMergedTransforms() {
		WebImage webImage = new WebImage(dd, 10, 10);
		List<DrawInstruction> inst = new ArrayList<DrawInstruction>();

		inst.add(f.createGraphics((WebGraphics) webImage.getGraphics()));// graphics

		inst.add(f.draw(new Rectangle(), null));// draw

		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 60)));

		inst.add(f.draw(new Rectangle(), null));// draw

		DirectDrawUtils.optimizeInstructions(dd, inst);
		assertEquals("Count not valid", 4, inst.size());
		assertEquals("Transform not expected", new AffineTransform(1, 0, 0, 1, 20, 70), ((TransformConst) inst.get(2).getArgs()[0]).getAffineTransform());
	}

	@Test
	public void testMergedComposites() {
		WebImage webImage = new WebImage(dd, 10, 10);
		List<DrawInstruction> inst = new ArrayList<DrawInstruction>();

		inst.add(f.createGraphics((WebGraphics) webImage.getGraphics()));// graphics

		inst.add(f.draw(new Rectangle(), null));// draw

		inst.add(f.setComposite(AlphaComposite.Dst));
		inst.add(f.setComposite(AlphaComposite.SrcAtop));

		inst.add(f.draw(new Rectangle(), null));// draw

		DirectDrawUtils.optimizeInstructions(dd, inst);
		assertEquals("Count not valid", 4, inst.size());
		assertEquals("Composite not expected", AlphaComposite.SrcAtop, ((CompositeConst) inst.get(2).getArgs()[0]).getComposite());
	}

}
