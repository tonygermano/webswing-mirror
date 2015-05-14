package org.webswing.server.handler;

import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.DefaultBroadcaster;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.ShiroInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;

@AtmosphereHandlerService(path = "/async/swing", broadcasterCache = UUIDBroadcasterCache.class, broadcaster = DefaultBroadcaster.class, interceptors = { AtmosphereResourceLifecycleInterceptor.class, TrackMessageSizeInterceptor.class, SuspendTrackerInterceptor.class, ShiroInterceptor.class })
public class SwingAsyncJsonManagedService extends AbstractAsyncManagedService {

}
