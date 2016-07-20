package org.webswing.toolkit.api;

import org.webswing.toolkit.api.lifecycle.WebswingShutdownListener;
import org.webswing.toolkit.api.security.WebswingUser;
import org.webswing.toolkit.api.security.WebswingUserListener;

public interface WebswingApi {

	public WebswingUser getPrimaryUser();

	public WebswingUser getMirrorViewUser();

	public Boolean primaryUserHasRole(String role) throws WebswingApiException;

	public Boolean primaryUserIsPermitted(String permission) throws WebswingApiException;

	public void addUserConnectionListener(WebswingUserListener listener);

	public void removeUserConnectionListener(WebswingUserListener listener);

	public void notifyShutdown(int forceKillTimeout);

	public void addShutdownListener(WebswingShutdownListener listener);

	public void removeShutdownListener(WebswingShutdownListener listener);

}
