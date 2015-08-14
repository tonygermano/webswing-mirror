package org.webswing.toolkit;

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.JobAttributes;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PageAttributes;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethodDescriptor;
import java.awt.image.ColorModel;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.MouseInfoPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.TrayIconPeer;
import java.awt.peer.WindowPeer;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import org.webswing.Constants;
import org.webswing.dispatch.WebEventDispatcher;
import org.webswing.dispatch.WebPaintDispatcher;
import org.webswing.toolkit.extra.WebRepaintManager;
import org.webswing.toolkit.extra.WindowManager;
import org.webswing.toolkit.util.Logger;
import org.webswing.toolkit.util.Util;

import sun.awt.SunToolkit;
import sun.awt.image.SurfaceManager;
import sun.java2d.SurfaceData;
import sun.print.PrintJob2D;
import sun.awt.datatransfer.DataTransferer;

@SuppressWarnings("restriction")
public abstract class WebToolkit extends SunToolkit {

	public static final String BACKGROUND_WINDOW_ID = "BG";

	private WebEventDispatcher eventDispatcher = new WebEventDispatcher();
	private WebPaintDispatcher paintDispatcher = new WebPaintDispatcher();

	private WindowManager windowManager = WindowManager.getInstance();

	public void init() {
		RepaintManager.setCurrentManager(new WebRepaintManager(RepaintManager.currentManager(null)));

		try {
			if (!System.getProperty("os.name", "").startsWith("Windows") && !System.getProperty("os.name", "").startsWith("Mac")) {
				Class<?> c = ClassLoader.getSystemClassLoader().loadClass("sun.awt.X11GraphicsEnvironment");
				Method initDisplayMethod = c.getDeclaredMethod("initDisplay", Boolean.TYPE);
				initDisplayMethod.setAccessible(true);
				initDisplayMethod.invoke(null, false);
			}
		} catch (Exception e) {
			Logger.error("Failed to init X11 display: ", e.getMessage());
		}
	}

	public void initSize(final Integer desktopWidth, final Integer desktopHeight) {
		int oldWidht = screenWidth;
		int oldHeight = screenHeight;
		screenWidth = desktopWidth;
		screenHeight = desktopHeight;
		displayChanged();
		resetGC();
		Util.resetWindowsGC(screenWidth, screenHeight);
		getPaintDispatcher().clientReadyToReceive();
		getPaintDispatcher().resetWindowsPosition(oldWidht, oldHeight);// in case windows moved out of screen by resizing screen.
		getPaintDispatcher().notifyWindowRepaintAll();
	}

	protected WindowManager getWindowManager() {
		return windowManager;
	}

