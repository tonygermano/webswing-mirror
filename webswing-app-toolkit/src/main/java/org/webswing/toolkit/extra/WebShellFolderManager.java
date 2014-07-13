package org.webswing.toolkit.extra;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import sun.awt.shell.ShellFolder;
import sun.awt.shell.ShellFolder.Invoker;
import sun.awt.shell.Win32ShellFolderManager2;

@SuppressWarnings("restriction")
public class WebShellFolderManager extends Win32ShellFolderManager2 {

    private boolean windows;
    private Object defaultManager;

    private File root;

    public WebShellFolderManager() {
        root = new IsolatedRootFile(System.getProperty("user.dir"));
        System.setProperty("user.home", root.getAbsolutePath());
        if (System.getProperty("os.name", "").startsWith("Windows")) {
            windows = true;
        }
        try {
            Class<?> managerClass = ClassLoader.getSystemClassLoader().loadClass("sun.awt.shell.ShellFolderManager");
            Constructor<?> c = managerClass.getDeclaredConstructor();
            c.setAccessible(true);
            defaultManager = c.newInstance();
        } catch (Exception e) {
            System.err.println("Error while instantiating default shell folder manager. " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Object get(String paramString) {
        if (paramString.equals("fileChooserDefaultFolder")) {
            return root;
        }
        if (paramString.equals("roots")) {
            return new File[] { root };
        }
        if (paramString.equals("fileChooserComboBoxFolders")) {
            return new File[] { root };
        }
        if (paramString.equals("fileChooserShortcutPanelFolders")) {
            return new File[] { root };
        }
        if (paramString.startsWith("fileChooserIcon ") || paramString.startsWith("optionPaneIcon ") || paramString.startsWith("shell32Icon ")) {
            return super.get(paramString);
        }
        return null;
    }

    @Override
    public ShellFolder createShellFolder(File paramFile) throws FileNotFoundException {
        try {
            if (paramFile.getCanonicalPath().startsWith(root.getCanonicalPath())) {
                if (windows) {
                    return super.createShellFolder(paramFile);
                } else {
                    try {
                        Method m = defaultManager.getClass().getDeclaredMethod("createShellFolder", File.class);
                        m.setAccessible(true);
                        return (ShellFolder) m.invoke(defaultManager, paramFile);
                    } catch (Exception e) {
                        System.err.println("Failed to invoke createShellFolder method on default shell folder manager: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error while creating ShellFolder. " + e.getMessage());
            return null;
        }
    }

    @Override
    protected Invoker createInvoker() {
        if (windows) {
            return super.createInvoker();
        } else {
            try {
                Method m = defaultManager.getClass().getDeclaredMethod("createInvoker");
                m.setAccessible(true);
                return (Invoker) m.invoke(defaultManager);
            } catch (Exception e) {
                System.err.println("Failed to invoke createInvoker method on default shell folder manager: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public boolean isComputerNode(File paramFile) {
        if (windows) {
            return super.isComputerNode(paramFile);
        } else {
            try {
                Method m = defaultManager.getClass().getDeclaredMethod("isComputerNode", File.class);
                m.setAccessible(true);
                return (Boolean) m.invoke(defaultManager, paramFile);
            } catch (Exception e) {
                System.err.println("Failed to invoke isComputerNode method on default shell folder manager: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public boolean isFileSystemRoot(File paramFile) {
        try {
            if (root.getCanonicalPath().equals(paramFile.getCanonicalPath())) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e1) {
            if (windows) {
                return super.isFileSystemRoot(paramFile);
            } else {
                try {
                    Method m = defaultManager.getClass().getDeclaredMethod("isFileSystemRoot", File.class);
                    m.setAccessible(true);
                    return (Boolean) m.invoke(defaultManager, paramFile);
                } catch (Exception e) {
                    System.err.println("Failed to invoke isFileSystemRoot method on default shell folder manager: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }
        }

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void sortFiles(List paramList) {
        if (windows) {
            super.sortFiles(paramList);
        } else {
            try {
                Method m = defaultManager.getClass().getDeclaredMethod("sortFiles", List.class);
                m.setAccessible(true);
                m.invoke(defaultManager, paramList);
            } catch (Exception e) {
                System.err.println("Failed to invoke sortFiles method on default shell folder manager: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
