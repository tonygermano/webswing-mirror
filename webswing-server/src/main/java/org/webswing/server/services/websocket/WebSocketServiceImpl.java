package org.webswing.server.services.websocket;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.ShiroInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.base.WebswingService;

import com.google.inject.Singleton;

@Singleton
public class WebSocketServiceImpl implements WebswingService, WebSocketService {
	private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);

	AtmosphereFramework framework = new AtmosphereFramework(false, false);

	public void start() {
		/*
			<init-param>
				<param-name>org.atmosphere.cpr.packages</param-name>
				<param-value>org.webswing.server.handler</param-value>
			</init-param>
			<init-param>
				<param-name>org.atmosphere.websocket.bufferSize</param-name>
				<param-value>1048576</param-value>
			</init-param>
			<init-param>
				<param-name>org.atmosphere.websocket.maxTextMessageSize</param-name>
				<param-value>1048576</param-value>
			</init-param>
			<init-param>
				<param-name>org.atmosphere.websocket.maxBinaryMessageSize</param-name>
				<param-value>1048576</param-value>
			</init-param>
			<init-param>
		<param-name>org.atmosphere.cpr.broadcaster.maxProcessingThreads</param-name>
		<param-value>10</param-value>
		    </init-param>
			<init-param>
		<param-name>org.atmosphere.cpr.broadcaster.maxAsyncWriteThreads</param-name>
		<param-value>10</param-value>
		    </init-param>
			<init-param>
		<param-name>org.atmosphere.cpr.broadcaster.shareableThreadPool</param-name>
		<param-value>true</param-value>
		    </init-param>*/

		framework.init();
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
	public void registerBinaryWebSocketListener(String mapping, WebSocketMessageListener listener) {
		AtmosphereHandler h = new WebSocketAtmosphereHandler(listener);
		Class<?>[] interceptors = new Class<?>[] { AtmosphereResourceLifecycleInterceptor.class, SuspendTrackerInterceptor.class, ShiroInterceptor.class };
		framework.addAtmosphereHandler(mapping, h, instantiate(interceptors));
	}

	@Override
	public void registerJsonWebSocketListener(String mapping, WebSocketMessageListener listener) {
		AtmosphereHandler h = new WebSocketAtmosphereHandler(listener);
		Class<?>[] interceptors = new Class<?>[] { AtmosphereResourceLifecycleInterceptor.class, TrackMessageSizeInterceptor.class, SuspendTrackerInterceptor.class, ShiroInterceptor.class };
		framework.addAtmosphereHandler(mapping, h, instantiate(interceptors));
	}

	@Override
	public void removeListener(String mapping) {
		framework.removeAtmosphereHandler(mapping);
	}

	public boolean canServe(String path) {
		return framework.getAtmosphereHandlers().containsKey(path);
	}

	public void serve(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		framework.doCometSupport(AtmosphereRequest.wrap(req), AtmosphereResponse.wrap(res));
	}

}
