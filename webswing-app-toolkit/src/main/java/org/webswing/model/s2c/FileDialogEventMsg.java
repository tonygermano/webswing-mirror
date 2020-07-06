package org.webswing.model.s2c;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.webswing.Constants;
import org.webswing.model.Msg;
import org.webswing.toolkit.api.file.WebswingFileChooserUtil;

public class FileDialogEventMsg implements Msg {

	private static final long serialVersionUID = -7470385173647106699L;

	public enum FileDialogEventType {
		AutoUpload,
		AutoSave,
		Open,
		Close;
	}

	private FileDialogEventType eventType;
	private boolean allowDownload;
	private boolean allowUpload;
	private boolean allowDelete;
	private boolean customDialog;
	private String selection;
	private String filter = "";
	private boolean isMultiSelection;

	public FileDialogEventMsg() {
		this(null);
	}

	public FileDialogEventMsg(JFileChooser chooser) {
		if (chooser != null) {
			Boolean downloadOverride = (Boolean) chooser.getClientProperty(WebswingFileChooserUtil.ALLOW_DOWNLOAD_OVERRIDE);
			Boolean uploadOverride = (Boolean) chooser.getClientProperty(WebswingFileChooserUtil.ALLOW_UPLOAD_OVERRIDE);
			Boolean deleteOverride = (Boolean) chooser.getClientProperty(WebswingFileChooserUtil.ALLOW_DELETE_OVERRIDE);
			allowDownload = downloadOverride != null ? downloadOverride : Boolean.valueOf(System.getProperty(Constants.SWING_START_SYS_PROP_ALLOW_DOWNLOAD));
			allowUpload = uploadOverride != null ? uploadOverride : Boolean.valueOf(System.getProperty(Constants.SWING_START_SYS_PROP_ALLOW_UPLOAD));
			allowDelete = deleteOverride != null ? deleteOverride : Boolean.valueOf(System.getProperty(Constants.SWING_START_SYS_PROP_ALLOW_DELETE));
			Boolean customDialog = (Boolean) chooser.getClientProperty(WebswingFileChooserUtil.CUSTOM_FILE_CHOOSER);
			this.customDialog = customDialog != null ? customDialog : false;
		}
	}

	public void addFilter(FileFilter[] filterArr) {
		for (FileFilter ff : filterArr) {
			if (ff instanceof FileNameExtensionFilter) {
				FileNameExtensionFilter fe = (FileNameExtensionFilter) ff;
				for (String s : fe.getExtensions()) {
					filter += "." + s + ", ";
				}
			}
		}
		if (filter.length() > 2)
			filter = filter.substring(0, filter.length() - 2);
	}

	public FileDialogEventType getEventType() {
		return eventType;
	}

	public void setEventType(FileDialogEventType eventType) {
		this.eventType = eventType;
	}

	public boolean isAllowDownload() {
		return allowDownload;
	}

	public void setAllowDownload(boolean allowDownload) {
		this.allowDownload = allowDownload;
	}

	public boolean isAllowUpload() {
		return allowUpload;
	}

	public void setAllowUpload(boolean allowUpload) {
		this.allowUpload = allowUpload;
	}

	public boolean isAllowDelete() {
		return allowDelete;
	}

	public void setAllowDelete(boolean allowDelete) {
		this.allowDelete = allowDelete;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public boolean isMultiSelection() {
		return isMultiSelection;
	}

	public void setMultiSelection(boolean isMultiSelection) {
		this.isMultiSelection = isMultiSelection;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public boolean isCustomDialog() {
		return customDialog;
	}

	public void setCustomDialog(boolean customDialog) {
		this.customDialog = customDialog;
	}
}
