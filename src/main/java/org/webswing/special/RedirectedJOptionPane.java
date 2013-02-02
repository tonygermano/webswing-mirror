package org.webswing.special;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.UIManager;

import org.webswing.containers.WebJDialog;


public class RedirectedJOptionPane {

    public static java.lang.String showInputDialog(java.lang.Object message) {
        return showInputDialog(null, message);
    }

    public static java.lang.String showInputDialog(java.lang.Object message, java.lang.Object initialSelectionValue) {
        return showInputDialog(null, message, initialSelectionValue);
    }

    public static java.lang.String showInputDialog(java.awt.Component parentComponent, java.lang.Object message) {
        return showInputDialog(parentComponent, message, "", JOptionPane.QUESTION_MESSAGE);
    }

    public static java.lang.String showInputDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.Object initialSelectionValue) {
        return (String) showInputDialog(parentComponent, message, "", JOptionPane.QUESTION_MESSAGE, null, null, initialSelectionValue);
    }

    public static java.lang.String showInputDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int messageType) {
        return (String) showInputDialog(parentComponent, message, title, messageType, null, null, null);
    }

    @SuppressWarnings("deprecation")
    public static java.lang.Object showInputDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int messageType, javax.swing.Icon icon, java.lang.Object[] selectionValues, java.lang.Object initialSelectionValue) {
        System.out.println("showInputDialog");
        JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.OK_CANCEL_OPTION, icon, null, null);

        pane.setWantsInput(true);
        pane.setSelectionValues(selectionValues);
        pane.setInitialSelectionValue(initialSelectionValue);
        pane.setComponentOrientation(((parentComponent == null) ? JOptionPane.getRootFrame() : parentComponent).getComponentOrientation());

        int style = styleFromMessageType(messageType);
        JDialog dialog = createDialog(pane, parentComponent, title, style);

        pane.selectInitialValue();
        dialog.show();
        dialog.dispose();

        Object value = pane.getInputValue();

        if (value == JOptionPane.UNINITIALIZED_VALUE) {
            return null;
        }
        return value;
    }

    public static void showMessageDialog(java.awt.Component parentComponent, java.lang.Object message) {
        showMessageDialog(parentComponent, message, "", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showMessageDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int messageType) {
        showMessageDialog(parentComponent, message, title, messageType, null);
    }

    public static void showMessageDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int messageType, javax.swing.Icon icon) {
        showOptionDialog(parentComponent, message, title, JOptionPane.DEFAULT_OPTION, messageType, icon, null, null);
    }

    public static int showConfirmDialog(java.awt.Component parentComponent, java.lang.Object message) {
        return showConfirmDialog(parentComponent, message, "", JOptionPane.YES_NO_CANCEL_OPTION);
    }

    public static int showConfirmDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int optionType) {
        return showConfirmDialog(parentComponent, message, title, optionType, JOptionPane.QUESTION_MESSAGE);
    }

    public static int showConfirmDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int optionType, int messageType) {
        return showConfirmDialog(parentComponent, message, title, optionType, messageType, null);
    }

    public static int showConfirmDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int optionType, int messageType, javax.swing.Icon icon) {
        return showOptionDialog(parentComponent, message, title, optionType, messageType, icon, null, null);
    }

    @SuppressWarnings("deprecation")
    public static int showOptionDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int optionType, int messageType, javax.swing.Icon icon, java.lang.Object[] options, java.lang.Object initialValue) {
        JOptionPane pane = new JOptionPane(message, messageType, optionType, icon, options, initialValue);

        pane.setInitialValue(initialValue);
        pane.setComponentOrientation(((parentComponent == null) ? JOptionPane.getRootFrame() : parentComponent).getComponentOrientation());

        int style = styleFromMessageType(messageType);
        JDialog dialog =createDialog( pane,parentComponent, title, style);

        pane.selectInitialValue();
        dialog.show();
        dialog.dispose();

        Object selectedValue = pane.getValue();

        if (selectedValue == null)
            return JOptionPane.CLOSED_OPTION;
        if (options == null) {
            if (selectedValue instanceof Integer)
                return ((Integer) selectedValue).intValue();
            return JOptionPane.CLOSED_OPTION;
        }
        for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
            if (options[counter].equals(selectedValue))
                return counter;
        }
        return JOptionPane.CLOSED_OPTION;
    }

    public static void showInternalMessageDialog(java.awt.Component arg0, java.lang.Object arg1) {
    }

    public static void showInternalMessageDialog(java.awt.Component arg0, java.lang.Object arg1, java.lang.String arg2, int arg3) {
    }

    public static void showInternalMessageDialog(java.awt.Component arg0, java.lang.Object arg1, java.lang.String arg2, int arg3, javax.swing.Icon arg4) {
    }

    public static int showInternalConfirmDialog(java.awt.Component parentComponent, java.lang.Object message) {
        return showInternalConfirmDialog(parentComponent, message,"", JOptionPane.YES_NO_CANCEL_OPTION);
    }

    public static int showInternalConfirmDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int optionType) {
        return showInternalConfirmDialog(parentComponent, message, title, optionType, JOptionPane.QUESTION_MESSAGE);
    }

    public static int showInternalConfirmDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int optionType, int messageType) {
        return showInternalConfirmDialog(parentComponent, message, title, optionType, messageType, null);
    }

    public static int showInternalConfirmDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int optionType, int messageType, javax.swing.Icon icon) {
        return showInternalOptionDialog(parentComponent, message, title, optionType, messageType, icon, null, null);
    }

    public static int showInternalOptionDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int optionType, int messageType, javax.swing.Icon icon, java.lang.Object[] options, java.lang.Object initialValue) {
       return showOptionDialog(parentComponent, message, title, optionType, messageType, icon, options, initialValue);
    }

    public static java.lang.String showInternalInputDialog(java.awt.Component parentComponent, java.lang.Object message) {
        return showInternalInputDialog(parentComponent, message, "", JOptionPane.QUESTION_MESSAGE);
    }

    public static java.lang.String showInternalInputDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int messageType) {
        return (String)showInternalInputDialog(parentComponent, message, title,
                messageType, null, null, null);
    }

    public static java.lang.Object showInternalInputDialog(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title, int messageType, javax.swing.Icon icon, java.lang.Object[] selectionValues, java.lang.Object initialSelectionValue) {
        return showInputDialog(parentComponent, message, title, messageType, icon, selectionValues, initialSelectionValue);
    }

    public static void initDialog(final JOptionPane pane, final JDialog dialog, int style, Component parentComponent) {
        dialog.setComponentOrientation(pane.getComponentOrientation());
        Container contentPane = dialog.getContentPane();

        contentPane.setLayout(new BorderLayout());
        contentPane.add(pane, BorderLayout.CENTER);
        dialog.setResizable(false);
        if (JDialog.isDefaultLookAndFeelDecorated()) {
            boolean supportsWindowDecorations = UIManager.getLookAndFeel().getSupportsWindowDecorations();
            if (supportsWindowDecorations) {
                dialog.setUndecorated(true);
                pane.getRootPane().setWindowDecorationStyle(style);
            }
        }
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        WindowAdapter adapter = new WindowAdapter() {

            private boolean gotFocus = false;

            public void windowClosing(WindowEvent we) {
                pane.setValue(null);
            }

            public void windowGainedFocus(WindowEvent we) {
                // Once window gets focus, set initial focus
                if (!gotFocus) {
                    pane.selectInitialValue();
                    gotFocus = true;
                }
            }
        };
        dialog.addWindowListener(adapter);
        dialog.addWindowFocusListener(adapter);
        dialog.addComponentListener(new ComponentAdapter() {

            public void componentShown(ComponentEvent ce) {
                // reset value to ensure closing works properly
                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            }
        });
        pane.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent event) {
                // Let the defaultCloseOperation handle the closing
                // if the user closed the window without selecting a button
                // (newValue = null in that case).  Otherwise, close the dialog.
                if (dialog.isVisible() && event.getSource() == pane && (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) && event.getNewValue() != null && event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
                    dialog.setVisible(false);
                }
            }
        });
    }

    static Window getWindowForComponent(Component parentComponent) throws HeadlessException {
        if (parentComponent == null)
            return JOptionPane.getRootFrame();
        if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
            return (Window) parentComponent;
        return getWindowForComponent(parentComponent.getParent());
    }

    public  static JDialog createDialog(JOptionPane pane, Component parentComponent, String title, int style) throws HeadlessException {

        final JDialog dialog;

        Window window = getWindowForComponent(parentComponent);
        if (window instanceof Frame) {
            dialog = new WebJDialog((Frame) window, title, true);
        } else {
            dialog = new WebJDialog((Dialog) window, title, true);
        }

        initDialog(pane, dialog, style, parentComponent);
        return dialog;
    }

    public static int styleFromMessageType(int messageType) {
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE:
                return JRootPane.ERROR_DIALOG;
            case JOptionPane.QUESTION_MESSAGE:
                return JRootPane.QUESTION_DIALOG;
            case JOptionPane.WARNING_MESSAGE:
                return JRootPane.WARNING_DIALOG;
            case JOptionPane.INFORMATION_MESSAGE:
                return JRootPane.INFORMATION_DIALOG;
            case JOptionPane.PLAIN_MESSAGE:
            default:
                return JRootPane.PLAIN_DIALOG;
        }
    }

}
