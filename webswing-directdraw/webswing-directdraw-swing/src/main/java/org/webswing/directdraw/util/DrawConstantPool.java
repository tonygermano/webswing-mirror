package org.webswing.directdraw.util;

import org.webswing.directdraw.model.DrawConstant;

public class DrawConstantPool {

    private LRUDrawConstantPoolCache pool;

    public DrawConstantPool(int size) {
        pool = new LRUDrawConstantPoolCache(size);
    }

    public DrawConstant getCached(DrawConstant constant) {
        return pool.getOrAdd(constant);
    }

    public void addToCache(DrawConstant constant) {
        pool.getOrAdd(constant);
    }

    public boolean isInCache(DrawConstant constant) {
        return pool.contains(constant);
    }
}
