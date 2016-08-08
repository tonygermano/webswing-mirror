package org.webswing.toolkit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.webswing.model.internal.ApiCallMsgInternal;
import org.webswing.model.internal.ApiCallMsgInternal.ApiMethod;
import org.webswing.model.internal.ApiEventMsgInternal;
import org.webswing.model.internal.ApiEventMsgInternal.ApiEventType;
import org.webswing.toolkit.api.WebswingApi;
import org.webswing.toolkit.api.WebswingApiException;
import org.webswing.toolkit.api.lifecycle.WebswingShutdownListener;
import org.webswing.toolkit.api.security.UserEvent;
import org.webswing.toolkit.api.security.WebswingUser;
import org.webswing.toolkit.api.security.WebswingUserListener;
import org.webswing.toolkit.util.DeamonThreadFactory;
import org.webswing.toolkit.util.Logger;
import org.webswing.toolkit.util.Services;
import org.webswing.toolkit.util.Util;

public class WebswingApiImpl implements WebswingApi {
	private List<WebswingShutdownListener> shutdownListeners = Collections.synchronizedList(new ArrayList<WebswingShutdownListener>());
	private List<WebswingUserListener> userConnectionListeners = Collections.synchronizedList(new ArrayList<WebswingUserListener>());
	private ExecutorService apiProcessor = Executors.newSingleThreadExecutor(DeamonThreadFactory.getInstance());
	WebswingUser primaryUser;
	WebswingUser mirrorUser;

	@Override
	public WebswingUser getPrimaryUser() {
		return primaryUser;
	}

	@Override
	public WebswingUser getMirrorViewUser() {
		return mirrorUser;
	}

	@Override
	public Boolean primaryUserHasRole(String role) throws WebswingApiException {
		try {
			return apiCall(ApiMethod.HasRole, role);
		} catch (WebswingApiException e) {
			Logger.error("Failed to resolve role assignemnt.", e);
			throw e;
		}
	}

	@Override
	public Boolean primaryUserIsPermitted(String permission) throws WebswingApiException {
		try {
			return apiCall(ApiMethod.IsPermitted, permission);
		} catch (WebswingApiException e) {
			Logger.error("Failed to resolve role assignemnt.", e);
			throw e;
		}
	}

	public void addUserConnectionListener(WebswingUserListener listener) {
		userConnectionListeners.add(listener);
	}

	public void removeUserConnectionListener(WebswingUserListener listener) {
		userConnectionListeners.remove(listener);
	}

	@Override
	public void notifyShutdown(int forceKillTimeout) {
		Util.getWebToolkit().getPaintDispatcher().notifyApplicationExiting(forceKillTimeout);
	}

	@Override
	public void addShutdownListener(WebswingShutdownListener listener) {
		shutdownListeners.add(listener);
	}

	@Override
	public void removeShutdownListener(WebswingShutdownListener listener) {
		shutdownListeners.remove(listener);
	}

	@SuppressWarnings("unchecked")
	private <T> T apiCall(ApiMethod m, Serializable... args) throws WebswingApiException {
		ApiCallMsgInternal msg = new ApiCallMsgInternal();
		msg.setMethod(m);
		msg.setArgs(args);
		try {
			ApiCallMsgInternal result = (ApiCallMsgInternal) Services.getConnectionService().sendObjectSync(msg, msg.getCorrelationId());
			return ((T) result.getResult());
		} catch (Exception e) {
			throw new WebswingApiException("API call failed.", e);
		}
	}

	public void processEvent(final ApiEventMsgInternal event) {
		apiProcessor.submit(new Runnable() {
			@Override
			public void run() {
				synchronized (userConnectionListeners) {
					try {
						UserEvent e = new UserEvent(new WebswingUserInfo(event.getArgs()));
						switch (event.getEvent()) {
						case UserConnected:
							primaryUser = e.getUser();
							break;
						case UserDisconnected:
							primaryUser = null;
							break;
						case MirrorViewConnected:
							mirrorUser = e.getUser();
							break;
						case MirrorViewDisconnected:
							mirrorUser = null;
							break;
						default:
							break;
						}
						WebswingApiImpl.this.fireUserListener(event.getEvent(), e);
					} catch (Exception e) {
						Logger.error("Processing api event failed.", e);
					}
				}
			}
		});

	}

	protected void fireShutdownListeners() {
		apiProcessor.submit(new Runnable() {

			@Override
			public void run() {
				synchronized (shutdownListeners) {
					if (shutdownListeners.size() == 0) {
						Logger.info("No shutdown listener found. Using default shutdown procedure.");
						Util.getWebToolkit().defaultShutdownProcedure();
					} else {
						for (WebswingShutdownListener l : shutdownListeners) {
							try {
								l.onShutdown();
							} catch (Exception e) {
								Logger.error("Shutdown Listener failed.", e);
							}
						}
					}
				}
			}
		});
	}

	private void fireUserListener(ApiEventType type, UserEvent event) {
		synchronized (userConnectionListeners) {
			for (WebswingUserListener l : userConnectionListeners) {
				try {
					switch (type) {
					case UserConnected:
						l.onPrimaryUserConnected(event);
						break;
					case UserDisconnected:
						l.onPrimaryUserDisconnected(event);
						break;
					case MirrorViewConnected:
						l.onMirrorViewConnected(event);
						break;
					case MirrorViewDisconnected:
						l.onMirrorViewDisconnected(event);
						break;
					default:
						break;
					}
				} catch (Exception e) {
					Logger.error("User Listener failed.", e);
				}
			}
		}
	}

	public class WebswingUserInfo implements WebswingUser {
		private String userId;
		private HashMap<String, Serializable> userAttributes;

		@SuppressWarnings("unchecked")
		public WebswingUserInfo(Serializable... attribs) {
			super();
			this.userId = (String) attribs[0];
			this.userAttributes = (HashMap<String, Serializable>) attribs[1];
		}

		@Override
		public String getUserId() {
			return userId;
		}

		@Override
		public Map<String, Serializable> getUserAttributes() {
			return userAttributes;
		}

		@Override
		public String toString() {
			return getUserId();
		}
	}

}
