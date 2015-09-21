package org.directdraw.webswing.util;

import static org.junit.Assert.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

import org.junit.*;
import org.webswing.directdraw.*;
import org.webswing.directdraw.model.*;
import org.webswing.directdraw.toolkit.*;
import org.webswing.directdraw.util.*;

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

		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));// ignored
		inst.add(f.setPaint(Color.black));
		inst.add(f.setComposite(AlphaComposite.Dst));
		inst.add(f.setStroke(new BasicStroke(1)));

		DirectDrawUtils.optimizeInstructions(dd, inst);
		assertEquals("Count not valid", 2, inst.size());
        assertEquals("Transform not expected", new AffineTransform(1, 0, 0, 1, 20, 20), ((TransformConst) inst.get(0).getArgs()[1]).getValue());
        assertEquals("Stroke not expected", 2, (int) ((StrokeConst) inst.get(0).getArgs()[2]).getValue().getLineWidth());
        assertEquals("Composite not expected", AlphaComposite.Src, ((CompositeConst) inst.get(0).getArgs()[3]).getValue());
        assertEquals("Color not expected", Color.blue, ((ColorConst) inst.get(0).getArgs()[4]).getValue());
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
        assertEquals("Transform not expected", new AffineTransform(1, 0, 0, 1, 10, 10), ((TransformConst) inst.get(0).getArgs()[1]).getValue());
        assertEquals("Stroke not expected", 2, (int) ((StrokeConst) inst.get(0).getArgs()[2]).getValue().getLineWidth());
        assertEquals("Composite not expected", AlphaComposite.Dst, ((CompositeConst) inst.get(0).getArgs()[3]).getValue());
        assertEquals("Color not expected", Color.blue, ((ColorConst) inst.get(0).getArgs()[4]).getValue());
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
        assertEquals("Color not expected", Color.green, ((ColorConst) inst.get(2).getArgs()[0]).getValue());
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
        assertEquals("Transform not expected", new AffineTransform(1, 0, 0, 1, 20, 70), ((TransformConst) inst.get(2).getArgs()[0]).getValue());
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
        assertEquals("Composite not expected", AlphaComposite.SrcAtop, ((CompositeConst) inst.get(2).getArgs()[0]).getValue());
	}

}
