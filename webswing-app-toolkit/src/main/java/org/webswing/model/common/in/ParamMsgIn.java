package org.webswing.model.common.in;

import org.webswing.model.CommonMsg;
import org.webswing.model.MsgIn;

public class ParamMsgIn implements MsgIn, CommonMsg {

	private static final long serialVersionUID = 3451224547948314923L;

	private String name;
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
