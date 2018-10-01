package org.webswing.toolkit.extra;

import org.webswing.Constants;
import org.webswing.toolkit.util.Logger;
import sun.awt.shell.PublicShellFolderManager;
import sun.awt.shell.ShellFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("restriction")
public class IsolatedFsShellFolderManager extends PublicShellFolderManager {

	private static File root;
	private static List<File> roots = new ArrayList<File>();

	static {
		String path = System.getProperty(Constants.SWING_START_SYS_PROP_TRANSFER_DIR, System.getProperty("user.dir") + "/upload");
		String[] paths = path.split(File.pathSeparator);
		for (int i = 0; i < paths.length; i++) {
			File root = new IsolatedRootFile(paths[i]);
			if (!root.getAbsoluteFile().exists()) {
				root.mkdirs();
			}
			roots.add(root);
			if (i == 0) {
				IsolatedFsShellFolderManager.root = root;
				System.setProperty("user.home", root.getAbsolutePath());
			}
		}
	}

	@Override
	public Object get(String paramString) {
		if (paramString.equals("fileChooserDefaultFolder")) {
			return ensureExists(root);
		}
		if (paramString.equals("roots")) {
			return ensureExists(roots.toArray(new File[roots.size()]));
		}
		if (paramString.equals("fileChooserComboBoxFolders")) {
			return ensureExists(roots.toArray(new File[roots.size()]));
		}
		if (paramString.equals("fileChooserShortcutPanelFolders")) {
			return ensureExists(roots.toArray(new File[roots.size()]));
		}
		if (paramString.startsWith("fileChooserIcon ") || paramString.startsWith("optionPaneIcon ") || paramString.startsWith("shell32Icon ")) {
			return super.get(paramString);
		}
		return null;
	}

	private File[] ensureExists(File[] roots) {
		for (File f : roots) {
			ensureExists(f);
		}
		return roots;
	}

	private File ensureExists(File f) {
		if (!f.getAbsoluteFile().exists()) {
			boolean done = f.mkdirs();
			if (done) {
				Logger.error("Isolated filesystem folder " + f.getAbsolutePath() + "not found. Make sure the folder is unique for each session (use ${user} variable) or disable the 'Clear Upload Folder' option in configuration.");
			} else {
				Logger.error("Isolated filesystem folder " + f.getAbsolutePath() + " could not be created. Make sure the path is valid and the process has write access.");
			}
		}
		return f;
	}

	@Override
	public ShellFolder createShellFolder(File paramFile) throws FileNotFoundException {
		try {
			if (isSubfolderOfRoots(paramFile)) {
				return super.createShellFolder(paramFile);
			} else {
				throw new FileNotFoundException("Path is outside the allowed Webswing Filesystem isolation folder. (" + paramFile.getCanonicalPath() + ")");
			}
		} catch (IOException e) {
			System.err.println("Error while creating ShellFolder. " + e.getMessage());
			if (e instanceof FileNotFoundException) {
				throw (FileNotFoundException) e;
			} else {
				throw new FileNotFoundException("Error while creating ShellFolder. " + e.getMessage());
			}
		}
	}

	public static boolean isSubfolderOfRoots(File paramFile) throws IOException {
		if(Boolean.getBoolean(Constants.SWING_START_SYS_PROP_ISOLATED_FS)) {
			String cp = paramFile.getCanonicalPath();
			for (File root : roots) {
				if (cp.startsWith(root.getCanonicalPath())) {
					return true;
				}
			}
		}
		return false;
	}


	@Override
	public boolean isFileSystemRoot(File paramFile) {
		try {
			for (File root : roots) {
				if (root.getCanonicalPath().equals(paramFile.getCanonicalPath())) {
					return true;
				}
			}
			return false;
		} catch (IOException e1) {
			return super.isFileSystemRoot(paramFile);
		}
	}
}
