package org.webswing.server;

import java.util.Date;

import org.atmosphere.cpr.AtmosphereResource;
import org.webswing.model.MsgIn;
import org.webswing.model.MsgOut;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.c2s.SimpleEventMsgIn;
import org.webswing.model.c2s.SimpleEventMsgIn.SimpleEventType;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.model.server.admin.SwingJvmStats;
import org.webswing.server.SwingJvmConnection.WebSessionListener;
import org.webswing.server.model.EncodedMessage;
import org.webswing.server.recording.SessionRecorder;
import org.webswing.server.util.ServerUtil;
import org.webswing.server.util.StatUtils;

public class SwingInstance implements WebSessionListener {

	private String user;
	private AtmosphereResource resource;
	private AtmosphereResource mirroredResource;
	private SwingDescriptor application;
	private SwingJvmConnection connection;
	private Date disconnectedSince;
	private SessionRecorder sessionRecorder;
	private final Date startedAt = new Date();
	private Date endedAt = null;

	public SwingInstance(ConnectionHandshakeMsgIn h, SwingDescriptor app, AtmosphereResource resource) {
		this.application = app;
		this.user = ServerUtil.getUserName(resource);
		registerPrimaryWebSession(resource);
		this.connection = new SwingJvmConnection(h, app, this, ServerUtil.getCustomArgs(resource.getRequest()), ServerUtil.getDebugPort(resource.getRequest()));
		this.sessionRecorder = ServerUtil.isRecording(resource.getRequest()) ? new SessionRecorder(this) : null;
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

	public void kill() {
		SimpleEventMsgIn simpleEventMsgIn = new SimpleEventMsgIn();
		simpleEventMsgIn.setType(SimpleEventType.killSwing);
		sendToSwing(null, simpleEventMsgIn);
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
			synchronized (mirroredResource) {
				ServerUtil.broadcastMessage(mirroredResource, serialized);
			}
		}
	}

	public boolean sendToSwing(AtmosphereResource r, MsgIn h) {
		if (connection.isRunning()) {
			if (h instanceof SimpleEventMsgIn) {
				SimpleEventMsgIn m = (SimpleEventMsgIn) h;
				if (m.getType().equals(SimpleEventMsgIn.SimpleEventType.paintAck)) {
					if (((resource != null && r.uuid().equals(resource.uuid())) || (resource == null && mirroredResource != null && r.uuid().equals(mirroredResource.uuid())))) {
						connection.send(h);
					}
				} else if (m.getType().equals(SimpleEventMsgIn.SimpleEventType.unload)) {
					if (resource != null && r.uuid().equals(resource.uuid())) {
						connection.send(h);
					}
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

	public SwingDescriptor getApplication() {
		return application;
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

	public SwingJvmStats getStats() {
		return connection.getLatest();
	}

	public Boolean isRecording() {
		return sessionRecorder != null && !sessionRecorder.isFailed();
	}

	public String getRecordingFile() {
		return sessionRecorder != null ? sessionRecorder.getFileName() : null;
	}
}
