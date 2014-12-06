package org.webswing.toolkit.directdraw.instructions;

import org.webswing.directdraw.model.DrawConstant;

public class DrawConstantPool {

    private static final int DRAW_CONSTANTS_POOL_CACHE_CAPACITY = 8192;

    private LRUDrawConstantPoolCache pool;

    public DrawConstantPool() {
        pool = new LRUDrawConstantPoolCache(DRAW_CONSTANTS_POOL_CACHE_CAPACITY);
    }

    public DrawConstant<?> getCachedConstant(DrawConstant<?> constant) {
        return pool.set(constant);
    }
    
    public boolean isInCache(DrawConstant<?> constant) {
        return pool.contains(constant);
    }
}
