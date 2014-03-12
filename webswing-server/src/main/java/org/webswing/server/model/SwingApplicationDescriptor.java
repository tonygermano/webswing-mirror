package org.webswing.server.model;

import java.io.Serializable;
import java.util.List;

public class SwingApplicationDescriptor implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 3708498208353403978L;
    
    private String mainClass;
    private List<String> classPathEntries;
    private String vmArgs;
    private String args;
    private String homeDir;
    private boolean homeDirPerSession;
    
    public String getMainClass() {
        return mainClass;
    }
    
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    
    public List<String> getClassPathEntries() {
        return classPathEntries;
    }
    
    public void setClassPathEntries(List<String> classPathEntries) {
        this.classPathEntries = classPathEntries;
    }
    
    public String getVmArgs() {
        return vmArgs;
    }
    
    public void setVmArgs(String vmArgs) {
        this.vmArgs = vmArgs;
    }
    
    public String getArgs() {
        return args;
    }
    
    public void setArgs(String args) {
        this.args = args;
    }
    
    public String getHomeDir() {
        return homeDir;
    }
    
    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }
    
    public boolean isHomeDirPerSession() {
        return homeDirPerSession;
    }
    
    public void setHomeDirPerSession(boolean homeDirPerSession) {
        this.homeDirPerSession = homeDirPerSession;
    }


}
