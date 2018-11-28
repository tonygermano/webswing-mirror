package org.webswing.demo.javafx;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ButtonHtml extends JPanel
        implements ActionListener {
        
    protected JButton b1, b2, b3;


    public ButtonHtml() {

        b1 = new JButton("<html><center><b><u>D</u>isable</b><br>"
                         + "<font color=#ffffdd>middle button</font>");
        Font font = b1.getFont().deriveFont(Font.PLAIN);
        b1.setFont(font);  
        b1.setVerticalTextPosition(AbstractButton.CENTER);
        b1.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        b1.setMnemonic(KeyEvent.VK_D);
        b1.setActionCommand("disable");
        
        b2 = new JButton("middle button");
        b2.setFont(font);
        b2.setForeground(new Color(0xffffdd));
        b2.setVerticalTextPosition(AbstractButton.BOTTOM);
        b2.setHorizontalTextPosition(AbstractButton.CENTER);
        b2.setMnemonic(KeyEvent.VK_M);
 

        b3 = new JButton("<html><center><b><u>E</u>nable</b><br>"
                         + "<font color=#ffffdd>middle button</font>");
        b3.setFont(font);
        //Use the default text position of CENTER, TRAILING (RIGHT).
        b3.setMnemonic(KeyEvent.VK_E);
        b3.setActionCommand("enable");
        b3.setEnabled(false);
 
        //Listen for actions on buttons 1 and 3.
        b1.addActionListener(this);
        b3.addActionListener(this);
 
        b1.setToolTipText("Click this button to disable the middle button.");
        b2.setToolTipText("This middle button does nothing when you click it.");
        b3.setToolTipText("Click this button to enable the middle button.");

        JComboBox<String> cb = new JComboBox<String>();


        cb.addItem("item 1");
        cb.addItem("item 2");
        cb.addItem("item 3");
        cb.addItem("item 4");


        //Add Components to this container, using the default FlowLayout.
        add(b1);
        add(b2);
        add(b3);
//        add(cb); //does not work in desktop version
        setBorder(BorderFactory.createTitledBorder("Swing Embeded in JavaFX via SwingNode"));
    }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        if ("disable".equals(e.getActionCommand())) {
            b2.setEnabled(false);
            b1.setEnabled(false);
            b3.setEnabled(true);
        } else {
            b2.setEnabled(true);
            b1.setEnabled(true);
            b3.setEnabled(false);
        }
    }
 

    /** Returns an ImageIcon, or null if the path was invalid.
     * @param path
     * @return  
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ButtonHtml.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}