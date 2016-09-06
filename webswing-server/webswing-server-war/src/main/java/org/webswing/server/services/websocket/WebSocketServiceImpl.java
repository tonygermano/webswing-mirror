package org.webswing.server.services.websocket;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.atmosphere.util.VoidServletConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.base.WebswingService;
import org.webswing.server.base.WsInitException;
import org.webswing.server.services.playback.RecordingPlaybackUrlHandlerImpl;
import org.webswing.server.services.security.SecurityManagerService;
import org.webswing.server.services.swingmanager.SwingInstanceHolder;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class WebSocketServiceImpl implements WebswingService, WebSocketService {
	private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);
	private static final String WEBSOCKET_MESSAGE_SIZE = System.getProperty(Constants.WEBSOCKET_MESSAGE_SIZE, "1048576");
	private static final String WEBSOCKET_THREADPOOL_SIZE = System.getProperty(Constants.WEBSOCKET_THREAD_POOL, "10");
	private static final Class<?>[] jsonInterceptors = new Class<?>[] { AtmosphereResourceLifecycleInterceptor.class, TrackMessageSizeInterceptor.class, SuspendTrackerInterceptor.class };
	private static final Class<?>[] binaryInterceptors = new Class<?>[] { AtmosphereResourceLifecycleInterceptor.class, SuspendTrackerInterceptor.class };

	private final SecuredAtmosphereFramework framework;
	private final ServletContext context;

	@Inject
	public WebSocketServiceImpl(ServletContext context, SecurityManagerService securityManager) {
		this.context = context;
		this.framework = new SecuredAtmosphereFramework(securityManager);

	}

	public void start() throws WsInitException {

		Map<String, String> initParams = new HashMap<String, String>();
		initParams.put("org.atmosphere.websocket.bufferSize", WEBSOCKET_MESSAGE_SIZE);
		initParams.put("org.atmosphere.websocket.maxTextMessageSize", WEBSOCKET_MESSAGE_SIZE);
		initParams.put("org.atmosphere.websocket.maxBinaryMessageSize", WEBSOCKET_MESSAGE_SIZE);
		initParams.put("org.atmosphere.cpr.broadcaster.maxProcessingThreads", WEBSOCKET_THREADPOOL_SIZE);
		initParams.put("org.atmosphere.cpr.broadcaster.maxAsyncWriteThreads", WEBSOCKET_THREADPOOL_SIZE);
		initParams.put("org.atmosphere.cpr.broadcaster.shareableThreadPool", "true");
		initParams.put("org.atmosphere.cpr.scanClassPath", "false");
		initParams.put("org.atmosphere.cpr.AtmosphereFramework.analytics", "false");
		initParams.put("org.atmosphere.cpr.broadcasterCacheClass", "org.atmosphere.cache.UUIDBroadcasterCache");
		initParams.put("org.atmosphere.container.JSR356AsyncSupport.mappingPath", "/{PATH}");

		try {

			framework.init(new VoidServletConfig(initParams) {
				@Override
				public ServletContext getServletContext() {
					return context;
				}
			}, false);

			initParams.put("org.atmosphere.container.JSR356AsyncSupport.mappingPath", context.getContextPath());

		} catch (ServletException e) {
			throw new WsInitException("Failed to initialize Websocket framework", e);
		}
	}

	public void stop() {
		framework.destroy();
	}

	private List<AtmosphereInterceptor> instantiate(Class<?>[] interceptors) {
		List<AtmosphereInterceptor> result = new ArrayList<AtmosphereInterceptor>();
		for (Class<?> c : interceptors) {
			if (AtmosphereInterceptor.class.isAssignableFrom(c)) {
				try {
					Constructor<?> constructor = c.getConstructor();
					result.add((AtmosphereInterceptor) constructor.newInstance());
				} catch (Exception e) {
					log.error("Failed to initialize Atmosphere Interceptors.", e);
				}
			}
		}
		return result;
	}

	@Override
	public WebSocketUrlHandler createBinaryWebSocketHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		return createWebSocketListener(parent, "/async/swing-bin", instanceHolder, binaryInterceptors);
	}

	@Override
	public WebSocketUrlHandler createJsonWebSocketHandler(UrlHandler parent, SwingInstanceHolder instanceHolder) {
		return createWebSocketListener(parent, "/async/swing", instanceHolder, jsonInterceptors);
	}

	@Override
	public WebSocketUrlHandler createPlaybackWebSocketHandler(UrlHandler parent) {
		RecordingPlaybackUrlHandlerImpl result = new RecordingPlaybackUrlHandlerImpl(parent, "/async/swing-play", this);
		AtmosphereHandler h = new WebSocketAtmosphereHandler(result);
		framework.addAtmosphereHandler(result.getFullPathMapping(), h, instantiate(binaryInterceptors));
		return result;
	}

	private WebSocketUrlHandler createWebSocketListener(UrlHandler parent, String url, SwingInstanceHolder instanceHolder, Class<?>[] interceptors) {
		WebSocketUrlHandlerImpl result = new WebSocketUrlHandlerImpl(parent, url, this, instanceHolder);
		AtmosphereHandler h = new WebSocketAtmosphereHandler(result);
		framework.addAtmosphereHandler(result.getFullPathMapping(), h, instantiate(interceptors));
		return result;
	}

	@Override
	public void removeListener(String mapping) {
		framework.removeAtmosphereHandler(mapping);
	}

	public void serve(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		framework.doCometSupport(AtmosphereRequest.wrap(req), AtmosphereResponse.wrap(res));
	}

}
