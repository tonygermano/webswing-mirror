package org.webswing.directdraw.util;

import java.util.List;

import org.webswing.directdraw.model.DrawConstant;
import org.webswing.directdraw.proto.Directdraw.DrawConstantProto;

public class DrawConstantPool {

    private LRUDrawConstantPoolCache pool;

    public DrawConstantPool(int size) {
        pool = new LRUDrawConstantPoolCache(size);
    }

    public DrawConstant<?> getCached(DrawConstant<?> constant) {
        return pool.getOrAdd(constant);
    }

    private void addToCache(DrawConstant<?> constant) {
        pool.getOrAdd(constant);
    }

    private boolean isInCache(DrawConstant<?> constant) {
        return pool.contains(constant);
    }
    
    public int addToCache(List<DrawConstantProto> protos, DrawConstant<?> cons) {
		int thisId;
		if (!isInCache(cons)) {
			addToCache(cons);
			DrawConstantProto.Builder proto = DrawConstantProto.newBuilder();
			if (cons.getFieldName() != null) {
				proto.setField(DrawConstantProto.Builder.getDescriptor().findFieldByName(cons.getFieldName()), cons.toMessage());
			}
			proto.setId(cons.getId());
			protos.add(proto.build());
			thisId=cons.getId();
		} else {
			thisId=getCached(cons).getId();
		}
		return thisId;
	}
}
