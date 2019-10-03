package org.webswing.security.extension.onetimeurl;

import java.util.List;
@Deprecated
public interface OtpTokenData {

	public String getRequestorId();

	public String getUser();

	public List<String> getRoles();

	public List<String> getPermissions();

	public List<Object> getAttributes();

	public String getOneTimePassword();

}