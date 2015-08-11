package org.webswing.directdraw.toolkit;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.RenderedImage;
import java.util.*;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.model.DrawConstant;
import org.webswing.directdraw.model.DrawConstant.HashConst;
import org.webswing.directdraw.model.DrawInstruction;
import org.webswing.directdraw.model.ImageConst;
import org.webswing.directdraw.model.PathConst;
import org.webswing.directdraw.model.PointsConst;
import org.webswing.directdraw.proto.Directdraw.DrawConstantProto;
import org.webswing.directdraw.proto.Directdraw.DrawInstructionProto.InstructionProto;
import org.webswing.directdraw.proto.Directdraw.WebImageProto;
import org.webswing.directdraw.util.DirectDrawUtils;
import org.webswing.directdraw.util.DrawConstantPool;
import org.webswing.directdraw.util.RenderUtil;

import sun.awt.image.SurfaceManager;
import sun.java2d.SurfaceData;

public class WebImage extends Image {

	private String id = UUID.randomUUID().toString();
	private List<WebImage> chunks = new ArrayList<WebImage>();
	private DirectDraw context;
	private Dimension size;
	private BufferedImage imageHolder;
	Map<DrawInstruction, BufferedImage> partialImageMap;
	private volatile int lastGraphicsId = 0;
	private WebGraphics lastUsedG = null;
	private Set<WebGraphics> usedGs = new HashSet<WebGraphics>();
	private List<DrawInstruction> instructions = new ArrayList<DrawInstruction>();
	private List<DrawInstruction> newInstructions = new ArrayList<DrawInstruction>();