	public WebEventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	public WebPaintDispatcher getPaintDispatcher() {
		return paintDispatcher;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////// Toolkit Implementation//////////////////////////////////////////////////
	private static WebMouseInfoPeer mPeer;

	public WebToolkit() {
		if (System.getProperty("java.version").startsWith("1.6") || System.getProperty("java.version").startsWith("1.7")) {
			try {
				Method m = SunToolkit.class.getDeclaredMethod("setDataTransfererClassName", String.class);
				m.setAccessible(true);
				m.invoke(null, "org.webswing.toolkit.WebDataTransfer");
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	private static GraphicsConfiguration config;
	private Hashtable<String, FontPeer> cacheFontPeer;
	private Clipboard clipboard;
	private Clipboard selectionClipboard;
	public static int screenWidth = Integer.parseInt(System.getProperty(Constants.SWING_SCREEN_WIDTH, Constants.SWING_SCREEN_WIDTH_MIN + ""));
	public static int screenHeight = Integer.parseInt(System.getProperty(Constants.SWING_SCREEN_HEIGHT, Constants.SWING_SCREEN_HEIGHT_MIN + ""));

	public static final Object targetToPeer(Object paramObject) {
		return SunToolkit.targetToPeer(paramObject);
	}

	public static final void targetDisposedPeer(Object paramObject1, Object paramObject2) {
		SunToolkit.targetDisposedPeer(paramObject1, paramObject2);
	}

	@Override
	protected void initializeDesktopProperties() {
		if (System.getProperty(Constants.SWING_START_SYS_PROP_ISOLATED_FS, "").equalsIgnoreCase("true")) {
			this.desktopProperties.put("Shell.shellFolderManager", "org.webswing.toolkit.extra.WebShellFolderManager");
		} else {
			if (System.getProperty("os.name", "").startsWith("Windows")) {
				this.desktopProperties.put("Shell.shellFolderManager", "sun.awt.shell.Win32ShellFolderManager2");
			}
		}
		this.desktopProperties.put("win.highContrast.on", Boolean.FALSE);
		this.desktopProperties.put("win.xpstyle.themeActive", Boolean.TRUE);
	}

	public boolean needUpdateWindow() {
		return true;
	}

	public boolean isTranslucencyCapable(GraphicsConfiguration paramGraphicsConfiguration) {
		return true;
	}

	public KeyboardFocusManagerPeer createKeyboardFocusManagerPeer(KeyboardFocusManager paramKeyboardFocusManager) throws HeadlessException {
		return new WebKeyboardFocusManagerPeer();
	}

	public FramePeer createFrame(Frame frame) throws HeadlessException {
		WebFramePeer localWFramePeer = new WebFramePeer(frame);
		targetCreatedPeer(frame, localWFramePeer);
		return localWFramePeer;
	}

	public DialogPeer createDialog(Dialog paramDialog) throws HeadlessException {
		WebDialogPeer localdialogPeer = new WebDialogPeer(paramDialog);
		targetCreatedPeer(paramDialog, localdialogPeer);
		return localdialogPeer;
	}

	public WindowPeer createWindow(Window paramWindow) throws HeadlessException {
		WebWindowPeer localwindowPeer = new WebWindowPeer(paramWindow);
		targetCreatedPeer(paramWindow, localwindowPeer);
		return localwindowPeer;
	}

	@Override
	protected synchronized MouseInfoPeer getMouseInfoPeer() {
		{
			if (mPeer == null) {
				mPeer = new WebMouseInfoPeer();
			}
			return mPeer;
		}
	}

	public FontPeer getFontPeer(String paramString, int paramInt) {
		FontPeer localObject = null;
		String str = paramString.toLowerCase();
		if (null != this.cacheFontPeer) {
			localObject = this.cacheFontPeer.get(str + paramInt);
			if (null != localObject) {
				return localObject;
			}
		}
		localObject = new WebFontPeer(paramString, paramInt);
		if (localObject != null) {
			if (null == this.cacheFontPeer) {
				this.cacheFontPeer = new Hashtable<String, FontPeer>(5, 0.9F);
			}
			if (null != this.cacheFontPeer) {
				this.cacheFontPeer.put(str + paramInt, localObject);
			}
		}
		return ((FontPeer) localObject);
	}

	public Clipboard getSystemClipboard() throws HeadlessException {
		synchronized (this) {
			if (this.clipboard == null) {
				this.clipboard = new WebClipboard("default", true);
			}
		}
		return this.clipboard;
	}

	public Clipboard getSystemSelection() throws HeadlessException {
		synchronized (this) {
			if (this.selectionClipboard == null) {
				this.selectionClipboard = new WebClipboard("selection", false);
			}
		}
		return this.selectionClipboard;
	}

	@Override
	protected Object lazilyLoadDesktopProperty(String name) {
		if ("awt.font.desktophints".equals(name)) {
			return SunToolkit.getDesktopFontHints();
		}

		return super.lazilyLoadDesktopProperty(name);
	}

	@Override
	protected RenderingHints getDesktopAAHints() {
		RenderingHints hints = new RenderingHints(null);
		hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, 140);
		return hints;

	}

	public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException {
		return WebDragSourceContextPeer.createDragSourceContextPeer(paramDragGestureEvent);
	}

	@SuppressWarnings("unchecked")
	public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> type, DragSource dragSource, Component component, int act, DragGestureListener listener) {
		if (MouseDragGestureRecognizer.class.equals(type)) {
			return (T) new WebMouseDragGestureRecognizer(dragSource, component, act, listener);
		}
		return null;
	}

	protected int getScreenWidth() {
		return screenWidth;
	}

	protected int getScreenHeight() {
		return screenHeight;
	}

	public int getScreenResolution() throws HeadlessException {
		return 72;
	}

	public ColorModel getColorModel() throws HeadlessException {
		if (config == null) {
			resetGC();
		}
		return config.getColorModel();
	}

	public void sync() {
	}

	public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight) throws HeadlessException {
		return null;
	}

	public static void resetGC() {
		config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}

	public abstract void displayChanged();

	// EventQueue.invokeLater(new Runnable() {
	//
	// public void run() {
	// ((WebGraphicsEnvironment) GraphicsEnvironment.getLocalGraphicsEnvironment()).displayChanged();
	// }
	// });
	// }

	public InputMethodDescriptor getInputMethodAdapterDescriptor() throws AWTException {
		return new WebInputMethodDescriptor();
	}

	protected boolean syncNativeQueue() {
		return true;
	}

	public void grab(Window paramWindow) {
	}

	public void ungrab(Window paramWindow) {
	}

	public ButtonPeer createButton(Button paramButton) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public TextFieldPeer createTextField(TextField paramTextField) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public ChoicePeer createChoice(Choice paramChoice) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public LabelPeer createLabel(Label paramLabel) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public ListPeer createList(List paramList) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public CheckboxPeer createCheckbox(Checkbox paramCheckbox) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public ScrollbarPeer createScrollbar(Scrollbar paramScrollbar) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public ScrollPanePeer createScrollPane(ScrollPane paramScrollPane) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public TextAreaPeer createTextArea(TextArea paramTextArea) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public FileDialogPeer createFileDialog(FileDialog paramFileDialog) throws HeadlessException {
		return new WebFileDialogPeer(paramFileDialog);
	}

	public MenuBarPeer createMenuBar(MenuBar paramMenuBar) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public MenuPeer createMenu(Menu paramMenu) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public MenuItemPeer createMenuItem(MenuItem paramMenuItem) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem) throws HeadlessException {
		throw new UnsupportedOperationException();
	}

