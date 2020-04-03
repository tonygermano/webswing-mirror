package org.webswing.javafx.toolkit;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import org.webswing.ext.services.ToolkitFXService;
import org.webswing.javafx.toolkit.adaper.WindowAdapter;
import org.webswing.model.s2c.ComponentTreeMsg;
import org.webswing.toolkit.util.Logger;
import org.webswing.toolkit.util.ToolkitUtil;

import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Labeled;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class ToolkitFXServiceImpl implements ToolkitFXService {

	private static ToolkitFXServiceImpl impl;
	
	private Set<Stage> stages = Collections.synchronizedSet(new HashSet<>());
	private List<String> ignoreChildren = Arrays.asList("Button", "CheckBox", "ChoiceBox", "ColorPicker", "ComboBox", "DatePicker", "Label", "PasswordField", "ProgressBar", "RadioButton", "Slider", "Spinner", "SplitMenuButton", "TextArea", "TextField", "ToggleButton");
	
	public static ToolkitFXServiceImpl getInstance() {
		if (impl == null) {
			impl = new ToolkitFXServiceImpl();
		}
		return impl;
	}

	private ToolkitFXServiceImpl() {
	}
	
	@Override
	public void registerStage(Object stage) {
		if (stage instanceof Stage) {
			stages.add((Stage) stage);
		}
	}
	
	@Override
	public List<ComponentTreeMsg> requestNodeTree(Object node) {
		List<ComponentTreeMsg> componentTree = new ArrayList<ComponentTreeMsg>();
		
		if (node != null && node instanceof Node) {
			componentTree.add(createComponentTreeMsg((Node) node));
		} else if (node != null && node instanceof JFXPanel) {
			componentTree.add(createComponentTreeMsg(((JFXPanel) node).getScene().getRoot()));
		} else {
			stages.forEach(stage -> componentTree.add(createComponentTreeMsg(stage.getScene().getRoot())));
		}
		
		return componentTree;
	}
	
	@Override
	public boolean isFXWindow(Window window) {
		return window instanceof WindowAdapter;
	}
	
	private ComponentTreeMsg createComponentTreeMsg(Node n) {
		ComponentTreeMsg msg = createComponentTreeMsgFromNode(n);
		
		if (ignoreChildren.contains(ToolkitUtil.getComponentType(n))) {
			return msg;
		}
		
		if (n instanceof Parent) {
			((Parent) n).getChildrenUnmodifiable().forEach(child -> msg.addChildComponent(createComponentTreeMsg(child)));
		}
		
		if (n instanceof SwingNode) {
			JComponent swingComponent = ((SwingNode) n).getContent();
			if (swingComponent != null) {
				try {
					msg.setComponents(ToolkitUtil.getComponentTree(swingComponent));
				} catch (Exception e) {
					Logger.warn("Error while creating Swing component tree for SwingNode!", e);
				}
			}
		}
		
		return msg;
	}
	
	private ComponentTreeMsg createComponentTreeMsgFromNode(Node n) {
		ComponentTreeMsg msg = new ComponentTreeMsg();
		
		msg.setComponentType(ToolkitUtil.getComponentType(n));
		msg.setName(n.getId());
		msg.setEnabled(!n.isDisabled());
		msg.setVisible(n.isVisible());
		
		boolean transparent = n.getOpacity() == 0d;
		msg.setHidden(transparent);
		
		if (n instanceof TextInputControl) {
			msg.setValue(((TextInputControl) n).getText());
		}
		
		if (n instanceof Labeled) {
			msg.setValue(((Labeled) n).getText());
		}
		
		if (n instanceof Slider) {
			msg.setValue(((Slider) n).getValue() + "");
		}
		
		if (n instanceof Spinner<?>) {
			Spinner<?> spinner = (Spinner<?>) n;
			if (spinner.getValue() != null) {
				msg.setValue(spinner.getValue().toString());
			}
		}
		
		if (n instanceof ComboBoxBase<?>) {
			ComboBoxBase<?> combo = (ComboBoxBase<?>) n;
			if (combo.getValue() != null) {
				msg.setValue(combo.getValue().toString());
			}
		}
		
		if (n instanceof ChoiceBox<?>) {
			ChoiceBox<?> choicebox = (ChoiceBox<?>) n;
			if (choicebox.getValue() != null) {
				msg.setValue(choicebox.getValue().toString());
			}
		}
		
		if (n instanceof ToggleButton) {
			msg.setSelected(((ToggleButton) n).isSelected());
		}
		
		if (n instanceof CheckBox) {
			msg.setSelected(((CheckBox) n).isSelected());
		}
		
		Bounds bounds = n.localToScreen(n.getBoundsInLocal());
		
		msg.setScreenX((int) bounds.getMinX());
		msg.setScreenY((int) bounds.getMinY());
		msg.setWidth((int) bounds.getWidth());
		msg.setHeight((int) bounds.getHeight());
		
		return msg;
	}

}
