package org.webswing.toolkit.directdraw;

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
import org.webswing.directdraw.proto.Directdraw.DrawConstantProto;
import org.webswing.directdraw.proto.Directdraw.WebImageProto;
import org.webswing.toolkit.directdraw.instructions.DrawConstantPool;

import com.google.protobuf.Message;

public class WebImage extends Image {

    private Dimension size;
    private int lastGraphicsId = 0;
    WebGraphics lastUsedG = null;
    private List<DrawInstruction> instructions = new ArrayList<DrawInstruction>();

    public WebImage(int w, int h) {
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

    @Override
    public Object getProperty(String paramString, ImageObserver paramImageObserver) {
        return null;
    }

    public void addInstruction(WebGraphics g, DrawInstruction in) {
        if (g != null && g.isDisposed()) {
            throw new RuntimeException("Drawing to disposed graphics.");
        }
        if (lastUsedG != g && g != null) {
            instructions.add(DrawInstruction.switchGraphics(g));
            lastUsedG = g;
        }
        instructions.add(in);
    }

    public Message toMessage(DirectDraw dd) {
        DrawConstantPool constantPool = dd.getConstantPool();
        WebImageProto.Builder webImageBuilder = WebImageProto.newBuilder();
        for (DrawInstruction ins : instructions) {
            DrawConstant<?>[] constants = ins.getArgs();
            for (DrawConstant<?> cons : constants) {
                if (!(cons instanceof DrawConstant.Integer)) {
                    boolean isInCache = constantPool.isInCache(cons);
                    DrawConstant<?> cached = constantPool.getCachedConstant(cons);
                    if (!isInCache) {//this constant was recently inserted to cache
                        DrawConstantProto.Builder builder = DrawConstantProto.newBuilder();
                        builder.setId(cons.getAddress());
                        if (cons.getFieldName() != null) {
                            builder.setField(DrawConstantProto.Builder.getDescriptor().findFieldByName(cons.getFieldName()), cons.toMessage(dd));
                        }
                        webImageBuilder.addConstants(builder.build());
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
