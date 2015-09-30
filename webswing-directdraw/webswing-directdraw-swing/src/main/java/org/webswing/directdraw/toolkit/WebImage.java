package org.webswing.directdraw.toolkit;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.model.DrawConstant;
import org.webswing.directdraw.model.DrawInstruction;
import org.webswing.directdraw.model.IntegerConst;
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
	private List<WebImage> chunks;
	private DirectDraw context;
	private Dimension size;
	private volatile int lastGraphicsId = 0;
	private WebGraphics lastUsedG = null;
	private Set<WebGraphics> usedGraphics;
	private List<DrawInstruction> instructions;

	@SuppressWarnings("restriction")
	public WebImage(DirectDraw dd, int w, int h) {
		this(dd, w, h, new ArrayList<WebImage>(), new ArrayList<DrawInstruction>());
		this.usedGraphics = new HashSet<WebGraphics>();
	}

	private WebImage(DirectDraw dd, int w, int h, List<WebImage> chunks, List<DrawInstruction> instructions) {
		this.context = dd;
		this.size = new Dimension(w, h);
		this.chunks = chunks;
		this.instructions = instructions;

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
			return !instructions.isEmpty();
		}
	}

	public void addInstruction(WebGraphics g, DrawInstruction in) {
		if (g != null && g.isDisposed()) {
			throw new RuntimeException("Drawing to disposed graphics.");
		}
		synchronized (this) {
			if (in.getInstruction().equals(InstructionProto.COPY_AREA) && !instructions.isEmpty()) {
				// copy area instruction must be isolated and always as a first
				// instruction
				WebImage newChunk = extractReadOnlyWebImage(true);
				chunks = newChunk.chunks;
				newChunk.chunks = null;
				chunks.add(newChunk);
			}
			DrawInstructionFactory factory = context.getInstructionFactory();
			if (lastUsedG != g) {
				if (!usedGraphics.contains(g)) {
					instructions.add(factory.createGraphics(g));
					usedGraphics.add(g);
					lastUsedG = g;
					if (in.getInstruction().equals(InstructionProto.TRANSFORM)) {
						// skip adding the instruction- transform is already
						// included in create graphics inst.
						return;
					}
				} else {
					instructions.add(factory.switchGraphics(g));
					lastUsedG = g;
				}
			}
			instructions.add(in);
		}
	}

	public void dispose(WebGraphics g) {
		synchronized (this) {
			usedGraphics.remove(g);
		}
	}

	public WebImage extractReadOnlyWebImage(boolean reset) {
		WebImage result;
		synchronized (this) {
			result = reset ?
				new ReadOnlyWebImage(context, size.width, size.height, chunks, instructions) :
				new ReadOnlyWebImage(context, size.width, size.height, new ArrayList<WebImage>(chunks), new ArrayList<DrawInstruction>(instructions));
			result.id = id;
			if (reset) {
				reset();
			}
		}
		return result;
	}

	protected WebImageProto toMessageInternal(DirectDraw dd) {
		DrawConstantPool constantPool = dd.getConstantPool();

		WebImageProto.Builder webImageBuilder = WebImageProto.newBuilder();

		DirectDrawUtils.optimizeInstructions(dd, instructions);
		// System.out.println(instructions + (imageHolder == null ? "" : "*"));
		// first process chunks
		if (chunks != null && chunks.size() > 0) {
			for (WebImage chunk : chunks) {
				webImageBuilder.addChunks(chunk.toMessage(dd));
			}
		}

		// build proto message
		for (DrawInstruction instruction : instructions) {
			for (DrawConstant cons : instruction) {
				if (!(cons instanceof IntegerConst) && cons != DrawConstant.nullConst) {
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
			webImageBuilder.addInstructions(instruction.toMessage(dd));
		}
		webImageBuilder.setWidth(size.width);
		webImageBuilder.setHeight(size.height);
		return webImageBuilder.build();
	}

	public void reset() {
		synchronized (this) {
			lastUsedG = null;
			usedGraphics.clear();
			// do not clear these collections as they are be copied to read-only instance
			instructions = new ArrayList<DrawInstruction>();
			chunks = new ArrayList<WebImage>();
		}
	}

	public WebImageProto toMessage(DirectDraw dd) {
		// toMessageInternal is executed from anonymous subclass' overriden
		// method created in 'extractReadOnlyWebImage' method
		throw new IllegalStateException("Only read-only image can be encoded to proto message. Invoke extractReadOnlyWebImage method first to create read-only webimage.");
	}

	public BufferedImage getSnapshot() {
		return RenderUtil.render(chunks, instructions, size);
	}

	public BufferedImage getSnapshot(BufferedImage result) {
		return RenderUtil.render(result, chunks, instructions);
	}

	private static class ReadOnlyWebImage extends WebImage {

		public ReadOnlyWebImage(DirectDraw dd, int w, int h, List<WebImage> chunks, List<DrawInstruction> instructions) {
			super(dd, w, h, chunks, instructions);
		}

		@Override
		public void addInstruction(WebGraphics g, DrawInstruction in) {
			throw new UnsupportedOperationException("This is read only instance of webimage.");
		}

		@Override
		public WebImageProto toMessage(DirectDraw dd) {
			return toMessageInternal(dd);
		}

		@Override
		public void dispose(WebGraphics g) {
			// nothing to dispose
		}

		@Override
		public void reset() {
			// do nothing
		}
	}
}
