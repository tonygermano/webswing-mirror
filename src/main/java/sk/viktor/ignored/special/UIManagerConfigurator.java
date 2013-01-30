package sk.viktor.ignored.special;

import javax.swing.UIManager;

public class UIManagerConfigurator {

    public static final String comboboxUI = "WebSwing.comboboxUiWrapper";

    public static void configureUI() {
        UIManager.put(comboboxUI, JComboBoxUIWrapper.class.getCanonicalName());

    }
}
