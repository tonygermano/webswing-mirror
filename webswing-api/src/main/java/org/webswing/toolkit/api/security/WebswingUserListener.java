package org.webswing.toolkit.api.security;

public interface WebswingUserListener {

	void onUserConnected(UserEvent evt);

	void onUserDisconnected(UserEvent evt);

	void onMirrorViewConnected(UserEvent evt);

	void onMirrorViewDisconnected(UserEvent evt);
}
