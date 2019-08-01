package org.webswing.demo.timezone;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sun.swingset3.DemoProperties;

@DemoProperties(value = "TimeZone", category = "Webswing", description = "Demonstrates time zone setting from client's browser", sourceFiles = { "org/webswing/demo/timezone/TimeZoneDemo.java" })
public class TimeZoneDemo extends JPanel {
	private static final long serialVersionUID = 8550928872207603286L;

	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss z");
	
	public TimeZoneDemo() {
		super(new BorderLayout());
		setPreferredSize(new Dimension(200, 200));
		
		JLabel time = new JLabel();
		time.setHorizontalAlignment(JLabel.CENTER);
		add("Center", time);
		
		JLabel note = new JLabel("<html><p>Use -Duser.timezone=${clientTimeZone} in JVM Arguments configuration to resolve client's browser locale in Webswing application.</p></html>");
		add("South", note);
		
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(() -> {
					time.setText(sdf.format(new Date()));
				});
			}
		}, 0, 500);
	}

	public static void main(String[] args) {
		final JFrame f = new JFrame("TimeZone Example");
		f.getContentPane().add(new TimeZoneDemo());
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		f.pack();
		f.setVisible(true);
	}
}
