package org.webswing.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Date;

import org.webswing.Constants;
import org.webswing.toolkit.util.Services;

public class SessionRecorder {
	
	private final String instanceId;

	private boolean recording = false;
	private OutputStream outputStream;
	private File tempFile;
	private SessionRecordingHeader header;

	private String fileName;

	private long lastFrame = 0;

	public SessionRecorder(String instanceId) {
		this.instanceId = instanceId;
	}

	private static byte[] serializeObject(Serializable o) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(o);
			return bos.toByteArray();
		} catch (IOException ex) {
			AppLogger.error("Failed to serialize object.", ex);
			throw ex;
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// ignore close exception
			}
			try {
				bos.close();
			} catch (IOException e) {
				// ignore close exception
			}
		}
	}

	public void saveFrame(byte[] serialized) {
		if (recording) {
			try {
				if (outputStream != null) {
					byte[] length = ByteBuffer.allocate(4).putInt(serialized.length).array();
					long now = new Date().getTime();
					this.lastFrame = this.lastFrame == 0 ? now : this.lastFrame;
					byte[] delay = ByteBuffer.allocate(4).putInt((int) (now - this.lastFrame)).array();
					this.lastFrame = now;
					try {
						outputStream.write(delay);
						outputStream.write(length);
						outputStream.write(serialized);
						outputStream.flush();
					} catch (IOException e) {
						AppLogger.error("Failed to write to session recording file.", e);
						throw e;
					}
				}
			} catch (Exception e) {
				AppLogger.error("Writing recorded frame failed. Stopping recording session.", e);
				try {
					stopRecording();
				} catch (Exception e1) {
					AppLogger.error("Stopping recording session failed.", e1);
				}
			}
		}
	}

	public void startRecording() throws Exception {
		if (!recording) {
			try {
				String fileName = URLEncoder.encode(instanceId, "UTF-8") + ".wss";
				this.fileName = fileName;
				AppLogger.info("Starting session recording for " + instanceId + " into file:" + fileName);
				//make sure previous out stream is closed
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (Exception e) {
						AppLogger.error("Failed to close previous recording!", e);
					}
				}
				
				String tempDir = System.getProperty(Constants.TEMP_DIR_PATH);
				tempFile = new File(URI.create(tempDir + fileName));
				if (tempFile.exists()) {
					tempFile.delete();
				} else if (!tempFile.getParentFile().exists()) {
					tempFile.getParentFile().mkdirs();
				}
				
				outputStream = new FileOutputStream(tempFile);
				header = new SessionRecordingHeader();
				header.setClientId(instanceId);
				header.setStartDate(new Date());
				this.lastFrame = new Date().getTime();
				byte[] version = ByteBuffer.allocate(4).putInt(SessionRecordingHeader.version).array();
				byte[] headerbytes = serializeObject(header);
				outputStream.write(version);
				recording = true;
				saveFrame(headerbytes);
			} catch (FileNotFoundException e) {
				AppLogger.error("Failed to create session recording file.", e);
				recording = false;
				throw new Exception("Failed to create session recording file.", e);
			} catch (IOException e) {
				AppLogger.error("Failed to start recording.", e);
				recording = false;
				throw new Exception("Failed to start recording.", e);
			}
		} else {
			throw new Exception("Already recording.");
		}
	}

	public void stopRecording() throws Exception {
		if (recording) {
			try {
				AppLogger.info("Stopping session recording for " + instanceId);
				if (outputStream != null) {
					try {
						outputStream.flush();
					} catch (IOException e) {
						AppLogger.error("Failed to close recording file.", e);
						throw e;
					}
					
					try (InputStream is = new FileInputStream(tempFile)) {
						Services.getDataStoreService().storeData("recording", fileName, is, true);
					} catch (Exception e) {
						AppLogger.error("Failed to store recording file [" + (tempFile == null ? "null" : tempFile.getAbsolutePath()) + "] to data store!", e);
						throw e;
					}
				}
			} catch (Exception e) {
				throw new Exception("Exception while finishing recording.", e);
			} finally {
				recording = false;
			}
		} else {
			throw new Exception("Recording not started.");
		}

	}

	public boolean isRecording() {
		return recording;
	}

	public String getFileName() {
		return fileName;
	}
	
}
