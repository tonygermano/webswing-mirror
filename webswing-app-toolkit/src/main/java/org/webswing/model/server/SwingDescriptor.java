package org.webswing.model.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SwingDescriptor implements Serializable {
	public enum SessionMode {
		ALWAYS_NEW_SESSION, CONTINUE_FOR_BROWSER, CONTINUE_FOR_USER;
	}

	private static final long serialVersionUID = 2413651075803737060L;
	private String name;
	private String icon;
	private String jreExecutable="${java.home}/bin/java"; 
	private String javaVersion="${java.version}";
	private String vmArgs = "";
	private List<String> classPathEntries = new ArrayList<String>();
	private String homeDir = "${user.dir}";
	private String theme = "Murrine";
	private int maxClients = 1;
	private SessionMode sessionMode=SessionMode.CONTINUE_FOR_BROWSER;
	private int swingSessionTimeout = 300;
	private boolean allowStealSession = true;
	private boolean antiAliasText = true;
	private boolean authorization = false;
	private boolean isolatedFs = false;
	private boolean debug = false;
	private boolean authentication = true;
	private boolean directdraw = false;
	private boolean allowDelete = true;
	private boolean allowDownload = true;
	private boolean allowAutoDownload =true;
	private boolean allowUpload = true;
	private float uploadMaxSize = 5; 
	private boolean allowJsLink = true;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getVmArgs() {
		return vmArgs;
	}

	public void setVmArgs(String vmArgs) {
		this.vmArgs = vmArgs;
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

	public String getHomeDir() {
		return homeDir;
	}

	public void setHomeDir(String homeDir) {
		this.homeDir = homeDir;
	}

	public int getMaxClients() {
		return maxClients;
	}

	public void setMaxClients(int maxClients) {
		this.maxClients = maxClients;
	}

	public boolean isAntiAliasText() {
		return antiAliasText;
	}

	public void setAntiAliasText(boolean antiAliasText) {
		this.antiAliasText = antiAliasText;
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

	public boolean isIsolatedFs() {
		return isolatedFs;
	}

	public void setIsolatedFs(boolean isolatedFs) {
		this.isolatedFs = isolatedFs;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isAuthentication() {
		return authentication;
	}

	public void setAuthentication(boolean authentication) {
		this.authentication = authentication;
	}

	public boolean isDirectdraw() {
		return directdraw;
	}

	public void setDirectdraw(boolean directdraw) {
		this.directdraw = directdraw;
	}

	public boolean isAllowDelete() {
		return allowDelete;
	}

	public void setAllowDelete(boolean allowDelete) {
		this.allowDelete = allowDelete;
	}

	public boolean isAllowDownload() {
		return allowDownload;
	}

	public void setAllowDownload(boolean allowDownload) {
		this.allowDownload = allowDownload;
	}

	public boolean isAllowUpload() {
		return allowUpload;
	}

	public void setAllowUpload(boolean allowUpload) {
		this.allowUpload = allowUpload;
	}

	public boolean isAllowJsLink() {
		return allowJsLink;
	}

	public void setAllowJsLink(boolean allowJsLink) {
		this.allowJsLink = allowJsLink;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getJreExecutable() {
		return jreExecutable;
	}

	public void setJreExecutable(String jreExecutable) {
		this.jreExecutable = jreExecutable;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public boolean isAllowAutoDownload() {
		return allowAutoDownload;
	}

	public void setAllowAutoDownload(boolean allowAutoDownload) {
		this.allowAutoDownload = allowAutoDownload;
	}

	public SessionMode getSessionMode() {
		return sessionMode;
	}

	public void setSessionMode(SessionMode sessionMode) {
		this.sessionMode = sessionMode;
	}

	public boolean isAllowStealSession() {
		return allowStealSession;
	}

	public void setAllowStealSession(boolean allowStealSession) {
		this.allowStealSession = allowStealSession;
	}

	public float getUploadMaxSize() {
		return uploadMaxSize;
	}

	public void setUploadMaxSize(float uploadMaxSize) {
		this.uploadMaxSize = uploadMaxSize;
	}


}
