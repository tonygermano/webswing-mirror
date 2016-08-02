package org.webswing.model.s2c;

import org.webswing.model.Msg;

public enum SimpleEventMsgOut implements Msg {
	applicationAlreadyRunning,
	shutDownNotification,
	tooManyClientsNotification,
	continueOldSession,
	continueOldSessionAutomatic,
	configurationError,
	sessionStolenNotification;

	public AppFrameMsgOut buildMsgOut() {
		AppFrameMsgOut result = new AppFrameMsgOut();
		result.setEvent(this);
		return result;
	}
}
