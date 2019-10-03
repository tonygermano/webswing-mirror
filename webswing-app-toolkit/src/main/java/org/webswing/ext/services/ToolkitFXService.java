package org.webswing.ext.services;

import java.util.List;

import org.webswing.model.s2c.ComponentTreeMsg;

public interface ToolkitFXService {

	public List<ComponentTreeMsg> requestNodeTree(Object node);
	
	public void registerStage(Object stage);
	
}
