package org.webswing.toolkit;

import org.webswing.applet.WebAppletContext;
import org.webswing.toolkit.ge.WebGraphicsEnvironment;
import org.webswing.toolkit.util.Logger;
import org.webswing.toolkit.util.Util;
import sun.awt.CausedFocusEvent;
import sun.awt.LightweightFrame;
import sun.awt.SunToolkit;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.image.SurfaceManager;
import sun.java2d.SurfaceData;

import java.applet.Applet;
import java.awt.*;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.lang.reflect.Method;

@SuppressWarnings("restriction")
public class WebToolkit8 extends WebToolkit {

    private KeyboardFocusManagerPeer kfmp;

    @Override
    WebFramePeer createWebFramePeer(Frame frame) throws HeadlessException {
        return new WebFramePeer8(frame);
    }

    @Override
    WebDialogPeer createWebDialogPeer(Dialog paramDialog) {
        return new WebDialogPeer8(paramDialog);
    }

    @Override
    WebWindowPeer createWebWindowPeer(Window paramWindow) {
        return new WebWindowPeer8(paramWindow);
    }

    @Override
    WebPanelPeer createWebPanelPeer(Panel panel) {
        return new WebPanelPeer8(panel);
    }

    @Override
    WebFileDialogPeer createWebFileDialogPeer(FileDialog paramFileDialog) {
        return new WebFileDialogPeer8(paramFileDialog);
    }


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
        WebLWFramePeer8 localWFramePeer = new WebLWFramePeer8(arg0);
        return localWFramePeer;
	}

    static{
        for(FocusEventCause cause:FocusEventCause.values()){
            assert CausedFocusEvent.Cause.valueOf(cause.name())!=null;
        }
    }

    public int shouldNativelyFocusHeavyweight(Component heavyweight, Component descendant, boolean temporary, boolean focusedWindowChangeAllowed, long time, FocusEventCause cause) {
        try {
            Method m2 = KeyboardFocusManager.class.getDeclaredMethod("shouldNativelyFocusHeavyweight", Component.class, Component.class, Boolean.TYPE, Boolean.TYPE, Long.TYPE, CausedFocusEvent.Cause.class);
            m2.setAccessible(true);
            Integer result2 = (Integer) m2.invoke(null, heavyweight, descendant, temporary, focusedWindowChangeAllowed, time, CausedFocusEvent.Cause.valueOf(cause.name()));
            return result2;
        } catch (Exception e) {
            Logger.debug("Failed to invoke processSynchronousLightweightTransfer on KeyboardFocusManager. Check your java version.", e);
            return 0;
        }
    }

    @SuppressWarnings("deprecation")
    public boolean deliverFocus(Component heavyweight, Component descendant, boolean temporary, boolean focusedWindowChangeAllowed, long time, FocusEventCause cause) {
        if (heavyweight == null) {
            heavyweight = descendant;
        }

        Component c = Util.getWebToolkit().getWindowManager().getActiveWindow().getFocusOwner();
        CausedFocusEvent focusEvent;
        if ((c != null) && (c.getPeer() == null)) {
            c = null;
        }
        if (c != null) {
            focusEvent = new CausedFocusEvent(c, CausedFocusEvent.FOCUS_LOST, false, heavyweight, CausedFocusEvent.Cause.valueOf(cause.name()));

            SunToolkit.postEvent(SunToolkit.targetToAppContext(c), focusEvent);
        }

        focusEvent = new CausedFocusEvent(heavyweight, CausedFocusEvent.FOCUS_GAINED, false, c, CausedFocusEvent.Cause.valueOf(cause.name()));

        SunToolkit.postEvent(SunToolkit.targetToAppContext(heavyweight), focusEvent);
        return true;
    }

    public static boolean requestFocus(Object target, Component paramComponent, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause paramCause) {
        if (target instanceof Window) {
            return Util.getWebToolkit().getWindowManager().activateWindow((Window) target, paramComponent, 0, 0, temporary, focusedWindowChangeAllowed, FocusEventCause.getValue(paramCause));
        } else if (target instanceof Applet) {
            Applet applet = (Applet) target;
            Window window = ((WebAppletContext) applet.getAppletContext()).getContainer();
            return Util.getWebToolkit().getWindowManager().activateWindow(window, paramComponent, 0, 0, temporary, focusedWindowChangeAllowed, FocusEventCause.getValue(paramCause));
        } else if (target instanceof Panel){
            return Util.getWebToolkit().getWindowManager().deliverFocus((Panel)target, paramComponent, temporary, FocusEventCause.getValue(paramCause));
        } else {
            return false;
        }
    }
}
