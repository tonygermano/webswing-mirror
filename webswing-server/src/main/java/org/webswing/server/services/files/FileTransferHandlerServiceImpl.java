package org.webswing.server.services.files;

import org.webswing.server.services.swingmanager.SwingInstanceManager;

public class FileTransferHandlerServiceImpl implements FileTransferHandlerService {

	public FileTransferHandler create(SwingInstanceManager manager) {
		return new FileServlet(manager);
	}
}
