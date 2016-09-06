package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class TimestampsMsgIn implements MsgIn {
	private static final long serialVersionUID = 4931319415647368965L;

	private String startTimestamp;
	private String sendTimestamp;
	private String renderingTime;

	public String getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(String startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public String getSendTimestamp() {
		return sendTimestamp;
	}

	public void setSendTimestamp(String sendTimestamp) {
		this.sendTimestamp = sendTimestamp;
	}

	public String getRenderingTime() {
		return renderingTime;
	}

	public void setRenderingTime(String renderingTime) {
		this.renderingTime = renderingTime;
	}

}
