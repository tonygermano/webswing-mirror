package org.webswing.server;

import java.util.Date;

import org.atmosphere.cpr.AtmosphereResource;
import org.webswing.model.MsgIn;
import org.webswing.model.MsgOut;
import org.webswing.model.admin.s2c.SwingJvmStatsMsg;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.c2s.SimpleEventMsgIn;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.server.SwingJvmConnection.WebSessionListener;
import org.webswing.server.handler.AdminAsyncManagedService;
import org.webswing.server.model.EncodedMessage;
import org.webswing.server.stats.SessionRecorder;
import org.webswing.server.util.ServerUtil;
import org.webswing.server.util.StatUtils;

public class SwingInstance implements WebSessionListener {

	private String user;
	private AtmosphereResource resource;
	private AtmosphereResource mirroredResource;
	private SwingApplicationDescriptor application;
	private SwingJvmConnection connection;
	private SwingJvmStatsMsg latest = new SwingJvmStatsMsg();
	private Date disconnectedSince;
	private SessionRecorder sessionRecorder;
	private final Date startedAt = new Date();
	private Date endedAt = null;

	public SwingInstance(ConnectionHandshakeMsgIn h, SwingApplicationDescriptor app, AtmosphereResource resource) {
		this.application = app;
		this.sessionRecorder = ServerUtil.isRecording(resource.getRequest()) ? new SessionRecorder(this) : null;
		this.user = ServerUtil.getUserName(resource);
		registerPrimaryWebSession(resource);
		this.connection = new SwingJvmConnection(h, app, this, ServerUtil.getCustomArgs(resource.getRequest()), ServerUtil.getDebugPort(resource.getRequest()));
	}

	public boolean registerPrimaryWebSession(AtmosphereResource resource) {
		if (this.resource == null) {
			this.resource = resource;
			this.disconnectedSince = null;
			return true;
		} else {
			if (resource == null) {
				SwingInstance.this.resource = null;
				SwingInstance.this.disconnectedSince = new Date();
				SwingInstanceManager.getInstance().notifySwingChangeChange();
			}
			return false;
		}
	}

	public boolean registerMirroredWebSession(AtmosphereResource resource) {
		if (this.mirroredResource == null) {
			this.mirroredResource = resource;
			return true;
		} else {
			if (resource == null) {
				SwingInstance.this.mirroredResource = null;
			}
			return false;
		}
	}

	public void sendToWeb(MsgOut o) {
		EncodedMessage serialized = new EncodedMessage(o);
		if (sessionRecorder != null) {
			sessionRecorder.saveFrame(serialized.getProtoMessage());
		}
		if (resource != null) {
			synchronized (resource) {
				ServerUtil.broadcastMessage(resource, serialized);
				int length = resource.forceBinaryWrite() ? serialized.getProtoMessage().length : serialized.getJsonMessage().getBytes().length;
				StatUtils.logOutboundData(this, length);
			}
		}
		if (mirroredResource != null) {
			synchronized (AdminAsyncManagedService.BROADCAST_LOCK) {
				synchronized (mirroredResource) {
					ServerUtil.broadcastMessage(mirroredResource, serialized);
				}
			}
		}
	}

	public boolean sendToSwing(AtmosphereResource r, MsgIn h) {
		if (connection.isRunning()) {
			if (h instanceof SimpleEventMsgIn) {
				SimpleEventMsgIn m = (SimpleEventMsgIn) h;
				if (m.getType().equals(SimpleEventMsgIn.SimpleEventType.paintAck) && ((resource != null && r.uuid().equals(resource.uuid())) || (resource == null && mirroredResource != null && r.uuid().equals(mirroredResource.uuid())))) {
					connection.send(h);
				} else if (m.getType().equals(SimpleEventMsgIn.SimpleEventType.unload)) {
					SwingInstanceManager.getInstance().notifySessionDisconnected(r.uuid());
				} else {
					connection.send(h);
				}
			} else {
				connection.send(h);
			}
			return true;
		} else {
			return false;
		}
	}

	public void notifyClose() {
		endedAt = new Date();
		SwingInstanceManager.getInstance().notifySwingClose(this);
		if (sessionRecorder != null) {
			sessionRecorder.close();
		}
	}

	public String getClientId() {
		return connection.getClientId();
	}

	public String getApplicationName() {
		return application.getName();
	}

	public String getSessionId() {
		if (resource != null) {
			return resource.uuid();
		}
		return null;
	}

	public String getMirroredSessionId() {
		if (mirroredResource != null) {
			return mirroredResource.uuid();
		}
		return null;
	}

	public boolean isRunning() {
		return connection.isRunning();
	}

	public String getUser() {
		return user;
	}

	public Date getDisconnectedSince() {
		return disconnectedSince;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public Date getEndedAt() {
		return endedAt;
	}

	public SwingJvmStatsMsg collectStats() {
		return latest = StatUtils.getSwingInstanceStats(this, connection != null ? connection.getJmxConnection() : null);
	}

	public SwingJvmStatsMsg getStats() {
		return latest;
	}

}
