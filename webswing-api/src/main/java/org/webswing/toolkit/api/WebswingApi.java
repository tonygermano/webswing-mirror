package org.webswing.toolkit.api;

import org.webswing.toolkit.api.lifecycle.WebswingShutdownListener;
import org.webswing.toolkit.api.security.WebswingUser;
import org.webswing.toolkit.api.security.WebswingUserListener;

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

}
