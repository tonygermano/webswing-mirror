package org.webswing.demo.window;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.border.LineBorder;

import com.sun.swingset3.DemoProperties;

@DemoProperties(value = "Windows", category = "Webswing", description = "Demonstrates handling of various window decoration options", sourceFiles = { "org/webswing/demo/window/WindowDecorationDemo.java" })
public class WindowDecorationDemo extends JPanel {

	static int count = 0;

	Point pos = new Point();
	int id = count++;
	JCheckBox parentCB = new JCheckBox("Parent", true);
	JCheckBox undecoCB = new JCheckBox("Undecorated");
	JCheckBox ontopCB = new JCheckBox("Always on top");
	JCheckBox modalCB = new JCheckBox("Modal");
	Window thisWindow;
	private Point initialClick;

	public WindowDecorationDemo() {
		this(null, null, null, false, false);
	}

	public WindowDecorationDemo(Window owner, Window parent, Point pos, boolean undecorated, boolean ontop) {
		thisWindow = owner;
		JPanel info = new JPanel(new FlowLayout());
		info.add(new JLabel("Id:" + id));
		info.add(new JLabel("Parent:" + getID(parent)));
		info.add(new JLabel("Modal:" + (owner instanceof JDialog ? ((JDialog) owner).isModal() : "false")));
		info.add(new JLabel("Always on top:" + (owner == null ? "N" : owner.isAlwaysOnTop())));

		if (owner == null) {
			parentCB.setSelected(false);
		}
		if (undecorated) {
			undecoCB.setSelected(true);
		}
		ontopCB.setSelected(ontop);
		if (pos != null) {
			this.pos = new Point(pos.x + 10, pos.y + 10);
		}
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		JPanel checkboxes = new JPanel(new FlowLayout());
		checkboxes.add(parentCB);
		checkboxes.add(undecoCB);
		checkboxes.add(ontopCB);
		checkboxes.add(modalCB);
		JPanel buttonsPanel = new JPanel(new FlowLayout());
		JButton frame = new JButton("new Frame");
		frame.setToolTipText("This is a veeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeery long tooltip text");
		frame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame(WindowDecorationDemo.this.pos, undecoCB.isSelected(), ontopCB.isSelected());
				WindowDecorationDemo.this.pos.y += 10;
			}
		});
		buttonsPanel.add(frame);
		JButton dialog = new JButton("new Dialog");
		dialog.setToolTipText("This is a veeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeery long tooltip text");
		dialog.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog(parentCB.isSelected() ? thisWindow : null, WindowDecorationDemo.this.pos, WindowDecorationDemo.this.undecoCB.isSelected(), ontopCB.isSelected(), modalCB.isSelected());
				WindowDecorationDemo.this.pos.y += 10;
			}
		});
		buttonsPanel.add(dialog);
		JButton window = new JButton("new Window");
		window.setToolTipText("This is a veeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeery long tooltip text");
		window.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				window(parentCB.isSelected() ? thisWindow : null, WindowDecorationDemo.this.pos, ontopCB.isSelected());
				WindowDecorationDemo.this.pos.y += 10;
			}
		});
		buttonsPanel.add(window);
		JPanel additional = new JPanel(new FlowLayout());
		//********************LABELS**************//
		//context menu
		JLabel context = new JLabel("Right click for context menu");
		context.setBorder(BorderFactory.createLineBorder(Color.black));
		JPopupMenu menu = new JPopupMenu("test");
		menu.add(new JMenuItem("test"));
		menu.add(new JMenuItem("test"));
		menu.add(new JMenuItem("test"));
		menu.add(new JMenuItem("test"));
		menu.add(new JMenuItem("test"));
		menu.add(new JMenuItem("test"));
		menu.add(new JMenuItem("test"));
		menu.add(new JMenuItem("test"));
		menu.add(new JMenuItem("test"));
		context.setComponentPopupMenu(menu);

		//move window
		JLabel moveButton = new JLabel("Move window by dragging this area");
		moveButton.setBorder(BorderFactory.createLineBorder(Color.black));
		moveButton.addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				Window parent = thisWindow;
				// get location of Window
				int thisX = parent.getLocation().x;
				int thisY = parent.getLocation().y;

				// Determine how much the mouse moved since the initial click
				int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
				int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

				// Move window to this position
				int X = thisX + xMoved;
				int Y = thisY + yMoved;
				parent.setLocation(X, Y);
			}
		});
		moveButton.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				initialClick = e.getPoint();
				getComponentAt(initialClick);
			}
		});
		JLabel maximize = new JLabel("maximize");
		maximize.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				((JFrame) thisWindow).setExtendedState(JFrame.MAXIMIZED_BOTH);
			}
		});
		JLabel close = new JLabel("close");
		close.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				thisWindow.dispose();
			}
		});
		if (thisWindow != null) {
			content.add(info);
		}
		content.add(checkboxes);
		content.add(buttonsPanel);
		if (thisWindow != null) {
			additional.add(moveButton);
			additional.add(context);
			if (thisWindow instanceof JFrame) {
				additional.add(maximize);
			}
			additional.add(close);
			content.add(additional);
		}
		add(content);
		setBorder(new LineBorder(Color.BLACK));
	}

	private static String getID(Window parent) {
		if (parent == null) {
			return "N";
		} else {
			if (parent instanceof JFrame) {
				return ((WindowDecorationDemo) ((JFrame) parent).getContentPane().getComponent(0)).id + "";
			}

			if (parent instanceof JDialog) {
				return ((WindowDecorationDemo) ((JDialog) parent).getContentPane().getComponent(0)).id + "";
			}

			if (parent instanceof JWindow) {
				return ((WindowDecorationDemo) ((JWindow) parent).getContentPane().getComponent(0)).id + "";
			}

		}
		return null;
	}

	public static JFrame frame(Point position, boolean undecorated, boolean ontop) {
		JFrame frame = new JFrame("Demo") {
			@Override
			public String toString() {
				return "Me:" + getID(this) + " Par:" + getID((Window) this.getParent());
			}
		};
		frame.setLocation(position);
		frame.setAlwaysOnTop(ontop);
		frame.setUndecorated(undecorated);
		WindowDecorationDemo panel = new WindowDecorationDemo(frame, null, position, undecorated, ontop);
		frame.getContentPane().add(panel);

		frame.pack();
		frame.setVisible(true);
		return frame;
	}

	public static void dialog(Window owner, Point position, boolean undecorated, boolean ontop, boolean modal) {
		JDialog frame = new JDialog(owner) {
			@Override
			public String toString() {
				return "Me:" + getID(this) + " Par:" + getID((Window) this.getParent());
			}
		};
		frame.setModal(modal);
		frame.setAlwaysOnTop(ontop);
		frame.setUndecorated(undecorated);
		WindowDecorationDemo panel = new WindowDecorationDemo(frame, owner, position, undecorated, ontop);
		frame.getContentPane().add(panel);
		frame.setLocation(position);

		frame.pack();
		frame.setVisible(true);
	}

	public static void window(Window owner, Point position, boolean ontop) {
		JWindow frame = new JWindow(owner) {
			@Override
			public String toString() {
				return "Me:" + getID(this) + " Par:" + getID((Window) this.getParent());
			}
		};
		frame.setLocation(position);
		frame.setAlwaysOnTop(ontop);
		WindowDecorationDemo panel = new WindowDecorationDemo(frame, owner, position, false, ontop);
		frame.getContentPane().add(panel);

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		JFrame f = frame(new Point(0, 0), false, false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
