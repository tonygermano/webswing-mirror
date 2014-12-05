package org.webswing.directdraw;

import org.webswing.toolkit.directdraw.instructions.DrawConstantPool;

public class DirectDraw {

	private DirectDrawServicesAdapter services = new DirectDrawServicesAdapter();
	private DrawConstantPool constantPool = new DrawConstantPool();

	public DirectDraw() {
	}

	public DirectDraw(DirectDrawServicesAdapter services) {
		this.services = services;
	}

	public void resetConstantCache() {
		constantPool = new DrawConstantPool();
	}

	public DrawConstantPool getConstantPool() {
		return constantPool;
	}

    public DirectDrawServicesAdapter getServices() {
        return services;
    }
	
}
