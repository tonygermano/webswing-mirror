package org.webswing.toolkit;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import org.webswing.toolkit.util.Logger;
import org.webswing.toolkit.util.Util;

public class WebClipboard extends Clipboard {

    private boolean isSystemClipboard;
    private ClipboardOwner owner = new ClipboardOwner() {

        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
        }
    };

    public WebClipboard(String name, boolean isSystemClipboard) {
        super(name);
        this.isSystemClipboard = isSystemClipboard;
    }

    public void setContent(String text) {
        Transferable t= new StringSelection(text);
        super.setContents(t, owner);
    }

    @Override
    public synchronized void setContents(Transferable contents, ClipboardOwner owner) {
        super.setContents(contents, owner);
        if (isSystemClipboard) {
            if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    String content = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    Util.getWebToolkit().getPaintDispatcher().notifyCopyEvent(content);
                } catch (Exception e) {
                    Logger.error("WebClipboard:setContent", e);
                }
            }
        }
    }
}
