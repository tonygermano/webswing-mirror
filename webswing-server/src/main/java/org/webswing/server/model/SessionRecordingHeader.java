package org.webswing.server.model;

import java.io.Serializable;
import java.util.Date;

import org.webswing.model.server.SwingDescriptor;

public class SessionRecordingHeader implements Serializable {
	public static final int version = 1;
	private static final long serialVersionUID = 3683092380648813794L;

	private Date startDate;
	private String clientId;
	private SwingDescriptor application;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public SwingDescriptor getApplication() {
		return application;
	}

	public void setApplication(SwingDescriptor application) {
		this.application = application;
	}

}