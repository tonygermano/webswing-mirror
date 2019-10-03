package org.webswing.model.s2c;

import java.awt.Component;
import java.awt.Frame;
import java.awt.IllegalComponentStateException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

import org.webswing.model.Msg;
import org.webswing.toolkit.util.ToolkitUtil;

public class ComponentTreeMsg implements Msg {

	private static final long serialVersionUID = -8433885502194335248L;
	
	private String componentType;
	private String name;
	private String value;
	private int screenX;
	private int screenY;
	private int width;
	private int height;
	private boolean enabled;
	private boolean visible;
	private Boolean selected;
	
	private boolean hidden; // custom property to flag components that should be hidden (e.g. JavaFX Region, Group, ...)
	
	private List<ComponentTreeMsg> components;
	
	public static ComponentTreeMsg fromComponent(Component c) {
		ComponentTreeMsg msg = new ComponentTreeMsg();
		
		msg.setComponentType(ToolkitUtil.getComponentType(c));
		msg.setName(c.getName());
		if (c instanceof Frame) {
			msg.setValue(((Frame) c).getTitle());
		}
		if (c instanceof AbstractButton) {
			msg.setValue(((AbstractButton) c).getText());
		}
		if (c instanceof JLabel) {
			msg.setValue(((JLabel) c).getText());
		}
		if (c instanceof JTextComponent) {
			msg.setValue(((JTextComponent) c).getText());
		}
		if (c instanceof JToggleButton) {
			msg.setSelected(((JToggleButton) c).isSelected());
		}
		if (c instanceof JComboBox) {
			JComboBox<?> combo = (JComboBox<?>) c;
			if (combo.getSelectedItem() != null) {
				msg.setValue(combo.getSelectedItem().toString());
			}
		}
		if (c instanceof JSlider) {
			msg.setValue(((JSlider) c).getValue() + "");
		}
		
		try {
			msg.setScreenX(c.getLocationOnScreen().x);
			msg.setScreenY(c.getLocationOnScreen().y);
		} catch (IllegalComponentStateException e) {
			// component not showing on the screen
		}
		msg.setWidth(c.getWidth());
		msg.setHeight(c.getHeight());
		msg.setEnabled(c.isEnabled());
		msg.setVisible(c.isVisible());
		
		return msg;
	}
	
	public void addChildComponent(ComponentTreeMsg child) {
		if (components == null) {
			components = new ArrayList<ComponentTreeMsg>();
		}
		
		components.add(child);
	}
	
	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getScreenX() {
		return screenX;
	}

	public void setScreenX(int screenX) {
		this.screenX = screenX;
	}

	public int getScreenY() {
		return screenY;
	}

	public void setScreenY(int screenY) {
		this.screenY = screenY;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public List<ComponentTreeMsg> getComponents() {
		return components;
	}

	public void setComponents(List<ComponentTreeMsg> components) {
		this.components = components;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	
}
