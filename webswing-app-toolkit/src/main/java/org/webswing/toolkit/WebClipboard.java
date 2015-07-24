package org.webswing.toolkit;

import java.awt.Desktop;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.webswing.toolkit.util.Logger;
import org.webswing.toolkit.util.Util;

public class WebClipboard extends Clipboard {
	private final boolean isSystemClipboard;
	private final ClipboardOwner owner = new ClipboardOwner() {

		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {
		}
	};

	public WebClipboard(String name, boolean isSystemClipboard) {
		super(name);
		this.isSystemClipboard = isSystemClipboard;
	}

	public void setContent(String text) {
		Transferable t = new StringSelection(text);
		super.setContents(t, owner);
	}

	@Override
	public synchronized void setContents(Transferable contents, ClipboardOwner owner) {
		super.setContents(contents, owner);
		if (isSystemClipboard) {
			String html = null;
			String text = null;
			try {
				if (contents.isDataFlavorSupported(new DataFlavor("text/html;class=java.lang.String"))) {
					Object transferData = contents.getTransferData(new DataFlavor("text/html;class=java.lang.String"));
					html = transferData.toString();
				}
			} catch (Exception e) {
				Logger.error("WebClipboard:setContent:HTML", e);
			}
			if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				try {
					text = (String) contents.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception e) {
					Logger.error("WebClipboard:setContent:Plain", e);
				}
			}
			Util.getWebToolkit().getPaintDispatcher().notifyCopyEvent(text, html);
		}
	}
}
