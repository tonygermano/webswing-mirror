package org.webswing.server.services.files;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.webswing.server.base.UrlHandler;

public interface FileTransferHandler extends UrlHandler {

	boolean registerFile(File file, final String id, long validForTime, TimeUnit timeUnit, String validForUser, String instanceId, boolean temp, boolean waitForFile, String overwriteDetails) ;

}
