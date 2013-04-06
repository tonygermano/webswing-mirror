package org.webswing.special;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.accessibility.AccessibleContext;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JRootPane;
import javax.swing.UIManager;

import org.webswing.containers.WebJDialog;


public class OverridenMethods {

    public static JDialog createDialog(JFileChooser t, Component parent) {
        String title = t.getUI().getDialogTitle(t);
        t.putClientProperty(AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY, title);

        JDialog dialog;
        Window window = RedirectedJOptionPane.getWindowForComponent(parent);
        if (window instanceof Frame) {
            dialog = new WebJDialog((Frame) window, title, true);
        } else {
            dialog = new WebJDialog((Dialog) window, title, true);
        }
        dialog.setComponentOrientation(t.getComponentOrientation());

        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(t, BorderLayout.CENTER);

        if (JDialog.isDefaultLookAndFeelDecorated()) {
            boolean supportsWindowDecorations = UIManager.getLookAndFeel().getSupportsWindowDecorations();
            if (supportsWindowDecorations) {
                dialog.getRootPane().setWindowDecorationStyle(JRootPane.FILE_CHOOSER_DIALOG);
            }
        }
        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        return dialog;
    }
    
}
