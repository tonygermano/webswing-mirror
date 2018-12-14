package org.webswing.server.services.recorder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.model.SessionRecordingHeader;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swingmanager.SwingInstanceManager;

public class SessionRecorderImpl implements SessionRecorder {
	private static final Logger log = LoggerFactory.getLogger(SessionRecorderImpl.class);
	private final SwingInstance swingInstance;
	private final String recordingDir;

	private boolean recording = false;
	private FileOutputStream outputStream;
	private SessionRecordingHeader header;

	private String fileName;

	private long lastFrame=0;

	public SessionRecorderImpl(SwingInstance swingInstance, SwingInstanceManager manager) {
		this.swingInstance = swingInstance;
		this.recordingDir = manager.getRecordingsDirPath();
	}

	private static byte[] serializeObject(Serializable o) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(o);
			return bos.toByteArray();
		} catch (IOException ex) {
			log.error("Failed to serialize object.", ex);
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
					this.lastFrame= this.lastFrame==0?now:this.lastFrame;
					byte[] delay = ByteBuffer.allocate(4).putInt((int) (now - this.lastFrame)).array();
					this.lastFrame = now;
					try {
						outputStream.write(delay);
						outputStream.write(length);
						outputStream.write(serialized);
						outputStream.flush();
					} catch (IOException e) {
						log.error("Failed to write to session recording file.", e);
						throw e;
					}
				}
			} catch (Exception e) {
				log.error("Writing recorded frame failed. Stopping recording session.", e);
				try {
					stopRecording();
				} catch (WsException e1) {
					log.error("Stopping recording session failed.", e1);
				}
			}
		}
	}

	@Override
	public void startRecording() throws WsException {
		if (!recording) {
			try {
				String fileName = URLEncoder.encode(swingInstance.getInstanceId(), "UTF-8") + ".wss";
				URI uri = URI.create(this.recordingDir + fileName);
				File file = new File(uri);
				if(file.exists()){
					file.delete();
				}else if( !file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
				this.fileName = fileName;
				log.info("Starting session recording for " + swingInstance.getInstanceId() + " into file:" + uri);
				//make sure previous out stream is closed
				if(outputStream!=null){
					outputStream.close();
				}
				outputStream = new FileOutputStream(file);
				header = new SessionRecordingHeader();
				header.setClientId(swingInstance.getInstanceId());
				header.setStartDate(new Date());
				this.lastFrame = header.getStartDate().getTime();
				byte[] version = ByteBuffer.allocate(4).putInt(SessionRecordingHeader.version).array();
				byte[] headerbytes = serializeObject(header);
				outputStream.write(version);
				recording=true;
				saveFrame(headerbytes);
			} catch (FileNotFoundException e) {
				log.error("Failed to create session recording file.", e);
				recording = false;
				throw new WsException("Failed to create session recording file.", e);
			} catch (IOException e) {
				log.error("Failed to start recording.", e);
				recording = false;
				throw new WsException("Failed to start recording.", e);
			}
		} else {
			throw new WsException("Already recording.");
		}
	}

	@Override
	public void stopRecording() throws WsException {
		if (recording) {
			try {
				log.info("Stopping session recording for " + swingInstance.getInstanceId());
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException e) {
				log.error("Failed to close recording file.", e);
				throw new WsException("Failed to close recording file.", e);
			} finally {
				recording=false;
			}
		} else {
			throw new WsException("Recording not started.");
		}

	}

	@Override
	public boolean isRecording() {
		return recording;
	}

	public String getFileName() {
		return fileName;
	}

}
