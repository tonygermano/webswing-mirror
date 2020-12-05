package org.webswing.toolkit.extra;

import sun.awt.shell.PublicDefaultShellFolder;
import sun.awt.shell.ShellFolder;

import java.io.File;

public class WebswingShellFolder extends PublicDefaultShellFolder {
    public WebswingShellFolder(ShellFolder parent, File f) {
        super(parent, f);
    }

    public WebswingShellFolder(File f) {
        super(null, f);
    }

    @Override
    public boolean renameTo(File file) {
        if (file == null || !isSameDirectory(this, file)) return false;
        return super.renameTo(file);
    }

    private boolean isSameDirectory(File file1, File file2) {
        if (file1 == null || file2 == null) return false;
        return file1.getParentFile().getAbsolutePath().equals(file2.getParentFile().getAbsolutePath());
    }
}
