package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class UploadEventMsgIn implements MsgIn {

    private static final long serialVersionUID = -7188733550212761231L;

    public enum UploadType {
        Download,
        Upload,
        Delete;
    }

    public UploadType type;
    public String fileName;
    public String tempFileLocation;
}
