package org.webswing.server.recording;

import java.io.Serializable;
import java.util.Date;

import org.webswing.model.server.SwingDescriptor;

public class RecordHeader implements Serializable {
	private static final long serialVersionUID = 3683092380648813794L;
	public static final int version = 1;
	protected Date startDate;
	protected String clientId;
	protected SwingDescriptor application;

}