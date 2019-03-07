package org.webswing.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.server.common.model.rest.LogRequest;
import org.webswing.server.common.model.rest.LogResponse;
import org.webswing.server.common.util.VariableSubstitutor;
import org.webswing.server.model.exception.WsException;

public class LogReaderUtil {
	private static final Logger log = LoggerFactory.getLogger(LogReaderUtil.class);

	private static final String WEBSWING_LOG_FILE_TYPE_PREFIX = "webswing.log.file.";
	private static final String WEBSWING_LOG_FILE_TYPE_SESSION = "session";

	public static LogResponse readLog(String type, LogRequest request) throws WsException {
		File f = findLogFile(type);
		return readLogInternal(f, request);
	}
	
	public static LogResponse readSessionLog(String logDir, LogRequest request) throws WsException {
		File f = findSessionLogFile(logDir, request.getLogName());
		return readLogInternal(f, request);
	}
	
	public static List<String> readSessionLogNames(String logDir, String appPath) throws WsException {
		List<String> logNames = new ArrayList<String>();
		
		String appUrl = normalizeApplicationUrlForFileName(appPath);
		
		try {
			Path dirPath = Paths.get(logDir);
			if (!dirPath.toFile().exists()) {
				return logNames;
			}
			
			Files.newDirectoryStream(dirPath, path -> path.toString().matches(".*" + appUrl + "\\." + WEBSWING_LOG_FILE_TYPE_SESSION + "\\.log\\.?[0-9]*$")) // TODO put the session log file type prefix into properties?
			        .forEach(path -> logNames.add(path.getFileName().toString()));
		} catch (IOException e) {
			log.error("Error while browsing session log folder for app [" + appPath + "]", e);
		}
		
		return logNames;
	}

	private static int getReadSize(long start, long fileSize, LogRequest r) {
		return (int) Math.min(r.getMax(), fileSize - start);
	}

	private static long getStartIndex(long fileSize, LogRequest r) {
		long offset = r.getOffset() == -1 ? (fileSize - 1) : r.getOffset();
		if (r.isBackwards()) {
			return offset - Math.min(offset, r.getMax());
		} else {
			return offset;
		}
	}
	
	private static LogResponse readLogInternal(File f, LogRequest request) throws WsException {
		RandomAccessFile fileHandler = null;
		try {
			fileHandler = new RandomAccessFile(f, "r");
			long startIndex = getStartIndex(fileHandler.length(), request);

			if (startIndex > 0) {//find index of start of next new line
				for (long filePointer = startIndex - 1; filePointer < fileHandler.length(); filePointer++) {
					fileHandler.seek(filePointer);
					int readByte = fileHandler.readByte();
					if (readByte == 0xA) {
						break;
					}
				}
			}

			startIndex = fileHandler.getFilePointer();
			byte[] b = new byte[getReadSize(startIndex, fileHandler.length(), request)];
			fileHandler.readFully(b);
			LogResponse result = new LogResponse();
			result.setStartOffset(startIndex);
			result.setEndOffset(fileHandler.getFilePointer());
			result.setLog(new String(b));
			return result;
		} catch (IOException e) {
			throw new WsException("Failed to read log file. " + e.getMessage());
		} finally {
			if (fileHandler != null) {
				try {
					fileHandler.close();
				} catch (IOException e) {
					/* ignore */
				}
			}
		}
	}

	private static File findLogFile(String type) {
		String filename = System.getProperty(WEBSWING_LOG_FILE_TYPE_PREFIX + type);
		if (filename != null) {
			filename = VariableSubstitutor.basic().replace(filename);
			File file = Paths.get(System.getProperty(Constants.LOGS_DIR_PATH, "") + filename).toAbsolutePath().normalize().toFile();
			return file;
		}
		return null;
	}
	
	private static File findSessionLogFile(String logDir, String logName) {
		return Paths.get(logDir + logName).toAbsolutePath().normalize().toFile();
	}

	public static String normalizeApplicationUrlForFileName(String url) {
		return url.replaceAll("\\W+", "_");
	}
	
	public static InputStream getZippedLog(String type) throws WsException {
		File f = findLogFile(type);
		String tempDir = System.getProperty(Constants.TEMP_DIR_PATH);
		File zip = new File(URI.create(tempDir + type + ".zip"));
		if (f != null) {
			FileInputStream in = null;
			FileOutputStream fos = null;
			ZipOutputStream out = null;
			try {
				in = new FileInputStream(f);
				fos = new FileOutputStream(zip);
				out = new ZipOutputStream(fos);
				if (zip.canWrite()) {
					out.putNextEntry(new ZipEntry(f.getName()));
					byte[] b = new byte[1024];
					int count;

					while ((count = in.read(b)) > 0) {
						out.write(b, 0, count);
					}
				} else {
					throw new IOException("Can not write to file " + zip.getAbsolutePath());
				}
				return new FileInputStream(zip);
			} catch (IOException e) {
				log.error("Failed to zip the log file.", e);
			} finally {
				try {
					out.close();
				} catch (Exception e) {
				}
				try {
					fos.close();
				} catch (Exception e) {
				}
				try {
					in.close();
				} catch (Exception e) {
				}
			}
			throw new WsException("Failed to download the zipped " + type + " log.");
		}
		return null;
	}
}
