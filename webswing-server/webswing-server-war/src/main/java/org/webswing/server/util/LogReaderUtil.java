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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.server.common.model.SwingConfig;
import org.webswing.server.common.model.rest.LogRequest;
import org.webswing.server.common.model.rest.LogResponse;
import org.webswing.server.common.model.rest.SessionLogRequest;
import org.webswing.server.common.util.VariableSubstitutor;
import org.webswing.server.model.exception.WsException;

import com.google.common.collect.Lists;

public class LogReaderUtil {
	private static final Logger log = LoggerFactory.getLogger(LogReaderUtil.class);

	private static final String WEBSWING_LOG_FILE_TYPE_PREFIX = "webswing.log.file.";
	private static final String WEBSWING_LOG_FILE_TYPE_SESSION = "session";

	public static LogResponse readLog(String type, LogRequest request) throws WsException {
		File f = findLogFile(type);
		return readLogInternal(f, request);
	}
	
	public static LogResponse readSessionLog(String appUrl, String logDir, SessionLogRequest request) throws WsException {
		String appUrlNormalized = LogReaderUtil.normalizeForFileName(appUrl);
		String sessionIdNormalized = LogReaderUtil.normalizeForFileName(request.getInstanceId());
		String logName = "webswing-" + sessionIdNormalized + "-" + appUrlNormalized + ".session.log";
		
		File f = findSessionLogFile(logDir, logName);
		
		if (f == null || !f.exists()) {
			return null;
		}
		
		return readLogInternal(f, request);
	}
	
	public static List<String> readSessionLogInstanceIds(String logDir, String appUrl) throws WsException {
		Set<String> instanceIds = new LinkedHashSet<String>();
		
		try {
			Path dirPath = Paths.get(logDir);
			if (!dirPath.toFile().exists()) {
				return Collections.emptyList();
			}
			
			String appUrlNormalized = LogReaderUtil.normalizeForFileName(appUrl);
			Pattern p = Pattern.compile("webswing-(.*)-" + appUrlNormalized + ".session.log");
			
			Files.newDirectoryStream(dirPath, path -> path.toString().matches(".*" + normalizeForFileName(appUrl) + "\\." + WEBSWING_LOG_FILE_TYPE_SESSION + "\\.log\\.?[0-9]*$"))
			        .forEach(path -> {
			        	String fileName = path.getFileName().toString();
			        	
			        	Matcher m = p.matcher(fileName);
			        	if (m.find()) {
			        		String instanceId = m.group(1);
			        		if (StringUtils.isNotBlank(instanceId)) {
			        			instanceIds.add(instanceId);
			        		}
			        	}
			        });
		} catch (IOException e) {
			log.error("Error while browsing session log folder for app [" + appUrl + "]", e);
		}
		
		return new ArrayList<String>(instanceIds);
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
			File file = Paths.get(getDefaultLogDir() + filename).toAbsolutePath().normalize().toFile();
			return file;
		}
		return null;
	}
	
	private static File findSessionLogFile(String logDir, String logName) {
		return Paths.get(logDir + logName).toAbsolutePath().normalize().toFile();
	}
	
	private static List<File> findSessionLogFiles(String logDir, String appUrl) {
		List<File> logFiles = new ArrayList<File>();
		
		try {
			Path dirPath = Paths.get(logDir);
			if (!dirPath.toFile().exists()) {
				return logFiles;
			}
			
			Files.newDirectoryStream(dirPath, path -> path.toString().matches(".*" + normalizeForFileName(appUrl) + "\\." + WEBSWING_LOG_FILE_TYPE_SESSION + "\\.log\\.?[0-9]*$"))
			        .forEach(path -> logFiles.add(path.toFile()));
		} catch (IOException e) {
			log.error("Error while browsing session log folder for app [" + appUrl + "]", e);
		}
		
		return logFiles;
	}
	
	public static String getSessionLogDir(VariableSubstitutor subs, SwingConfig config) {
		String logDir = subs.replace(config.getLoggingDirectory());
		if (StringUtils.isBlank(logDir)) {
			logDir = LogReaderUtil.getDefaultLogDir();
		} else if (!logDir.endsWith("/") && !logDir.endsWith("\\")) {
			logDir = logDir + "/";
		}
		
		return logDir;
	}
	
	private static String getDefaultLogDir() {
		return System.getProperty(Constants.LOGS_DIR_PATH, "logs/");
	}

	public static String normalizeForFileName(String text) {
		return text.replaceAll("\\W+", "_");
	}
	
	public static InputStream getZippedLog(String type) throws WsException {
		return zipFiles(Lists.newArrayList(findLogFile(type)), type);
	}
	
	public static InputStream getZippedSessionLog(String logDir, String appUrl) throws WsException {
		return zipFiles(findSessionLogFiles(logDir, appUrl), normalizeForFileName(appUrl) + "_session");
	}
	
	private static InputStream zipFiles(List<File> files, String zipFileName) throws WsException {
		String tempDir = System.getProperty(Constants.TEMP_DIR_PATH);
		File zip = new File(URI.create(tempDir + zipFileName + ".zip"));
		
		if (files == null || files.isEmpty()) {
			return null;
		}
			
		try (FileOutputStream fos = new FileOutputStream(zip); ZipOutputStream out = new ZipOutputStream(fos)) {
			files.forEach(f -> {
				try (FileInputStream in = new FileInputStream(f)) {
					if (zip.canWrite()) {
						out.putNextEntry(new ZipEntry(f.getName()));
						IOUtils.copy(in, out);
					} else {
						throw new IOException("Can not write to file " + zip.getAbsolutePath());
					}
				} catch (IOException e) {
					log.error("Failed to zip the log file.", e);
				}
			});
			
			return new FileInputStream(zip);
		} catch (IOException e) {
			log.error("Failed to zip the log file.", e);
		}
		
		throw new WsException("Failed to download zipped logs [" + zipFileName + "]!");
	}
	
}
