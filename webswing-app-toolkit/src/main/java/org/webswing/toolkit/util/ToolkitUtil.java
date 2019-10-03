package org.webswing.toolkit.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import org.webswing.model.s2c.ComponentTreeMsg;

public class ToolkitUtil {
	
	/**
	 * Return the whole component tree (Swing) and node tree (JavaFX), if available.
	 */
	public static List<ComponentTreeMsg> getComponentTree() {
		List<ComponentTreeMsg> tree = new ArrayList<ComponentTreeMsg>();
		
		tree.addAll(getComponentTree(null));
		
		if (Services.getToolkitFXService() != null) {
			tree.addAll(getNodeTree());
		}
		
		return tree;
	}
	
	/**
	 * Return the given Component's Swing component tree from ToolkitService.
	 */
	public static List<ComponentTreeMsg> getComponentTree(Component root) {
		List<ComponentTreeMsg> componentTree = new ArrayList<ComponentTreeMsg>();
		
		if (root == null) {
			Window[] windows = Util.getAllWindows();
			for (Window w : windows) {
				componentTree.add(createComponentTreeMsg(w));
			}
		} else {
			componentTree.add(createComponentTreeMsg(root));
		}
		
		return componentTree;
	}
	
	/**
	 * Return the whole JavaFX node tree from ToolkitFXService.
	 */
	public static List<ComponentTreeMsg> getNodeTree() {
		return getNodeTree(null);
	}
	
	/**
	 * Return the given Node's JavaFX node tree from ToolkitFXService.
	 */
	public static List<ComponentTreeMsg> getNodeTree(Object node) {
		return Services.getToolkitFXService().requestNodeTree(node);
	}
	
	public static String getComponentType(Object o) {
		Class<?> cls = o.getClass();
		
		if (cls.isAnonymousClass()) {
			cls = cls.getInterfaces().length == 0 ? cls.getSuperclass() : cls.getInterfaces()[0];
		}
		
		return cls.getSimpleName();
	}
	
	private static ComponentTreeMsg createComponentTreeMsg(Component c) {
		ComponentTreeMsg msg = ComponentTreeMsg.fromComponent(c);
		
		if (getComponentType(c).equalsIgnoreCase("JFXPanel")) {
			msg.setComponents(getNodeTree(c));
		} else if (c instanceof Container) {
			synchronized (c.getTreeLock()) {
				Component[] children = ((Container) c).getComponents();
				if (children != null && children.length > 0) {
					for (Component child : children) {
						msg.addChildComponent(createComponentTreeMsg(child));
					}
				}
			}
		}
		
		return msg;
	}

}
