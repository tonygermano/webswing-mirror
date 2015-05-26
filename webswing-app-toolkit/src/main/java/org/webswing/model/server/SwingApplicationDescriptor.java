package org.webswing.model.server;

public class SwingApplicationDescriptor extends SwingDescriptor {

	private static final long serialVersionUID = 3708498208353403978L;

	private String mainClass = "";
	private String args = "";

	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

}
