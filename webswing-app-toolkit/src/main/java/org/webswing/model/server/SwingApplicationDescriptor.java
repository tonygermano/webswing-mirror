package org.webswing.model.server;

import java.io.Serializable;
import java.util.List;

public class SwingApplicationDescriptor implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3708498208353403978L;

    private String name;
    private String icon;
    private String mainClass = "";
    private List<String> classPathEntries;
    private String vmArgs = "";
    private String args = "";
    private String homeDir = System.getProperty("user.dir");
    private int maxClients = 1;
    private boolean antiAliasText = true;
    private int swingSessionTimeout = 300;
    private boolean authorization = false;
    private boolean debug = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public List<String> getClassPathEntries() {
        return classPathEntries;
    }

    public String generateClassPathString() {
        String result = "";
        if (classPathEntries != null) {
            for (String cpe : classPathEntries) {
                result += cpe + ";";
            }
            result = result.substring(0, result.length() - 1);
        }
        return result;
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

    public boolean isAntiAliasText() {
        return antiAliasText;
    }

    public void setAntiAliasText(boolean antiAliasText) {
        this.antiAliasText = antiAliasText;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getMaxClients() {
        return maxClients;
    }

    public void setMaxClients(int maxClients) {
        this.maxClients = maxClients;
    }

    public int getSwingSessionTimeout() {
        return swingSessionTimeout;
    }

    public void setSwingSessionTimeout(int swingSessionTimeout) {
        this.swingSessionTimeout = swingSessionTimeout;
    }

    public boolean isAuthorization() {
        return authorization;
    }

    public void setAuthorization(boolean authorization) {
        this.authorization = authorization;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

}
