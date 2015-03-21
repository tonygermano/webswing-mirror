package org.webswing.server.stats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import org.webswing.Constants;
import org.webswing.server.SwingInstance;

public class SessionRecorder {

	FileOutputStream out;
	private SwingInstance swingInstance;

	public SessionRecorder(SwingInstance swingInstance) {
		this.swingInstance = swingInstance;
	}

	public void saveFrame(byte[] serialized) {
		if (out == null) {
			URI file = URI.create(System.getProperty(Constants.TEMP_DIR_PATH) + "/" + swingInstance.getClientId() + ".wss");
			try {
				out = new FileOutputStream(new File(file));
				System.out.println("Recording to " + file.toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (out != null) {
			byte[] length = ByteBuffer.allocate(4).putInt(serialized.length).array();
			try {
				out.write(length);
				out.write(serialized);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
