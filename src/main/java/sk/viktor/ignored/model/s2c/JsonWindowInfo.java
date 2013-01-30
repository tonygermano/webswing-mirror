package sk.viktor.ignored.model.s2c;

import java.io.Serializable;

public class JsonWindowInfo implements Serializable {
    private static final long serialVersionUID = 5192907046398894082L;
    
    String id;
    String parentId;
    String title;
    int width;
    int height;
    boolean modal;
    boolean hasFocus;
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
    }

    
    public boolean isHasFocus() {
        return hasFocus;
    }

    
    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }

    
    public String getTitle() {
        return title;
    }

    
    public void setTitle(String title) {
        this.title = title;
    }

    
}