	@SuppressWarnings("restriction")
	public WebImage(DirectDraw dd, int w, int h) {
		this.context = dd;
		this.size = new Dimension(w, h);
		SurfaceManager.setManager(this, new SurfaceManager() {

			@SuppressWarnings("unused")
			// java 1.6
			public SurfaceData getSourceSurfaceData(sun.java2d.SurfaceData s, sun.java2d.loops.CompositeType c, java.awt.Color color, boolean b) {
				BufferedImage snapshot = WebImage.this.getSnapshot();
				SurfaceManager m = SurfaceManager.getManager(snapshot);
				try {
					return (SurfaceData) m.getClass().getDeclaredMethod("getSourceSurfaceData", sun.java2d.SurfaceData.class, sun.java2d.loops.CompositeType.class, java.awt.Color.class, Boolean.TYPE).invoke(m, s, c, color, b);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			@SuppressWarnings("unused")
			// java 1.6
			public SurfaceData getDestSurfaceData() {
				BufferedImage snapshot = WebImage.this.getSnapshot();
				SurfaceManager m = SurfaceManager.getManager(snapshot);
				try {
					return (SurfaceData) m.getClass().getDeclaredMethod("getDestSurfaceData").invoke(m);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			public SurfaceData getPrimarySurfaceData() {// java 1.7
				BufferedImage snapshot = WebImage.this.getSnapshot();
				SurfaceManager m = SurfaceManager.getManager(snapshot);
				try {
					return (SurfaceData) m.getClass().getDeclaredMethod("getPrimarySurfaceData").invoke(m);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			public SurfaceData restoreContents() {// java 1.7
				BufferedImage snapshot = WebImage.this.getSnapshot();
				SurfaceManager m = SurfaceManager.getManager(snapshot);
				try {
					return (SurfaceData) m.getClass().getDeclaredMethod("restoreContents").invoke(m);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

		});
	}

	@Override
	public int getWidth(ImageObserver paramImageObserver) {
		return size.width;
	}

	@Override
	public int getHeight(ImageObserver paramImageObserver) {
		return size.height;
	}

	@Override
	public ImageProducer getSource() {
		return getSnapshot().getSource();
	}

	@Override
	public Graphics getGraphics() {
		return new WebGraphics(this);
	}

	protected synchronized int getNextGraphicsId() {
		return lastGraphicsId++;
	}

	public DirectDraw getContext() {
		return context;
	}

	@Override
	public Object getProperty(String paramString, ImageObserver paramImageObserver) {
		return null;
	}

	public boolean isDirty() {
		synchronized (this) {
            return newInstructions.size() != 0;
		}
	}

	public void addInstruction(WebGraphics g, DrawInstruction in) {
		if (g != null && g.isDisposed()) {
			throw new RuntimeException("Drawing to disposed graphics.");
		}
		if (in.getInstruction().equals(InstructionProto.COPY_AREA) && newInstructions.size() > 0) {
			// copy area instruction must be isolated and always as a first
			// instruction
			WebImage newChunk = extractReadOnlyWebImage(true);
			chunks = newChunk.chunks;
			newChunk.chunks = null;
			chunks.add(newChunk);
		}
		DrawInstructionFactory factory = context.getInstructionFactory();
		synchronized (this) {
			if (lastUsedG != g) {
				if (!usedGs.contains(g)) {
					newInstructions.add(factory.createGraphics(g));
					usedGs.add(g);
					lastUsedG = g;
					if (in.getInstruction().equals(InstructionProto.TRANSFORM)) {
						// skip adding the instruction- transform is already
						// included in create graphics inst.
						return;
					}
				} else {
					newInstructions.add(factory.switchGraphics(g));
					lastUsedG = g;
				}
			}
			newInstructions.add(in);
		}
	}

	public void addImage(WebGraphics g, Object image, ImageObserver o, AffineTransform xform, Rectangle2D.Float crop) {
		Graphics2D ihg = (Graphics2D) getImageHolder().getGraphics();
		ihg.setTransform(g.getTransform());
		ihg.setClip(g.getClip());
		if (xform != null) {
			ihg.transform(xform);
		}
		if (image instanceof Image) {
			Image i = (Image) image;
			if (crop != null) {
				ihg.drawImage(i, 0, 0, (int) crop.width, (int) crop.height, (int) crop.x, (int) crop.y, (int) (crop.x + crop.width), (int) (crop.y + crop.height), o);
				ihg.clip(new Rectangle((int) crop.width, (int) crop.height));
			} else {
				ihg.drawImage(i, 0, 0, o);
				ihg.clip(new Rectangle(i.getWidth(o), i.getHeight(o)));
			}
		} else if (image instanceof RenderedImage) {
			RenderedImage i = (RenderedImage) image;
			ihg.drawRenderedImage(i, null);
			ihg.clip(new Rectangle(i.getWidth(), i.getHeight()));
		}
		ihg.setTransform(new AffineTransform(1, 0, 0, 1, 0, 0));
		ihg.clip(new Rectangle(getImageHolder().getWidth(), getImageHolder().getHeight()));
		if (ihg.getClip().getBounds().width > 0 && ihg.getClip().getBounds().height > 0) {
			addInstruction(g, new DrawInstruction(InstructionProto.DRAW_IMAGE, new PathConst(context, ihg.getClip()), new DrawConstant.Integer(0)));
		}
		ihg.dispose();
	}

	public void addFillInstruction(WebGraphics g, Shape fillShape) {
		if (imageHolder != null) {
			Graphics2D ihg = (Graphics2D) getImageHolder().getGraphics();
			ihg.setTransform(g.getTransform());
			ihg.setClip(g.getClip());
			if (g.getPaint().getTransparency() == Transparency.OPAQUE) {
				ihg.setComposite(AlphaComposite.Src);
				ihg.setPaint(new Color(0, 0, 0, 0));
				ihg.fill(fillShape);
			} else {
				ihg.setPaint(g.getPaint());
				ihg.fill(fillShape);
			}
			ihg.dispose();
		}
		addInstruction(g, context.getInstructionFactory().fill(fillShape, g.getClip()));
	}

	private BufferedImage getImageHolder() {
		if (imageHolder == null) {
			imageHolder = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		}
		return imageHolder;
	}

	public WebImage extractReadOnlyWebImage(boolean reset) {
		WebImage result = new WebImage(context, size.width, size.height) {

			@Override
			public void addInstruction(WebGraphics g, DrawInstruction in) {
				throw new UnsupportedOperationException("This is read only instance of webimage.");
			}

			public WebImageProto toMessage(DirectDraw dd) {
				return toMessageInternal(dd);
			}

		};
		synchronized (this) {
			result.id = id;
			result.newInstructions = newInstructions;
			result.chunks = chunks;
			// extract the areas where images were painted. For saving some
			// memory by not keeping the full window image
			// but just the relevant areas
			result.preprocessDrawImageInstructions(imageHolder);
			if (reset) {
				reset();
			}
		}
		return result;
	}

	protected void preprocessDrawImageInstructions(BufferedImage imageHolder) {
		if (imageHolder != null) {
			partialImageMap = new HashMap<DrawInstruction, BufferedImage>();
			// find drawImage instructions and save subImages to subImageMap
			// (these are later encoded to proto message)
			for (DrawInstruction ins : newInstructions) {
				DrawConstant[] constants = ins.getArgs();
				if (ins.getInstruction() == InstructionProto.DRAW_IMAGE) {
					Rectangle2D bounds = ((PathConst) constants[0]).getShape().getBounds().createIntersection(new Rectangle(imageHolder.getWidth(), imageHolder.getHeight()));
					BufferedImage subImage = new BufferedImage((int) bounds.getWidth(), (int) bounds.getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics sig = subImage.getGraphics();
					sig.drawImage(imageHolder.getSubimage((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight()), 0, 0, null);
					sig.dispose();
					partialImageMap.put(ins, subImage);
					constants[1] = new PointsConst(context, 0, (int) bounds.getX(), (int) bounds.getY());
				}
			}
		}
	}

	protected WebImageProto toMessageInternal(DirectDraw dd) {
		DrawConstantPool constantPool = dd.getConstantPool();
		DrawConstantPool imagePool = dd.getImagePool();

		WebImageProto.Builder webImageBuilder = WebImageProto.newBuilder();
		synchronized (this) { // TODO: remove this and keep only instructions
			// field
			instructions = newInstructions;
			newInstructions = new ArrayList<DrawInstruction>();
		}

		DirectDrawUtils.optimizeInstructions(dd, instructions);
		// System.out.println(instructions + (imageHolder == null ? "" : "*"));
		// first process chunks
		if (chunks != null && chunks.size() > 0) {
			for (WebImage chunk : chunks) {
				webImageBuilder.addChunks(chunk.toMessage(dd));
			}
		}

		// preprocess draw_image instructions
		if (partialImageMap != null) {
			for (DrawInstruction ins : partialImageMap.keySet()) {
				DrawConstant[] constants = ins.getArgs();
				// compute image hash and link containing image
				BufferedImage subImage = partialImageMap.get(ins);
				long hash = context.getServices().computeHash(subImage);
				HashConst imageHashConst = new DrawConstant.HashConst(hash);
				Integer[] points = ((PointsConst) constants[1]).getIntArray();
				if (!imagePool.isInCache(imageHashConst)) {
					ImageConst imageConst = new ImageConst(context, subImage, hash);
					imagePool.addToCache(new DrawConstant.HashConst(imageConst));
					DrawConstantProto.Builder builder = DrawConstantProto.newBuilder();
					builder.setId(imageConst.getId());
					if (imageConst.getFieldName() != null) {
						builder.setField(DrawConstantProto.Builder.getDescriptor().findFieldByName(imageConst.getFieldName()), imageConst.extractMessage(dd));
					}
					webImageBuilder.addImages(builder.build());
					constants[1] = new PointsConst(context, imageConst.getId(), points[1], points[2]);
				} else {
                    constants[1] = new PointsConst(context, imagePool.getCached(imageHashConst).getId(), points[1], points[2]);
				}
			}
		}
		// build proto message
		for (DrawInstruction ins : instructions) {
			for (DrawConstant cons : ins.getArgs()) {
				if (!(cons instanceof DrawConstant.Integer) && cons != DrawConstant.nullConst) {
					// update cache
					if (!constantPool.isInCache(cons)) {
						constantPool.addToCache(new DrawConstant.HashConst(cons));
						DrawConstantProto.Builder builder = DrawConstantProto.newBuilder();
						builder.setId(cons.getId());
						if (cons.getFieldName() != null) {
							builder.setField(DrawConstantProto.Builder.getDescriptor().findFieldByName(cons.getFieldName()), cons.extractMessage(dd));
						}
						webImageBuilder.addConstants(builder.build());
					} else {
                        cons.setId(constantPool.getCached(cons).getId());
					}
				}
			}
			webImageBuilder.addInstructions(ins.toMessage(dd));
		}
		webImageBuilder.setWidth(size.width);
		webImageBuilder.setHeight(size.height);
        return webImageBuilder.build();
	}

	public void reset() {
		imageHolder = null;
		newInstructions = new ArrayList<DrawInstruction>();
		chunks = new ArrayList<WebImage>();
		lastUsedG = null;
		usedGs.clear();
	}

	public WebImageProto toMessage(DirectDraw dd) {
		// toMessageInternal is executed from anonymous subclass' overriden
		// method created in 'extractReadOnlyWebImage' method
		throw new IllegalStateException("Only read-only image can be encoded to proto message. Invoke extractReadOnlyWebImage method first to create read-only webimage.");
	}

	public BufferedImage getSnapshot() {
		return RenderUtil.render(this, imageHolder, partialImageMap, chunks, newInstructions, size);
	}

	public BufferedImage getSnapshot(BufferedImage result) {
		return RenderUtil.render(result, this, imageHolder, partialImageMap, chunks, newInstructions, size);
	}
}
