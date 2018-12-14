package org.webswing.server.services.recorder;

import org.webswing.server.model.exception.WsException;

public interface SessionRecorder {

	void saveFrame(byte[] serialized);

	void startRecording() throws WsException;

	void stopRecording() throws WsException;

	boolean isRecording();

	String getFileName();
}
