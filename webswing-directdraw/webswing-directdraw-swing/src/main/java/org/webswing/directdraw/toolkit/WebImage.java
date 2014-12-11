package org.webswing.directdraw.toolkit;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.List;

import org.webswing.directdraw.DirectDraw;
import org.webswing.directdraw.model.DrawConstant;
import org.webswing.directdraw.model.DrawInstruction;
import org.webswing.directdraw.model.ImageConst;
import org.webswing.directdraw.proto.Directdraw.DrawConstantProto;
import org.webswing.directdraw.proto.Directdraw.WebImageProto;
import org.webswing.directdraw.proto.Directdraw.DrawInstructionProto.InstructionProto;
import org.webswing.directdraw.util.DrawConstantPool;

import com.google.protobuf.Message;

public class WebImage extends Image {

	private DirectDraw context;
	private Dimension size;
	private int lastGraphicsId = 0;
	WebGraphics lastUsedG = null;
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

	protected int getNextGraphicsId() {
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
		synchronized (this) {
			if (lastUsedG != g && g != null) {
				newInstructions.add(DrawInstruction.switchGraphics(g));
				lastUsedG = g;
			}
			newInstructions.add(in);
		}
	}

	public Message toMessage(DirectDraw dd) {
		return toMessage(dd, true);
	}

	public Message toMessage(DirectDraw dd, boolean resetOld) {
		DrawConstantPool constantPool = dd.getConstantPool();
		DrawConstantPool imagePool = dd.getImagePool();
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
		for (DrawInstruction ins : instructions) {
			DrawConstant[] constants = ins.getArgs();
			for (DrawConstant cons : constants) {
				if (!(cons instanceof DrawConstant.Integer)) {
					boolean useImagePool = (cons instanceof ImageConst) && (ins.getInstruction() == InstructionProto.DRAW_IMAGE);
					DrawConstantPool pool = useImagePool ? imagePool : constantPool;

					boolean isInCache = pool.isInCache(cons);
					DrawConstant cached = pool.getCachedConstant(cons);
					if (!isInCache) {// this constant was recently inserted to
										// cache
						DrawConstantProto.Builder builder = DrawConstantProto.newBuilder();
						builder.setId(cons.getAddress());
						if (cons.getFieldName() != null) {
							builder.setField(DrawConstantProto.Builder.getDescriptor().findFieldByName(cons.getFieldName()), cons.getMessage(dd));
						}

						if(useImagePool){
							webImageBuilder.addImageConstants(builder.build());
						}else{
							webImageBuilder.addConstants(builder.build());
						}
					} else {
						cons.setAddress(cached.getAddress());
					}
				}
			}
			webImageBuilder.addInstructions(ins.toMessage(dd));
		}
		return webImageBuilder.build();
	}
}
