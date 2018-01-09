package org.webswing.toolkit;

import java.applet.*;
import java.awt.*;
import java.awt.peer.*;

import org.webswing.dispatch.*;
import org.webswing.toolkit.extra.*;
import org.webswing.toolkit.ge.*;
import sun.awt.*;
import sun.awt.datatransfer.*;
import sun.awt.image.*;
import sun.java2d.*;

public class WebToolkit9 extends WebToolkit {
    private KeyboardFocusManagerPeer kfmp;
    private WindowManager9 windowManager = WindowManager9.getInstance();
    private WebEventDispatcher9 eventDispatcher = new WebEventDispatcher9();
    
    @Override
    public void displayChanged() {
        EventQueue.invokeLater(new Runnable() {
            
            public void run() {
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                WebGraphicsEnvironment wge = (WebGraphicsEnvironment) ge;
                wge.displayChanged();
            }
        });
    }
    
    @Override
    public KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() throws HeadlessException {
        if (kfmp == null) {
            kfmp = new WebKeyboardFocusManagerPeer();
        }
        return kfmp;
    }
    
    @Override
    protected boolean syncNativeQueue(long paramLong) {
        return false;
    }
    
    @Override
    public boolean webConpoenentPeerUpdateGraphicsData() {
        ((WebGraphicsEnvironment) GraphicsEnvironment.getLocalGraphicsEnvironment()).displayChanged();
        return true;
    }
    
    @Override
    public SurfaceData webComponentPeerReplaceSurfaceData(SurfaceManager mgr) {
        return mgr.getPrimarySurfaceData();
    }
    
    @Override
    public DataTransferer getDataTransferer() {
        return WebDataTransfer.getInstanceImpl();
    }
    
    @Override
    public FramePeer createLightweightFrame(LightweightFrame arg0)
            throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean isTaskbarSupported() {
        return true;
    }
    
    @Override
    public WindowManager9 getWindowManager() {
        return windowManager;
    }
    
    @Override
    public DialogPeer createDialog(Dialog paramDialog) throws HeadlessException {
        WebDialogPeer9 localdialogPeer = new WebDialogPeer9(paramDialog);
        targetCreatedPeer(paramDialog, localdialogPeer);
        return localdialogPeer;
    }
    
    @Override
    public WindowPeer createWindow(Window paramWindow) throws HeadlessException {
        WebWindowPeer9 localwindowPeer = new WebWindowPeer9(paramWindow);
        targetCreatedPeer(paramWindow, localwindowPeer);
        return localwindowPeer;
    }
    
    @Override
    public FileDialogPeer createFileDialog(FileDialog paramFileDialog) throws HeadlessException {
        return new WebFileDialogPeer9(paramFileDialog);
    }
    
    @Override
    public FramePeer createFrame(Frame frame) throws HeadlessException {
        WebFramePeer9 localWFramePeer = new WebFramePeer9(frame);
        targetCreatedPeer(frame, localWFramePeer);
        return localWFramePeer;
    }
    
    @Override
    public PanelPeer createPanel(Panel panel) {
        if (panel instanceof Applet) {
            return super.createPanel(panel);
        }
        WebPanelPeer9 localpanelPeer = new WebPanelPeer9(panel);
        targetCreatedPeer(panel, localpanelPeer);
        return localpanelPeer;
    }
    
    @Override
    public Dimension getScreenSize() {
        return new Dimension(screenWidth, screenHeight);
    }
    
    public WebEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
}
