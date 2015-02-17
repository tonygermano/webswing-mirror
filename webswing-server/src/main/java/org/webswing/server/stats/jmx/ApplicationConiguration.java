package org.webswing.server.stats.jmx;

import java.util.List;

import org.webswing.model.server.SwingApplicationDescriptor;

public class ApplicationConiguration {
	SwingApplicationDescriptor sad;

	public ApplicationConiguration(SwingApplicationDescriptor sad) {
		this.sad = sad;
	}

	public String getName() {
		return sad.getName();
	}

	public String getMainClass() {
		return sad.getMainClass();
	}

	public List<String> getClassPathEntries() {
		return sad.getClassPathEntries();
	}

	public String getVmArgs() {
		return sad.getVmArgs();
	}

	public String getArgs() {
		return sad.getArgs();
	}

	public String getHomeDir() {
		return sad.getHomeDir();
	}

	public boolean isAntiAliasText() {
		return sad.isAntiAliasText();
	}

	public String getIcon() {
		return sad.getIcon();
	}

	public int getMaxClients() {
		return sad.getMaxClients();
	}

	public int getSwingSessionTimeout() {
		return sad.getSwingSessionTimeout();
	}

	public boolean isAuthorization() {
		return sad.isAuthorization();
	}

	public boolean isDebug() {
		return sad.isDebug();
	}

	public boolean isIsolatedFs() {
		return sad.isIsolatedFs();
	}

	public boolean isAuthentication() {
		return sad.isAuthentication();
	}

	public boolean isDirectdraw() {
		return sad.isDirectdraw();
	}

	public boolean isAllowDelete() {
		return sad.isAllowDelete();
	}

	public boolean isAllowDownload() {
		return sad.isAllowDownload();
	}

	public boolean isAllowUpload() {
		return sad.isAllowUpload();
	}
}
