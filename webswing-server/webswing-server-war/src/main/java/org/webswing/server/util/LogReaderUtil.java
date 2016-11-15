package org.webswing.server.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;

import org.webswing.server.common.model.rest.LogRequest;
import org.webswing.server.common.model.rest.LogResponse;
import org.webswing.server.common.util.VariableSubstitutor;
import org.webswing.server.model.exception.WsException;

public class LogReaderUtil {

	private static final String WEBSWING_LOG_FILE_TYPE_PREFIX = "webswing.log.file.";

	public static LogResponse readLog(String type, LogRequest request) throws WsException {
		File f = findLogFile(type);
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

	private static File findLogFile(String type) {
		String filename = System.getProperty(WEBSWING_LOG_FILE_TYPE_PREFIX + type);
		if (filename != null) {
			filename = VariableSubstitutor.basic().replace(filename);
			File file = Paths.get(filename).toAbsolutePath().normalize().toFile();
			return file;
		}
		return null;
	}
}
