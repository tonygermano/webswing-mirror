package org.webswing.model.s2c;

import java.io.Serializable;

public class JsonFileDialogEvent implements Serializable {

    private static final long serialVersionUID = -6782940433721602460L;

    public enum FileDialogEventType {
        Open,
        Close;
    }

    public FileDialogEventType eventType;
    public boolean allowDelete = true;
    public boolean allowDownload = true;
    public boolean allowUpload = true;
}
