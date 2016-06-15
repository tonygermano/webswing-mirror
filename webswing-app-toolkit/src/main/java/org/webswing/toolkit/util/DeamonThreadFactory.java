package org.webswing.toolkit.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DeamonThreadFactory implements ThreadFactory {

	private static ThreadFactory instance;

	public static ThreadFactory getInstance() {
		if(instance==null){
			instance = new DeamonThreadFactory();
		}
		return instance;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
	}
}
