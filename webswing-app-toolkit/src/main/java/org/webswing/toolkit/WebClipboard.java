package org.webswing.toolkit;

import java.awt.Desktop;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.webswing.util.Logger;
import org.webswing.util.Util;

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
      boolean htmlSupported=false;
      DataFlavor[] transferDataFlavors = contents.getTransferDataFlavors();
      for (DataFlavor df: transferDataFlavors) {
        htmlSupported|=df.getMimeType().startsWith("text/html");
      }
              
      if (htmlSupported) {
        try {
          Object transferData = contents.getTransferData(new DataFlavor("text/html;class=java.lang.String"));
          String htmlData=transferData.toString();
            try {
              File file = File.createTempFile("ws_", ".html");
              FileWriter fileWriter = new FileWriter(file);
              fileWriter.write(htmlData);
              fileWriter.flush();
              fileWriter.close();
              Logger.info("Wrote clipboard content to file: " + file.getAbsolutePath());
              
              WebDesktopPeer peer = new WebDesktopPeer(Desktop.getDesktop());
              peer.open(file);
              
              //@todo: Please can we call a JavaScript Dialog here, which renders the HTML content natively inside the browser
            } catch (IOException ex) {
              Logger.log(Logger.FATAL, "Write clipboard to file", ex);
            }
        } catch (Exception ex) {
          Logger.log(Logger.FATAL, "Get HTML transferable", ex);
        }
      } else if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
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
