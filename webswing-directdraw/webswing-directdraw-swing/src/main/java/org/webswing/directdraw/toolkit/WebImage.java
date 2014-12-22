package org.webswing.directdraw.toolkit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.model.DrawConstant;
import org.webswing.directdraw.model.DrawInstruction;
import org.webswing.directdraw.model.ImageConst;
import org.webswing.directdraw.model.PathConst;
import org.webswing.directdraw.model.PointsConst;
import org.webswing.directdraw.proto.Directdraw.DrawConstantProto;
import org.webswing.directdraw.proto.Directdraw.DrawInstructionProto.InstructionProto;
import org.webswing.directdraw.proto.Directdraw.ImageProto;
import org.webswing.directdraw.proto.Directdraw.WebImageProto;
import org.webswing.directdraw.util.DrawConstantPool;
import org.webswing.directdraw.util.ImageConstantPool;

import com.google.protobuf.Message;

public class WebImage extends Image {

	private DirectDraw context;
	private Dimension size;
	private BufferedImage imageHolder;
	private volatile int lastGraphicsId = 0;
	private WebGraphics lastUsedG = null;
	private Set<WebGraphics> usedGs = new HashSet<WebGraphics>();
	private List<DrawInstruction> instructions = new ArrayList<DrawInstruction>();
	private List<DrawInstruction> newInstructions = new ArrayList<DrawInstruction>();

