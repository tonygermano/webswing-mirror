package org.webswing.server.services.security.otp.impl;

import java.io.Serializable;

public class OtpTokenData implements Serializable {
	private static final long serialVersionUID = -4871408167327762993L;

	private String swingPath;
	private String requestorId;
	private String user;
	private String[] roles;
	private String[] permissions;
	private String oneTimePassword;

	public OtpTokenData() {
	}

	public String getSwingPath() {
		return swingPath;
	}

	public void setSwingPath(String swingPath) {
		this.swingPath = swingPath;
	}

	public String getRequestorId() {
		return requestorId;
	}

	public void setRequestorId(String requestorId) {
		this.requestorId = requestorId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public String[] getPermissions() {
		return permissions;
	}

	public void setPermissions(String[] permissions) {
		this.permissions = permissions;
	}

	public String getOneTimePassword() {
		return oneTimePassword;
	}

	public void setOneTimePassword(String oneTimePassword) {
		this.oneTimePassword = oneTimePassword;
	}
}