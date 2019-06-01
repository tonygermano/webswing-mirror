package org.webswing.server.services.swingprocess;

import com.google.inject.Singleton;
import org.webswing.server.model.exception.WsInitException;
import org.webswing.toolkit.util.DeamonThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Singleton
public class SwingProcessServiceImpl implements SwingProcessService {
	private ScheduledExecutorService processHandlerThread ;

	@Override
	public SwingProcess create(SwingProcessConfig config) {
		return new SwingProcessImpl(config,processHandlerThread);
	}

	@Override
	public void start() throws WsInitException {
		processHandlerThread=Executors.newSingleThreadScheduledExecutor(DeamonThreadFactory.getInstance("Webswing Process Handler"));
	}

	@Override
	public void stop() {
		processHandlerThread.shutdown();
	}
}
