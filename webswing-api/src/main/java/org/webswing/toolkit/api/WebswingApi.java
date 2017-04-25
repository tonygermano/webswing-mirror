package org.webswing.toolkit.api;

import org.webswing.toolkit.api.lifecycle.WebswingShutdownListener;
import org.webswing.toolkit.api.security.WebswingUser;
import org.webswing.toolkit.api.security.WebswingUserListener;
import org.webswing.toolkit.api.url.WebswingUrlState;
import org.webswing.toolkit.api.url.WebswingUrlStateChangeListener;

/**
 * Webswing API used by Swing application for easy integration.  
 */
public interface WebswingApi {

	/**
	 * Return the user of connected web session. 
	 * <b>Note:</b>if user disconnects/closes browser, this method will return null.  
	 *
	 * @return user or null if session is disconnected.
	 */
	public WebswingUser getPrimaryUser();

	/**
	 * Return the user of connected mirror view web session. (Admin console -> view session)
	 * <b>Note:</b>if admin disconnects/closes browser, this method will return null.  
	 *
	 * @return user or null if mirror view session is disconnected.
	 */
	public WebswingUser getMirrorViewUser();

	/**
	 * Check if connected web session user has role defined. If no user is connected null is returned.
	 * @param role name of role
	 * @return True if user has the role, false if not. Null if no user is connected
	 * @throws WebswingApiException if communication with server fails. 
	 */
	public Boolean primaryUserHasRole(String role) throws WebswingApiException;

	/**
	 * Check if connected web session user has permission defined. If no user is connected null is returned.
	 * @param permission name of permission
	 * @return True if user has the permission, false if not. Null if no user is connected
	 * @throws WebswingApiException if communication with server fails. 
	 */
	public Boolean primaryUserIsPermitted(String permission) throws WebswingApiException;

	/**
	 * Add listener to receive notifications when user (primary/mirror view) connects or disconnects from session
	 * @param listener listener to add
	 */
	public void addUserConnectionListener(WebswingUserListener listener);

	/**
	 * Remove user listener 
	 * @param listener listener to remove
	 */
	public void removeUserConnectionListener(WebswingUserListener listener);

	/**
	 * When swing application exit and shutdown process takes longer time to process, invoking this method
	 * will notify user (web session) the application has finished, and disconnect the user from the session.  
	 * Swing Application process is removed from running sessions list even though the process might still be running.
	 *
	 * @param forceKillTimeout how long (in Ms) to wait for process to finish. After this time the process is forcefully terminated.
	 */
	public void notifyShutdown(int forceKillTimeout);

	/**
	 * Add listener that is triggered when Webswing requests swing application to exit. If there is no explicit shutdown listener
	 * Webswing use default shutdown procedure (send window closing event to all windows). Otherwise listeners are fired.
	 * It is expected the result of listener execution will exit the swing process. Otherwise the swing process will be forcefully terminated
	 * after defined timeout (system property "webswing.waitForExit", default: 30000) 
	 * @param listener listener to add
	 */
	public void addShutdownListener(WebswingShutdownListener listener);

	/**
	 * Remove shutdown listener
	 * @param listener listener to remove
	 */
	public void removeShutdownListener(WebswingShutdownListener listener);

	/**
	 * @return the Webswing version in 'git describe' format
	 */
	public String getWebswingVersion();

	/**
	 * See {@link WebswingApi#setUrlState(WebswingUrlState, boolean)}.
	 * This method will not trigger url change event in {@link WebswingUrlStateChangeListener}
	 *
	 * @param state state object url is generated from
	 */
	public void setUrlState(WebswingUrlState state);

	/**
	 * Sets the hash Fragment of users browser URL to represent the current state of the swing application.
	 * Intended for use in combination with {@link WebswingUrlStateChangeListener} and/or {@link WebswingApi#getUrlState()}
	 *
	 * @param state state object url is generated from
	 * @param fireChangeEvent if true, invoking this method will trigger url change event
	 */
	public void setUrlState(WebswingUrlState state, boolean fireChangeEvent);

	/**
	 * @return current user's URL state (parsed hash fragment of URL) or null if no user is connected.
	 */
	public WebswingUrlState getUrlState();

	/**
	 * Register a URL state change listener
	 * @param listener the listener
	 */

	public void removeUrlStateChangeListener(WebswingUrlStateChangeListener listener);

	/**
	 * Remove URL state change listener
	 * @param listener the listener
	 */
	public void addUrlStateChangeListener(WebswingUrlStateChangeListener listener);

	/**
	 * Reset session timeout to prevent automatic termination.
	 * Useful if a long running operation has to finish even if user disconnects or is inactive for longer timeframe.
	 * Note: Reset needs to be called in periods shorter than configured session timeout. ("webswing.sessionTimeoutSec" system property)
	 * Note2: This method has no effect if session timeout is set to 0
	 */
	public void resetInactivityTimeout();

}
