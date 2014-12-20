package org.webswing.directdraw.util;

import java.util.HashMap;

import org.webswing.directdraw.model.DrawConstant;
import org.webswing.directdraw.model.ImageConst;

public class ImageConstantPool {
	private LRUDrawConstantPoolCache pool;
	private final HashMap<Long, ImageConst> subImageHashMap = new HashMap<Long, ImageConst>();

	public ImageConstantPool(int size) {
		pool = new LRUDrawConstantPoolCache(size){
			@Override
			public void onElementRemoved(DrawConstant val) {
				for(Long hash:((ImageConst)val).getSubImageHashMap().keySet()){
					subImageHashMap.remove(hash);
				}
			}
		};
	}

	public ImageConst putImage(ImageConst image) {
		for (Long hash : image.getSubImageHashMap().keySet()) {
			subImageHashMap.put(hash, image);
		}
		return (ImageConst) pool.set(image);
	}

	public ImageConst getImageConst(Long hash) {
		ImageConst ic = subImageHashMap.get(hash);
		if (ic != null) {
			return (ImageConst) pool.set(ic);
		}
		return null;
	}

	public boolean isInCache(Long hash) {
		ImageConst ic = subImageHashMap.get(hash);
		if (ic != null) {
			return pool.contains(ic);
		}
		return false;
	}

	
}
