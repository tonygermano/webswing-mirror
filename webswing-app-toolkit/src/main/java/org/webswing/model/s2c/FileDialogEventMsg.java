package org.webswing.model.s2c;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.webswing.Constants;
import org.webswing.model.Msg;

public class FileDialogEventMsg implements Msg {

	private static final long serialVersionUID = -7470385173647106699L;

	public enum FileDialogEventType {
		Open, Close;
	}

	public FileDialogEventType eventType;
	public boolean allowDownload;
	public boolean allowUpload;
	public boolean allowDelete;
	public String filter = "";
	public boolean isMultiSelection;

	public FileDialogEventMsg() {
		allowDownload = Boolean.valueOf(System.getProperty(Constants.SWING_START_SYS_PROP_ALLOW_DOWNLOAD));
		allowUpload = Boolean.valueOf(System.getProperty(Constants.SWING_START_SYS_PROP_ALLOW_UPLOAD));
		allowDelete = Boolean.valueOf(System.getProperty(Constants.SWING_START_SYS_PROP_ALLOW_DELETE));
	}

	private void addFilter(String arr[]) {
		for (String s : arr) {
			filter += "." + s + ", ";
		}
	}

	public void addFilter(FileFilter filterArr[]) {
		for (FileFilter ff : filterArr) {
			if (ff instanceof FileNameExtensionFilter) {
				FileNameExtensionFilter fe = (FileNameExtensionFilter) ff;
				addFilter(fe.getExtensions());
			}
		}
		if (filter.length() > 2)
			filter = filter.substring(0, filter.length() - 2);
	}

}
