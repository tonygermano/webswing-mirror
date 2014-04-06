package org.webswing.server.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WebswingConfiguration implements Serializable {

    private static final long serialVersionUID = 7439369522912189703L;

    private boolean swingDebugEnabled=false;
    private Map<String, SwingApplicationDescriptor> applications=new HashMap<String, SwingApplicationDescriptor>();

    public boolean isSwingDebugEnabled() {
        return swingDebugEnabled;
    }

    public void setSwingDebugEnabled(boolean swingDebugEnabled) {
        this.swingDebugEnabled = swingDebugEnabled;
    }

    public Map<String, SwingApplicationDescriptor> getApplications() {
        return applications;
    }

    public void setApplications(Map<String, SwingApplicationDescriptor> applications) {
        this.applications = applications;
    }

}
