package org.webswing.server.handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.c2s.UploadEventMsgIn;
import org.webswing.server.SwingInstance;
import org.webswing.server.SwingInstanceManager;
import org.webswing.server.util.ServerUtil;

public class FileServlet extends HttpServlet {

	private static final long serialVersionUID = 7829511263519944733L;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final Logger log = LoggerFactory.getLogger(AbstractAsyncManagedService.class);

	private static FileServlet currentServlet = null;
	private Map<String, FileDescriptor> fileMap = new HashMap<String, FileDescriptor>();
	private ScheduledExecutorService validatorService = Executors.newSingleThreadScheduledExecutor();

	public void init() throws ServletException {
		currentServlet = this;
	}

	//Handle file download
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileId = request.getParameter("id");
		String userId = (String) SecurityUtils.getSubject().getPrincipal();

		if (!fileMap.containsKey(fileId) || fileMap.get(fileId).file == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
			return;
		}

		if (userId == null || !userId.equals(fileMap.get(fileId).userId)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403.
			return;
		}

		FileDescriptor fd = fileMap.get(fileId);
		synchronized (fd) {
			if (fd.waitForFile) {
				try {
					fd.wait(Long.getLong(Constants.FILE_SERVLET_WAIT_TIMEOUT, 300000));
				} catch (InterruptedException e) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
					fd.waitForFileTask.cancel(false);
					return;
				}
			}
		}

		if (!fileMap.get(fileId).file.exists()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
			return;
		}

		File file = fd.file;
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
		BufferedInputStream input = null;
		BufferedOutputStream output = null;

		try {
			input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		} finally {
			close(output);
			close(input);
		}
	}

	//handle file upload
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String clientId = request.getParameter("clientId");
			if (clientId != null) {
				SwingInstance instance = SwingInstanceManager.getInstance().findInstance(clientId);
				if (instance != null) {
					float maxMB = instance.getApplication().getUploadMaxSize();
					long maxsize = (long) (maxMB * 1024 * 1024);
					Part filePart = request.getPart("files[]"); // Retrieves <input type="file" name="file">
					String filename = getFilename(filePart);
					if (maxsize>0 && filePart.getSize() > maxsize) {
						resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
						resp.getWriter().write(String.format("File '%s' is too large. (Max. file size is %.1fMB)", filename, maxMB));
					} else {
						String tempDir = System.getProperty(Constants.TEMP_DIR_PATH);
						String tempName = UUID.randomUUID().toString();
						InputStream filecontent = filePart.getInputStream();
						File f = new File(URI.create(tempDir + "/" + tempName));
						FileOutputStream output = new FileOutputStream(f);
						IOUtils.copy(filecontent, output);
						output.close();
						filecontent.close();
						UploadEventMsgIn msg = new UploadEventMsgIn();
						msg.setFileName(filename);
						msg.setTempFileLocation(f.getAbsolutePath());
						boolean sent = SwingInstanceManager.getInstance().sendMessageToSwing(null, clientId, msg);
						if (!sent) {
							f.delete();
						} else {
							resp.getWriter().write("{\"files\":[{\"name\":\"" + filename + "\"}]}"); // TODO size
						}
					}
				}else{
					throw new Exception("Related Swing instance not found.("+clientId+")");
				}
			}else{
				throw new Exception("clientId not specified in request");
			}
		} catch (Exception e) {
			resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
			resp.getWriter().write("Upload finished with error...");
			log.error("Error while uploading file: "+e.getMessage(),e);
		}
	}
	
	@Override
	public void destroy() {
		validatorService.shutdownNow();
		super.destroy();
	}

	private static void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getFilename(Part part) {
		for (String cd : part.getHeader("Content-Disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
			}
		}
		return null;
	}

	private static class FileDescriptor {

		protected File file;
		@SuppressWarnings("unused")
		protected Future<?> invalidateScheduleTask;
		protected String userId;
		protected boolean temporary;
		protected boolean waitForFile;
		protected String lastFileAttributes;
		protected Future<?> waitForFileTask;
		protected String overwriteDetails;

		public FileDescriptor(File file, String userId) {
			super();
			this.file = file;
			this.userId = userId;
		}
	}

	public static String registerFile(File file, final String id, long validForTime, TimeUnit timeUnit, String validForUser, boolean waitForFile, String overwriteDetails) throws IOException {
		return registerFileInternal(file, id, validForTime, timeUnit, validForUser, false, waitForFile, overwriteDetails);
	}

	private static String registerFileInternal(File file, final String id, long validForTime, TimeUnit timeUnit, String validForUser, boolean temp, boolean waitForFile, String overwriteDetails) throws IOException {
		if (currentServlet != null) {
			final FileDescriptor fd = new FileDescriptor(file, validForUser);
			fd.temporary = temp;
			fd.waitForFile = waitForFile;
			fd.overwriteDetails = overwriteDetails;
			synchronized (currentServlet.fileMap) {
				currentServlet.fileMap.put(id, fd);
				if (validForTime > 0) {
					Future<?> invalidateTask = currentServlet.validatorService.schedule(new Runnable() {

						@Override
						public void run() {
							currentServlet.fileMap.remove(id);
							if (fd.temporary) {
								fd.file.delete();
							}
						}
					}, validForTime, timeUnit);
					fd.invalidateScheduleTask = invalidateTask;
				}
				if (waitForFile) {
					Future<?> waitForFileTask = currentServlet.validatorService.scheduleAtFixedRate(new Runnable() {
						@Override
						public void run() {
							synchronized (fd) {
								if (fd.file.exists()) {
									String details = fd.file.length() + "|" + fd.file.lastModified();
									if (!details.equals(fd.overwriteDetails)) {
										if (details.equals(fd.lastFileAttributes)) {
											if (!ServerUtil.isFileLocked(fd.file)) {
												fd.waitForFile = false;
												fd.notifyAll();
												fd.waitForFileTask.cancel(false);
											}
										} else {
											fd.lastFileAttributes = details;
										}
									}
								}
							}
						}
					}, 1, 1, TimeUnit.SECONDS);
					fd.waitForFileTask = waitForFileTask;
				}
			}
			return file.getAbsolutePath();
		} else {
			throw new IOException("File servlet not yet initialized!");
		}
	}

}
