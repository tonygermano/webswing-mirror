package org.webswing.model.c2s;

public class JsonEventUpload implements JsonEvent {

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
