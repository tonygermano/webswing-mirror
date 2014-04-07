package org.webswing.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebswingConfiguration implements Serializable {

    private static final long serialVersionUID = 7439369522912189703L;

    private boolean swingDebugEnabled = false;
    private List<SwingApplicationDescriptor> applications = new ArrayList<SwingApplicationDescriptor>();

    public boolean isSwingDebugEnabled() {
        return swingDebugEnabled;
    }

    public void setSwingDebugEnabled(boolean swingDebugEnabled) {
        this.swingDebugEnabled = swingDebugEnabled;
    }

    public void setApplications(List<SwingApplicationDescriptor> applications) {
        this.applications = applications;
    }

    public List<SwingApplicationDescriptor> getApplications() {
        return applications;
    }

}
