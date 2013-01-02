package sk.viktor.ignored.special;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.accessibility.Accessible;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class JComboBoxUIWrapper extends ComboBoxUI {

    BasicComboBoxUI original;

    public JComboBoxUIWrapper(JComboBox target) {
        try {
            // System.out.println((String) UIManager.get("ComboBoxUI"));
            Class<?> uiClass = UIManager.getDefaults().getUIClass("ComboBoxUI", JComboBox.class.getClassLoader());
            try {
                Method m;
                Class<?> acClass = javax.swing.JComponent.class;
                m = uiClass.getMethod("createUI", new Class[] { acClass });
                UIManager.put(uiClass, m);
                original = (BasicComboBoxUI) m.invoke(m, target);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ComponentUI createUI(JComponent c) {
        return new JComboBoxUIWrapper((JComboBox) c);
    }

    public boolean contains(JComponent c, int x, int y) {
        return original.contains(c, x, y);
    }

    public Accessible getAccessibleChild(JComponent c, int i) {
        return original.getAccessibleChild(c, i);
    }

    public int getAccessibleChildrenCount(JComponent c) {
        return original.getAccessibleChildrenCount(c);
    }

    public int getBaseline(JComponent c, int width, int height) {
        return original.getBaseline(c, width, height);
    }

    public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent c) {
        return original.getBaselineResizeBehavior(c);
    }

    public Dimension getMaximumSize(JComponent c) {
        return original.getMaximumSize(c);
    }

    public Dimension getMinimumSize(JComponent c) {
        return original.getMinimumSize(c);
    }

    public Dimension getPreferredSize(JComponent c) {
        return original.getPreferredSize(c);
    }

    public void installUI(JComponent c) {
        original.installUI(c);
        try {
            Field popupField = BasicComboBoxUI.class.getDeclaredField("popup");
            popupField.setAccessible(true);
            ComboPopup popup = (ComboPopup) popupField.get(original);
            popup = wrapPopup(popup);
            Method uninstallListenersMethod=BasicComboBoxUI.class.getDeclaredMethod("uninstallListeners");
            uninstallListenersMethod.setAccessible(true);
            uninstallListenersMethod.invoke(original);
            Method uninstallComponentsMethod=BasicComboBoxUI.class.getDeclaredMethod("uninstallComponents");
            uninstallComponentsMethod.setAccessible(true);
            uninstallComponentsMethod.invoke(original);
            popupField.set(original, popup);
            Method installListenersMethod=BasicComboBoxUI.class.getDeclaredMethod("installListeners");
            installListenersMethod.setAccessible(true);
            installListenersMethod.invoke(original);
            Method installComponentsMethod=BasicComboBoxUI.class.getDeclaredMethod("installComponents");
            installComponentsMethod.setAccessible(true);
            installComponentsMethod.invoke(original);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void paint(Graphics g, JComponent c) {
        original.paint(g, c);
    }

    public void uninstallUI(JComponent c) {
        original.uninstallUI(c);
    }

    public void update(Graphics g, JComponent c) {
        original.update(g, c);
    }

    @Override
    public void setPopupVisible(JComboBox c, boolean v) {
        original.setPopupVisible(c, v);
    }

    @Override
    public boolean isPopupVisible(JComboBox c) {
        return original.isPopupVisible(c);
    }

    @Override
    public boolean isFocusTraversable(JComboBox c) {
        return original.isFocusTraversable(c);
    }
    
    
     protected static ComboPopup wrapPopup(ComboPopup original) {
        try {
            Field cbField = BasicComboPopup.class.getDeclaredField("comboBox");
            cbField.setAccessible(true);
            return new ComboPopupWrapper((JComboBox) cbField.get(original));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
