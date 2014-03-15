package org.webswing.toolkit;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.peer.DesktopPeer;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.webswing.util.Util;

public class WebDesktopPeer implements DesktopPeer {


    public WebDesktopPeer(Desktop d) {
    }

    @Override
    public boolean isSupported(Action action) {
        switch (action) {
            case BROWSE:
            case MAIL:
                return true;
            case EDIT:
            case OPEN:
            case PRINT:
            default:
                return false;
        }

    }

    @Override
    public void open(File file) throws IOException {
        //not supported yet
    }

    @Override
    public void edit(File file) throws IOException {
        //not supported yet
    }

    @Override
    public void print(File file) throws IOException {
        //TODO:
    }

    @Override
    public void mail(URI mailtoURL) throws IOException {
        Util.getWebToolkit().getPaintDispatcher().notifyOpenLinkAction(mailtoURL);
    }

    @Override
    public void browse(URI url) throws IOException {
        Util.getWebToolkit().getPaintDispatcher().notifyOpenLinkAction(url);
    }
}
