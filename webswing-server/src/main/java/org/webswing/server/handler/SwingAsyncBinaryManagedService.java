package org.webswing.server.handler;

import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.DefaultBroadcaster;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.ShiroInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;

@AtmosphereHandlerService(path = "/async/swing-bin", broadcasterCache = UUIDBroadcasterCache.class, broadcaster = DefaultBroadcaster.class, interceptors = { AtmosphereResourceLifecycleInterceptor.class, SuspendTrackerInterceptor.class, ShiroInterceptor.class })
public class SwingAsyncBinaryManagedService extends AbstractAsyncManagedService {

}
