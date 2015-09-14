package org.webswing.directdraw.toolkit;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

import org.webswing.directdraw.*;
import org.webswing.directdraw.model.*;
import org.webswing.directdraw.proto.Directdraw.*;
import org.webswing.directdraw.proto.Directdraw.DrawInstructionProto.*;
import org.webswing.directdraw.util.*;
import sun.awt.image.*;
import sun.java2d.*;

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
    
    public void dispose(WebGraphics g) {
        synchronized (this) {
            newInstructions.add(context.getInstructionFactory().disposeGraphics(g));
            usedGs.remove(g);
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
			addInstruction(g, context.getInstructionFactory().drawImage(ihg.getClip()));
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

            @Override
			public WebImageProto toMessage(DirectDraw dd) {
				return toMessageInternal(dd);
			}

            @Override
            public boolean isDirty()
            {
                return false;
            }
        };
		synchronized (this) {
			result.id = id;
			result.newInstructions = newInstructions;
            newInstructions = new ArrayList<DrawInstruction>();
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
                    Rectangle hold = new Rectangle(imageHolder.getWidth(), imageHolder.getHeight());
					Rectangle2D bounds = hold.createIntersection(RenderUtil.getShape(constants[0]).getBounds());
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

		DirectDrawUtils.optimizeInstructions(dd, newInstructions);
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
                ImageConst imageConst = new ImageConst(context, subImage);
				int[] points = ((PointsConst) constants[1]).getPoints();
				if (!imagePool.isInCache(imageConst)) {
					imagePool.addToCache(imageConst);
					DrawConstantProto.Builder builder = DrawConstantProto.newBuilder();
					builder.setId(imageConst.getId());
					if (imageConst.getFieldName() != null) {
						builder.setField(DrawConstantProto.Builder.getDescriptor().findFieldByName(imageConst.getFieldName()), imageConst.toMessage());
					}
					webImageBuilder.addImages(builder.build());
					constants[1] = new PointsConst(context, imageConst.getId(), points[1], points[2]);
				} else {
                    constants[1] = new PointsConst(context, imagePool.getCached(imageConst).getId(), points[1], points[2]);
				}
			}
		}
		// build proto message
		for (DrawInstruction ins : newInstructions) {
			for (DrawConstant cons : ins.getArgs()) {
				if (!(cons instanceof DrawConstant.IntegerConst) && cons != DrawConstant.nullConst) {
					// update cache
					if (!constantPool.isInCache(cons)) {
						constantPool.addToCache(cons);
						DrawConstantProto.Builder builder = DrawConstantProto.newBuilder();
						builder.setId(cons.getId());
						if (cons.getFieldName() != null) {
							builder.setField(DrawConstantProto.Builder.getDescriptor().findFieldByName(cons.getFieldName()), cons.toMessage());
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

	public synchronized void reset() {
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
