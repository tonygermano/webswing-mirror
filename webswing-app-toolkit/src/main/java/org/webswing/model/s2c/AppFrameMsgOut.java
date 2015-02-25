package org.webswing.model.s2c;

import java.util.ArrayList;
import java.util.List;

import org.webswing.model.MsgOut;

public class AppFrameMsgOut implements MsgOut {

	private static final long serialVersionUID = 6019708608380425820L;

	private String type = "app";
	public List<ApplicationInfoMsg> applications;
	public LinkActionMsg linkAction;
	public WindowMoveActionMsg moveAction;
	public CursorChangeEventMsg cursorChange;
	public String user;
	public CopyEventMsg copyEvent;
	public FileDialogEventMsg fileDialogEvent;
	public List<WindowMsg> windows;
	public WindowMsg closedWindow;
	public SimpleEventMsgOut event;

	public LinkActionMsg getLinkAction() {
		return linkAction;
	}

	public void setLinkAction(LinkActionMsg linkAction) {
		this.linkAction = linkAction;
	}

	public List<WindowMsg> getWindows() {
		return windows;
	}

	public void setWindows(List<WindowMsg> windows) {
		this.windows = windows;
	}

	public WindowMsg getOrCreateWindowById(String guid) {
		if (windows != null) {
			for (WindowMsg w : windows) {
				if (w.getId().equals(guid)) {
					return w;
				}
			}
		} else {
			windows = new ArrayList<WindowMsg>();
		}
		WindowMsg window = new WindowMsg();
		window.setId(guid);
		windows.add(window);
		return window;
	}

	public String getType() {
		return type;
	}

}
