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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
			if (newInstructions.size() == 0) {
				return false;
			} else {
				return true;
			}
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
			addInstruction(g, new DrawInstruction(InstructionProto.DRAW_IMAGE, new PathConst(context, ihg.getClip(), null), new DrawConstant.Integer(0)));
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

		};
		synchronized (this) {
			result.id = id;
			if (!reset) {
				result.newInstructions = new ArrayList<DrawInstruction>(newInstructions);
				result.imageHolder = imageHolder == null ? null : DirectDrawUtils.deepCopy(imageHolder);
				result.chunks = new ArrayList<WebImage>(chunks);
			} else {
				result.newInstructions = newInstructions;
				result.imageHolder = imageHolder;
				result.chunks = chunks;
				reset();
			}
		}
		return result;
	}

	public void reset() {
		imageHolder = null;
		newInstructions = new ArrayList<DrawInstruction>();
		chunks = new ArrayList<WebImage>();
		lastUsedG = null;
		usedGs.clear();
	}

	public WebImageProto toMessage(DirectDraw dd) {
		DrawConstantPool constantPool = dd.getConstantPool();
		DrawConstantPool imagePool = dd.getImagePool();

		WebImageProto.Builder webImageBuilder = WebImageProto.newBuilder();
		synchronized (this) {
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
		for (DrawInstruction ins : instructions) {
			DrawConstant[] constants = ins.getArgs();
			// compute image hash and link containing image
			if (ins.getInstruction() == InstructionProto.DRAW_IMAGE) {
				Rectangle2D bounds = ((PathConst) constants[0]).getShape().getBounds().createIntersection(new Rectangle(imageHolder.getWidth(), imageHolder.getHeight()));
				BufferedImage subImage = imageHolder.getSubimage((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
				long hash = context.getServices().computeHash(subImage);
				HashConst imageHashConst = new DrawConstant.HashConst(hash);
				if (!imagePool.isInCache(imageHashConst)) {
					ImageConst imageConst = new ImageConst(context, subImage, hash);
					imagePool.getCachedConstant(new DrawConstant.HashConst(imageConst));
					DrawConstantProto.Builder builder = DrawConstantProto.newBuilder();
					builder.setId(imageConst.getAddress());
					if (imageConst.getFieldName() != null) {
						builder.setField(DrawConstantProto.Builder.getDescriptor().findFieldByName(imageConst.getFieldName()), imageConst.extractMessage(dd));
					}
					webImageBuilder.addImages(builder.build());
					constants[1] = new PointsConst(context, imageConst.getAddress(), (int) bounds.getX(), (int) bounds.getY());
				} else {
					DrawConstant imageRef = imagePool.getCachedConstant(imageHashConst);
					constants[1] = new PointsConst(context, imageRef.getAddress(), (int) bounds.getX(), (int) bounds.getY());
				}
			}
		}
		// build proto message
		for (DrawInstruction ins : instructions) {
			DrawConstant[] constants = ins.getArgs();
			for (DrawConstant cons : constants) {
				if (!(cons instanceof DrawConstant.Integer)) {
					// update cache
					if (!constantPool.isInCache(cons)) {
						constantPool.getCachedConstant(new DrawConstant.HashConst(cons));
						DrawConstantProto.Builder builder = DrawConstantProto.newBuilder();
						builder.setId(cons.getAddress());
						if (cons.getFieldName() != null) {
							builder.setField(DrawConstantProto.Builder.getDescriptor().findFieldByName(cons.getFieldName()), cons.extractMessage(dd));
						}
						webImageBuilder.addConstants(builder.build());
					} else {
						DrawConstant cached = constantPool.getCachedConstant(cons);
						cons.setAddress(cached.getAddress());
					}
				}
			}
			webImageBuilder.addInstructions(ins.toMessage(dd));
		}
		webImageBuilder.setWidth(size.width);
		webImageBuilder.setHeight(size.height);
		WebImageProto result = webImageBuilder.build();
		return result;
	}

	public BufferedImage getSnapshot() {
		return RenderUtil.render(this, imageHolder, chunks, newInstructions, size);
	}

	public BufferedImage getSnapshot(BufferedImage result) {
		return RenderUtil.render(result, this, imageHolder, chunks, newInstructions, size);
	}
}
