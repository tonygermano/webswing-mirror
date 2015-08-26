package org.webswing.demo.printing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.sun.swingset3.DemoProperties;

@DemoProperties(value = "Printing", category = "Webswing", description = "Demonstrates printing abilities of websinwg", sourceFiles = { "org/webswing/demo/printing/PaginationExample.java", "org/webswing/demo/printing/PrintableExample.java", "org/webswing/demo/printing/PrintJobExample.java" })
public class PrintingDemo extends JPanel {

	protected JComboBox orientation = new JComboBox(new String[] { "Portrait", "Landscape" });

	public PrintingDemo() {
		super(new BorderLayout());
		JTextArea text = new JTextArea(50, 20);
		for (int i = 1; i <= 50; i++) {
			text.append("Line " + i + "\n");
		}
		JScrollPane pane = new JScrollPane(text);
		add("Center", pane);
		JButton printButton = new JButton("Print (Printable interface)");
		printButton.addActionListener(new PrintableExample(this));
		JButton print2Button = new JButton("Print (Toolkit.getPrintJob)");
		print2Button.addActionListener(new PrintJobExample(this));
		JButton printButton2 = new JButton("Print Pages");
		printButton2.addActionListener(new PaginationExample(this));
		JPanel panel = new JPanel(new GridLayout(1, 5, 15, 0));
		panel.add(orientation);
		panel.add(printButton);
		panel.add(print2Button);
		panel.add(printButton2);
		add("South", panel);
	}

	public static void main(String[] args) {
		final JFrame f = new JFrame("Print UI Example");
		f.getContentPane().add(new PrintingDemo());
		f.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		f.pack();
		f.setVisible(true);
	}
}