	public WebImage(DirectDraw dd, int w, int h) {
		this.context = dd;
		this.size = new Dimension(w, h);
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
		return null;
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

	public void addInstruction(WebGraphics g, DrawInstruction in) {
		if (g != null && g.isDisposed()) {
			throw new RuntimeException("Drawing to disposed graphics.");
		}
		DrawInstructionFactory factory = context.getInstructionFactory();
		synchronized (this) {
			if (lastUsedG != g) {
				if (!usedGs.contains(g)) {
					newInstructions.add(factory.createGraphics(g));
					usedGs.add(g);
					lastUsedG = g;
					if(in.getInstruction().equals(InstructionProto.TRANSFORM)){
						//skip adding the instruction- transform is already included in create graphics inst.
						return;
					}
				}else{
					newInstructions.add(factory.switchGraphics(g));
					lastUsedG = g;
				}
			}
			newInstructions.add(in);
		}
	}

	public void addImage(WebGraphics g, Image i, ImageObserver o, AffineTransform xform, Rectangle2D.Float crop, Color bkg) {
		Graphics2D ihg = (Graphics2D) getImageHolder().getGraphics();
		ihg.setTransform(g.getTransform());
		ihg.setClip(g.getClip());
		if (xform != null) {
			ihg.transform(xform);
		}
		ihg.setBackground(bkg);
		if (crop != null) {
			ihg.drawImage(i, 0, 0, (int) crop.width, (int) crop.height, (int) crop.x, (int) crop.y, (int) (crop.x + crop.width), (int) (crop.y + crop.height), o);
			ihg.clip(new Rectangle((int) crop.width, (int) crop.height));
		} else {
			ihg.drawImage(i, 0, 0, o);
			ihg.clip(new Rectangle(i.getWidth(o), i.getHeight(o)));
		}
		ihg.setTransform(new AffineTransform(1, 0, 0, 1, 0, 0));
		ihg.clip(new Rectangle(getImageHolder().getWidth(), getImageHolder().getHeight()));
		if (ihg.getClip().getBounds().width > 0 && ihg.getClip().getBounds().height > 0) {
			addInstruction(g, new DrawInstruction(InstructionProto.DRAW_IMAGE, new PathConst(context, ihg.getClip(), null), new DrawConstant.Integer(0)));
		}
		ihg.dispose();
	}

	public void addImage(WebGraphics g, RenderedImage i, AffineTransform xform) {
		Graphics2D ihg = (Graphics2D) getImageHolder().getGraphics();
		ihg.setTransform(g.getTransform());
		ihg.setClip(g.getClip());
		if (xform != null) {
			ihg.transform(xform);
		}
		ihg.drawRenderedImage(i, null);
		ihg.clip(new Rectangle(i.getWidth(), i.getHeight()));
		ihg.setTransform(new AffineTransform(1, 0, 0, 1, 0, 0));
		ihg.clip(new Rectangle(getImageHolder().getWidth(), getImageHolder().getHeight()));
		if (ihg.getClip().getBounds().width > 0 && ihg.getClip().getBounds().height > 0) {
			addInstruction(g, new DrawInstruction(InstructionProto.DRAW_IMAGE, new PathConst(context, ihg.getClip(), null), new DrawConstant.Integer(0)));
		}
		ihg.dispose();
	}

	private BufferedImage getImageHolder() {
		if (imageHolder == null) {
			imageHolder = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		}
		return imageHolder;
	}

	public WebImage extractReadOnlyWebImage() {
		WebImage result = new WebImage(context, size.width, size.height){
			@Override
			public void addInstruction(WebGraphics g, DrawInstruction in) {
				throw new UnsupportedOperationException("This is read only instance of webimage.");
			}
		};
		synchronized (this) {
			System.out.println(newInstructions);
			result.newInstructions = newInstructions;
			if (imageHolder != null) {
				result.getImageHolder().getGraphics().drawImage(imageHolder, 0, 0, null);
			}
			newInstructions = new ArrayList<DrawInstruction>();
			lastUsedG = null;
			usedGs.clear();
		}
		return result;
	}

	public Message toMessage(DirectDraw dd) {
		return toMessage(dd, true);
	}

	public Message toMessage(DirectDraw dd, boolean resetOld) {
		DrawConstantPool constantPool = dd.getConstantPool();
		ImageConstantPool imagePool = dd.getImagePool();
		HashMap<Long, Point> currentFrameImageHashes = new HashMap<Long, Point>();
		List<DrawInstruction> instructionsToUpdate = new ArrayList<DrawInstruction>();
		Rectangle2D imageCrop = null;

		WebImageProto.Builder webImageBuilder = WebImageProto.newBuilder();
		synchronized (this) {
			if (resetOld) {
				instructions = newInstructions;
				newInstructions = new ArrayList<DrawInstruction>();
			} else {
				instructions.addAll(newInstructions);
				newInstructions.clear();
			}
		}
		// preprocess draw_image instructions
		for (DrawInstruction ins : instructions) {
			DrawConstant[] constants = ins.getArgs();
			// compute image hash and link containing image
			if (ins.getInstruction() == InstructionProto.DRAW_IMAGE) {
				Rectangle2D bounds = ((PathConst) constants[0]).getShape().getBounds().createIntersection(new Rectangle(imageHolder.getWidth(), imageHolder.getHeight()));
				BufferedImage subImage = imageHolder.getSubimage((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
				long imageHash = context.getServices().computeHash(subImage);
				if (imagePool.isInCache(imageHash)) {
					ImageConst imageRef = imagePool.getImageConst(imageHash);
					Point startPoint = imageRef.getSubImageHashMap().get(imageHash);
					constants[1] = new PointsConst(dd, imageRef.getAddress(), (int) (startPoint.x - bounds.getX()), (int) (startPoint.y - bounds.getY()));
				} else {
					imageCrop = imageCrop == null ? bounds : bounds.createUnion(imageCrop);
					currentFrameImageHashes.put(imageHash, new Point((int) bounds.getX(), (int) bounds.getY()));
					instructionsToUpdate.add(ins);
				}
			}
		}
		// include current imageHolder to message if necessary
		if (currentFrameImageHashes.size() > 0) {
			ImageConst imageConst = new ImageConst(context, imageHolder, imageCrop, currentFrameImageHashes);
			imageConst = imagePool.putImage(imageConst);
			for (DrawInstruction ins : instructionsToUpdate) {
				ins.getArgs()[1] = new PointsConst(dd, imageConst.getAddress(), 0, 0);
			}
			webImageBuilder.setImage((ImageProto) imageConst.extractMessage(dd));
		}
		// build proto message
		for (DrawInstruction ins : instructions) {
			DrawConstant[] constants = ins.getArgs();
			for (DrawConstant cons : constants) {
				if (!(cons instanceof DrawConstant.Integer)) {
					// update cache
					boolean isInCache = constantPool.isInCache(cons);
					DrawConstant cached = constantPool.getCachedConstant(cons);
					if (!isInCache) {// this constant was recently inserted to
										// cache
						DrawConstantProto.Builder builder = DrawConstantProto.newBuilder();
						builder.setId(cons.getAddress());
						if (cons.getFieldName() != null) {
							builder.setField(DrawConstantProto.Builder.getDescriptor().findFieldByName(cons.getFieldName()), cons.extractMessage(dd));
						}
						webImageBuilder.addConstants(builder.build());
					} else {
						cons.setAddress(cached.getAddress());
					}
				}
			}
			webImageBuilder.addInstructions(ins.toMessage(dd));
		}
		webImageBuilder.setWidth(size.width);
		webImageBuilder.setHeight(size.height);
		return webImageBuilder.build();
	}
}
