package org.webswing.demo.files;

import java.awt.BorderLayout;
import java.awt.Component;
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

@DemoProperties(value = "Files", category = "Webswing", description = "Demonstrates file handling.", sourceFiles = { "org/webswing/demo/files/FilesDemo.java" })
public class FilesDemo extends JPanel {

	protected JComboBox multiSelection = new JComboBox(new String[] { "True", "False" });
	protected JComboBox mode = new JComboBox(new String[] { "Files", "Directories", "Both" });

	public FilesDemo() {
		super(new BorderLayout());

		JPanel body = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JScrollPane pane = new JScrollPane(body);
		add("Center", pane);

		JPanel msPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		msPanel.add(new JLabel("MultiSelection:"));
		msPanel.add(multiSelection);

		JPanel smPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		smPanel.add(new JLabel("Selection mode:"));
		smPanel.add(mode);

		JPanel panel = new JPanel(new GridLayout(4, 2, 20, 0));
		panel.add(msPanel);
		panel.add(smPanel);
		panel.add(new JPanel());
		JButton filesBtn = new JButton("Open File Chooser");
		filesBtn.addActionListener(new OpenFileDialog(this));
		panel.add(filesBtn);
		//save dialog
		panel.add(new JPanel());
		JButton saveBtn = new JButton("Open Save Dialog");
		saveBtn.addActionListener(new SaveFileDialog(this));
		panel.add(saveBtn);
		add("South", panel);
	}

	public static void main(String[] args) {
		final JFrame f = new JFrame("Print UI Example");
		f.getContentPane().add(new FilesDemo());
		f.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		f.pack();
		f.setVisible(true);
	}
}
