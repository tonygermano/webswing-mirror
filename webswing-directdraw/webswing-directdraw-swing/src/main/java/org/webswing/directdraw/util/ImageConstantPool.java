package org.webswing.directdraw.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Set;

import org.webswing.directdraw.model.ImageConst;

public class ImageConstantPool {
	private LRUDrawConstantPoolCache pool;
	private HashMap<Long, WeakReference<ImageConst>> subImageHashMap = new HashMap<Long, WeakReference<ImageConst>>();

	public ImageConstantPool(int size) {
		pool = new LRUDrawConstantPoolCache(size);
	}

	public ImageConst putImage(Set<Long> hashes, ImageConst image){
		for(Long hash:hashes){
			subImageHashMap.put(hash,new WeakReference<ImageConst>(image));
		}
		return (ImageConst) pool.set(image);
	}

	public ImageConst getImageConst(Long hash) {
		WeakReference<ImageConst> wr = subImageHashMap.get(hash);
		if (wr!=null && wr.get()!=null){
			return (ImageConst) pool.set(wr.get());
		}
		return null;
	}

	public boolean isInCache(Long hash) {
		WeakReference<ImageConst> wr = subImageHashMap.get(hash);
		if (wr!=null && wr.get()!=null){
			return pool.contains(wr.get());
		}
		return false;
	}
}
