package org.webswing.model.s2c;

import org.webswing.model.Msg;

public class WindowSwitchMsg implements Msg {

	private static final long serialVersionUID = -523823816533325842L;
	
	private String id;
	private String title;
	private boolean modalBlocked;

	public WindowSwitchMsg() {
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isModalBlocked() {
		return modalBlocked;
	}

	public void setModalBlocked(boolean modalBlocked) {
		this.modalBlocked = modalBlocked;
	}

}
