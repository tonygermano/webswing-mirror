package org.directdraw.webswing.util;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.model.DrawInstruction;
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
	public void testTransformGroupedOptimization() {
		List<DrawInstruction> inst = new ArrayList<DrawInstruction>();
		inst.add(f.createGraphics((WebGraphics) new WebImage(dd, 10, 10).getGraphics()));
		inst.add(f.createGraphics((WebGraphics) new WebImage(dd, 10, 10).getGraphics()));
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		inst.add(f.createGraphics((WebGraphics) new WebImage(dd, 10, 10).getGraphics()));
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		inst.add(f.draw(new Rectangle(), null));
		inst.add(f.transform(new AffineTransform(2, 0, 0, 2, 0, 0)));
		inst.add(f.transform(new AffineTransform(0.5, 0, 0, 2, 0, 0)));
		inst.add(f.draw(new Rectangle(), null));
		inst.add(f.draw(new Rectangle(), null));
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, -10, -10)));
		inst.add(f.draw(new Rectangle(), null));
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		inst.add(f.transform(new AffineTransform(1, 0, 0, 1, 10, 10)));
		DirectDrawUtils.optimizeInstructions(dd, inst);
		assertEquals("Count not valid",8, inst.size());
		assertEquals("Transform not expected", new AffineTransform(1, 0, 0, 1, 0, 0), ((TransformConst) inst.get(0).getArgs()[1]).getAffineTransform());
		assertEquals("Transform not expected", new AffineTransform(1, 0, 0, 1, 20, 20), ((TransformConst) inst.get(1).getArgs()[1]).getAffineTransform());
		assertEquals("Transform not expected", new AffineTransform(1, 0, 0, 1, 20, 20), ((TransformConst) inst.get(2).getArgs()[1]).getAffineTransform());
		assertEquals("Transform not expected", new AffineTransform(1, 0, 0, 4, 0, 0), ((TransformConst) inst.get(4).getArgs()[0]).getAffineTransform());
	}
}
