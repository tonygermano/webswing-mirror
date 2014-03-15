package org.webswing.toolkit;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.AWTEventListener;
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
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.webswing.Constants;
import org.webswing.common.ImageServiceIfc;
import org.webswing.common.ServerConnectionIfc;
import org.webswing.common.SwingClassLoaderFactoryIfc;
import org.webswing.dispatch.WebEventDispatcher;
import org.webswing.dispatch.WebPaintDispatcher;
import org.webswing.toolkit.extra.WindowManager;
import org.webswing.toolkit.ge.WebGraphicsEnvironment;

import sun.awt.SunToolkit;

@SuppressWarnings("restriction")
public class WebToolkit extends SunToolkit {

    public static final String BACKGROUND_WINDOW_ID = "backgroundWindowId";
    private ServerConnectionIfc serverConnection;
    private ImageServiceIfc imageService;
    private SwingClassLoaderFactoryIfc webswingClassLoaderFactory;

    private WebEventDispatcher eventDispatcher = new WebEventDispatcher();
    private WebPaintDispatcher paintDispatcher;

    private WindowManager windowManager;

    public void init() {
        paintDispatcher = new WebPaintDispatcher(serverConnection, imageService);
        windowManager = WindowManager.getInstance();
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

            public void eventDispatched(AWTEvent event) {
                System.out.println(event);
            }

        }, AWTEvent.WINDOW_EVENT_MASK | AWTEvent.WINDOW_FOCUS_EVENT_MASK | AWTEvent.WINDOW_STATE_EVENT_MASK);

    }

    public void initSize(Integer desktopWidth, Integer desktopHeight) {
        screenWidth =desktopWidth;
        screenHeight = desktopHeight;
        displayChanged();
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

    public void setServerConnection(ServerConnectionIfc serverConnection) {
        this.serverConnection = serverConnection;
    }

    public void setImageService(ImageServiceIfc imageService) {
        this.imageService = imageService;
    }

    public ImageServiceIfc getImageService() {
        return imageService;
    }

    public SwingClassLoaderFactoryIfc getWebswingClassLoaderFactory() {
        return webswingClassLoaderFactory;
    }

    public void setWebswingClassLoaderFactory(SwingClassLoaderFactoryIfc webswingClassLoader) {
        this.webswingClassLoaderFactory = webswingClassLoader;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////// Toolkit Implementation//////////////////////////////////////////////////
    private static WebMouseInfoPeer mPeer;

    public WebToolkit() {

    }

    private static GraphicsConfiguration config;
    private Hashtable<String, FontPeer> cacheFontPeer;
    public static int screenWidth = Integer.parseInt(System.getProperty(Constants.SWING_SCREEN_WIDTH, Constants.SWING_SCREEN_WIDTH_MIN+""));
    public static int screenHeight = Integer.parseInt(System.getProperty(Constants.SWING_SCREEN_HEIGHT, Constants.SWING_SCREEN_HEIGHT_MIN+""));

    public static final Object targetToPeer(Object paramObject) {
        return SunToolkit.targetToPeer(paramObject);
    }

    public static final void targetDisposedPeer(Object paramObject1, Object paramObject2) {
        SunToolkit.targetDisposedPeer(paramObject1, paramObject2);
    }

    public boolean needUpdateWindow() {
        return true;
    }

    public boolean isTranslucencyCapable(GraphicsConfiguration paramGraphicsConfiguration) {
        return true;
    }

    @Override
    public KeyboardFocusManagerPeer createKeyboardFocusManagerPeer(KeyboardFocusManager paramKeyboardFocusManager) throws HeadlessException {
        return super.createKeyboardFocusManagerPeer(paramKeyboardFocusManager);
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
        throw new SecurityException("test");
    }

    public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
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
        //?
    }

    public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight) throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    public static void resetGC() {
        config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }

    public static void displayChanged() {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                ((WebGraphicsEnvironment) GraphicsEnvironment.getLocalGraphicsEnvironment()).displayChanged();
            }
        });
    }

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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    public boolean isTraySupported() {
        return false;
    }

    public RobotPeer createRobot(Robot paramRobot, GraphicsDevice paramGraphicsDevice) throws AWTException {
        throw new UnsupportedOperationException();
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

    public PrintJob getPrintJob(Frame paramFrame, String paramString, Properties paramProperties) {
        throw new UnsupportedOperationException();
    }

    public void beep() {

    }

    public GraphicsConfiguration getGraphicsConfig() {
        if (config == null) {
            resetGC();
        }
        return config;
    }

}
