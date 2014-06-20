package org.webswing.toolkit;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;

import org.webswing.toolkit.ge.WebGraphicsEnvironment;

public class WebToolkit6 extends WebToolkit {

    @Override
    public void displayChanged() {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                ((WebGraphicsEnvironment) GraphicsEnvironment.getLocalGraphicsEnvironment()).displayChanged();
            }
        });
    }

}
