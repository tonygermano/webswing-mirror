package org.webswing.server.stats.jmx;

import java.util.Date;

import org.webswing.model.admin.s2c.JsonSwingSession;

public class SessionDetails {
	JsonSwingSession sessionInfo;

	public SessionDetails(JsonSwingSession sessionInfo) {
		this.sessionInfo = sessionInfo;
	}

	public String getId() {
		return sessionInfo.getId();
	}

	public String getUser() {
		return sessionInfo.getUser();
	}

	public String getApplication() {
		return sessionInfo.getApplication();
	}

	public Date getStartedAt() {
		return sessionInfo.getStartedAt();
	}

	public Boolean getConnected() {
		return sessionInfo.getConnected();
	}

	public Date getDisconnectedSince() {
		return sessionInfo.getDisconnectedSince();
	}

	public Date getEndedAt() {
		return sessionInfo.getEndedAt();
	}

	public double getHeapSize() {
		return sessionInfo.getState().getHeapSize();
	}

	public double getHeapSizeUsed() {
		return sessionInfo.getState().getHeapSizeUsed();
	}

	public Date getSnapshotTime() {
		return sessionInfo.getState().getSnapshotTime();
	}

	public long getInboundDataSizeSum() {
		return sessionInfo.getState().getInboundDataSizeSum();
	}

	public long getInboundMsgCount() {
		return sessionInfo.getState().getInboundMsgCount();
	}

	public long getOutboundDataSizeSum() {
		return sessionInfo.getState().getOutboundDataSizeSum();
	}

	public long getOutboundMsgCount() {
		return sessionInfo.getState().getOutboundMsgCount();
	}

	public long getAverageOutboundMessageSize() {
		return getOutboundMsgCount() == 0 ? 0 : getOutboundDataSizeSum() / getOutboundMsgCount();
	}

	public long getAverageInboundMessageSize() {
		return getInboundMsgCount() == 0 ? 0 : getInboundDataSizeSum() / getInboundMsgCount();
	}
}
