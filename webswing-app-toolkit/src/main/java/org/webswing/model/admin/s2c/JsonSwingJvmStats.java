package org.webswing.model.admin.s2c;

import java.io.Serializable;
import java.util.Date;

public class JsonSwingJvmStats implements Serializable {

    private static final long serialVersionUID = 3515968260132337452L;

    private Date snapshotTime;
    private double heapSize;
    private double heapSizeUsed;

    public double getHeapSize() {
        return heapSize;
    }

    public void setHeapSize(double heapSize) {
        this.heapSize = heapSize;
    }

    public double getHeapSizeUsed() {
        return heapSizeUsed;
    }

    public void setHeapSizeUsed(double heapSizeUsed) {
        this.heapSizeUsed = heapSizeUsed;
    }

    
    public Date getSnapshotTime() {
        return snapshotTime;
    }

    
    public void setSnapshotTime(Date snapshotTime) {
        this.snapshotTime = snapshotTime;
    }


}
