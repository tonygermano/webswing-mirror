package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.peer.ComponentPeer;
import java.lang.reflect.Field;
import javax.swing.JComponent;
import javax.swing.RepaintManager;

@TranslucentRepaintManager
public class RepaintManagerX extends RepaintManager {
    public RepaintManagerX() {
    }

    public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        Rectangle dirtyRegion = this.getDirtyRegion(c);
        if (dirtyRegion.width == 0 && dirtyRegion.height == 0) {
            int lastDeltaX = c.getX();
            int lastDeltaY = c.getY();

            for(Container parent = c.getParent(); parent instanceof JComponent; parent = parent.getParent()) {
                if (!parent.isVisible() || getPeer(parent) == null) {
                    return;
                }

                if (parent instanceof JXPanel && (((JXPanel)parent).getAlpha() < 1.0F || !parent.isOpaque())) {
                    x += lastDeltaX;
                    y += lastDeltaY;
                    lastDeltaY = 0;
                    lastDeltaX = 0;
                    c = (JComponent)parent;
                }

                lastDeltaX += parent.getX();
                lastDeltaY += parent.getY();
            }
        }

        super.addDirtyRegion(c, x, y, w, h);
    }

    public static ComponentPeer getPeer(Component positionWin) {
        try {
            Field peer = Component.class.getDeclaredField("peer");
            peer.setAccessible(true);
            return (ComponentPeer) peer.get(positionWin);
        } catch (Exception e) {
            return null;
        }
    }
}