package org.webswing.server.services.swingprocess;

public class SwingProcessServiceImpl implements SwingProcessService {

	@Override
	public SwingProcess create(SwingProcessConfig config) {
		return new SwingProcessImpl(config);
	}

}
