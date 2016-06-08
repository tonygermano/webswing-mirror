package org.webswing.server.handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.webswing.Constants;
import org.webswing.server.util.ServerUtil;

public class FileServlet extends HttpServlet {

	private static final long serialVersionUID = 7829511263519944733L;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static FileServlet currentServlet = null;
	private Map<String, FileDescriptor> fileMap = new HashMap<String, FileDescriptor>();
	private ScheduledExecutorService validatorService = Executors.newSingleThreadScheduledExecutor();

	public void init() throws ServletException {
		currentServlet = this;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileId = request.getParameter("id");
		String userId = (String) SecurityUtils.getSubject().getPrincipal();

		if (!fileMap.containsKey(fileId) || fileMap.get(fileId).file == null ) {
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

	@Override
	public void destroy() {
		super.destroy();
		//TODO: delete temp files
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
