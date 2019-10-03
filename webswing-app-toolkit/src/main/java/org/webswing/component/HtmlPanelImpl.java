package org.webswing.component;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.webswing.toolkit.api.action.WebActionEvent;
import org.webswing.toolkit.api.action.WebWindowActionListener;
import org.webswing.toolkit.api.component.HtmlPanel;
import org.webswing.toolkit.util.Util;

public class HtmlPanelImpl extends HtmlPanel {

	private static final long serialVersionUID = 3974466420979353544L;

	private HtmlWindow htmlWin;
	private List<WebWindowActionListener> webActionListeners = new ArrayList<>();
	
	public HtmlPanelImpl() {
		init();
	}
	
	private void init() {
		addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if (!Util.isCompositingWM() || htmlWin != null) {
					return;
				}
				
				if (isShowing()) {
					htmlWin = new HtmlWindow(SwingUtilities.getWindowAncestor(HtmlPanelImpl.this), HtmlPanelImpl.this);
					htmlWin.setVisible(true);
				}
			}
		});
	}
	
	@Override
	public final void handleWebActionEvent(WebActionEvent webActionEvent) {
		for (WebWindowActionListener listener : webActionListeners) {
			listener.actionPerformed(webActionEvent);
		}
	}
	
	@Override
	public void handleWindowInitialized() {
		for (WebWindowActionListener listener : webActionListeners) {
			listener.windowInitialized();
		}
	}
	
	public void addWebWindowActionListener(WebWindowActionListener listener) {
		webActionListeners.add(listener);
	}
	
	public void removeWebWindowActionListener(WebWindowActionListener listener) {
		webActionListeners.remove(listener);
	}
	
	public class HtmlWindow extends Window {

		private static final long serialVersionUID = 993484953134066624L;

		private Window owner;
		private HtmlPanel target;
		
		private HtmlWindow(Window owner, HtmlPanel target) throws HeadlessException {
			super(owner);
			
			this.target = target;
			this.owner = owner;
			
			init();
		}
		
		private void init() {
			setName(target.getName());
			
			target.addHierarchyListener(new HierarchyListener() {
				@Override
				public void hierarchyChanged(HierarchyEvent e) {
					if (!target.isShowing()) {
						setVisible(false);
					} else {
						setVisible(true);
						updateBounds();
					}
				}
			});
			target.addHierarchyBoundsListener(new HierarchyBoundsListener() {
				@Override
				public void ancestorResized(HierarchyEvent e) {
					updateBounds();
				}
				
				@Override
				public void ancestorMoved(HierarchyEvent e) {
					updateBounds();
				}
			});
			target.addComponentListener(new ComponentListener() {
				@Override
				public void componentShown(ComponentEvent e) {
				}
				
				@Override
				public void componentResized(ComponentEvent e) {
					updateBounds();
				}
				
				@Override
				public void componentMoved(ComponentEvent e) {
					updateBounds();
				}
				
				@Override
				public void componentHidden(ComponentEvent e) {
					setVisible(false);
				}
			});
			owner.addComponentListener(new ComponentListener() {
				@Override
				public void componentShown(ComponentEvent e) {
				}
				
				@Override
				public void componentResized(ComponentEvent e) {
					updateBounds();
				}
				
				@Override
				public void componentMoved(ComponentEvent e) {
					updateBounds();
				}
				
				@Override
				public void componentHidden(ComponentEvent e) {
					setVisible(false);
				}
			});
		}
		
		public HtmlPanel getTarget() {
			return target;
		}
		
		public void updateBounds() {
			if (!target.isShowing()) {
				return;
			}
			
			Point targetLocation = target.getLocationOnScreen();
			Dimension targetSize = target.getSize();
			
			Point ownerLocation = owner.getLocationOnScreen();
			Dimension ownerSize = owner.getSize();
			
			setLocation(targetLocation);
			
			setSize(Math.max(Math.min(ownerLocation.x + ownerSize.width, targetLocation.x + targetSize.width) - targetLocation.x, 0), 
					Math.max(Math.min(ownerLocation.y + ownerSize.height, targetLocation.y + targetSize.height) - targetLocation.y, 0));
		}
		
	}
	
}
