package org.webswing.directdraw.util;

import org.webswing.directdraw.model.DrawConstant;

public class DrawConstantPool {

    private LRUDrawConstantPoolCache pool;

    public DrawConstantPool(int size) {
        pool = new LRUDrawConstantPoolCache(size);
    }

    public DrawConstant getCachedConstant(DrawConstant constant) {
        return pool.set(constant);
    }

    public boolean isInCache(DrawConstant constant) {
        return pool.contains(constant);
    }
}
