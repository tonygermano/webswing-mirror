package org.webswing.model.s2c;

import java.io.Serializable;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.webswing.Constants;

public class JsonFileDialogEvent implements Serializable {

	private static final long serialVersionUID = -6782940433721602460L;

	public enum FileDialogEventType {
		Open, Close;
	}

	public JsonFileDialogEvent() {
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

	public FileDialogEventType eventType;
	public boolean allowDownload;
	public boolean allowUpload;
	public boolean allowDelete;
	public String filter = "";
	public boolean isMultiSelection;

}