	public TrayIconPeer createTrayIcon(TrayIcon paramTrayIcon) throws HeadlessException, AWTException {
		throw new UnsupportedOperationException();
	}

	public SystemTrayPeer createSystemTray(SystemTray paramSystemTray) {
		return new WebSystemTrayPeer();
	}

	public boolean isTraySupported() {
		return false;
	}

	public RobotPeer createRobot(Robot paramRobot, GraphicsDevice paramGraphicsDevice) throws AWTException {
		return null;
	}

	public boolean isDesktopSupported() {
		return true;
	}

	public boolean isWindowOpacityControlSupported() {
		return true;
	}

	public boolean isWindowShapingSupported() {
		return false;
	}

	public boolean isWindowTranslucencySupported() {
		return true;
	}

	protected DesktopPeer createDesktopPeer(Desktop paramDesktop) throws HeadlessException {
		return new WebDesktopPeer(paramDesktop);
	}

	public PrintJob getPrintJob(Frame frame, String jobtitle, JobAttributes jobAttributes, PageAttributes pageAttributes) {
		PrintJob2D localPrintJob2D = new PrintJob2D(frame, jobtitle, jobAttributes, pageAttributes);

		if (!localPrintJob2D.printDialog()) {
			localPrintJob2D = null;
		}

		return localPrintJob2D;
	}

	public PrintJob getPrintJob(Frame frame, String jobtitle, Properties paramProperties) {
		return getPrintJob(frame, jobtitle, null, null);
	}

	public void beep() {

	}

	@Override
	public boolean getLockingKeyState(int keyCode) throws UnsupportedOperationException {
		return false;
	}

	public GraphicsConfiguration getGraphicsConfig() {
		if (config == null) {
			resetGC();
		}
		return config;
	}

	public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
		return true;
	}

	// update system colors to win7 default theme (light)
	protected void loadSystemColors(int[] systemColors) throws HeadlessException {
		if (systemColors != null && systemColors.length == 26) {
			systemColors[0] = 0xff000000;
			systemColors[1] = 0xff99b4d1;
			systemColors[2] = 0xff000000;
			systemColors[3] = 0xffb4b4b4;
			systemColors[4] = 0xffbfcddb;
			systemColors[5] = 0xff434e54;
			systemColors[6] = 0xfff4f7fc;
			systemColors[7] = 0xffffffff;
			systemColors[8] = 0xff646464;
			systemColors[9] = 0xff000000;
			systemColors[10] = 0xfff0f0f0;
			systemColors[11] = 0xff000000;
			systemColors[12] = 0xffffffff;
			systemColors[13] = 0xff000000;
			systemColors[14] = 0xff3399ff;
			systemColors[15] = 0xffffffff;
			systemColors[16] = 0xff6d6d6d;
			systemColors[17] = 0xfff0f0f0;
			systemColors[18] = 0xff000000;
			systemColors[19] = 0xffe3e3e3;
			systemColors[20] = 0xffffffff;
			systemColors[21] = 0xffa0a0a0;
			systemColors[22] = 0xff696969;
			systemColors[23] = 0xffc8c8c8;
			systemColors[24] = 0xffffffe1;
			systemColors[25] = 0xff000000;
		}
	}

	abstract public boolean webConpoenentPeerUpdateGraphicsData();

	abstract public SurfaceData webComponentPeerReplaceSurfaceData(SurfaceManager mgr);

	public void exitSwing(final int i) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				System.exit(i);
			}
		});

	}
}
