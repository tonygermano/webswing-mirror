package org.webswing.server.services.security.modules;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.api.WebswingSecurityModuleConfig;
import org.webswing.server.services.security.api.WebswingUser;
import org.webswing.server.services.security.modules.AbstractSecurityModule.UserPasswordCredentials;

public abstract class AbstractSecurityModule implements WebswingSecurityModule<UserPasswordCredentials> {

	public AbstractSecurityModule(WebswingSecurityModuleConfig config) {
	}
	
	@Override
	public void init() {
	}

	public String getLoginResource() {
		return "login.html";
	}

	public String getUserName(HttpServletRequest request) {
		return request.getParameter("username");
	}

	public String getPassword(HttpServletRequest request) {
		return request.getParameter("password");
	}

	@Override
	public UserPasswordCredentials getCredentials(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException e) throws IOException {
		String username = getUserName(request);
		String password = getPassword(request);
		if ((username == null && password == null) || e != null) {
			URL resource = this.getClass().getClassLoader().getResource(getLoginResource());
			if (resource != null) {
				String template = IOUtils.toString(resource.openStream());
				Map<String, String> map = new HashMap<String, String>();
				map.put("errorMessage", e == null ? "" : e.getLocalizedMessage());
				StrSubstitutor subs = new StrSubstitutor(map);
				response.getOutputStream().print(subs.replace(template));
			}
			return null;
		} else {
			return new UserPasswordCredentials(username, password);
		}
	}

	@Override
	public WebswingUser getUser(UserPasswordCredentials token) throws WebswingAuthenticationException {
		UserPasswordCredentials creds = (UserPasswordCredentials) token;
		return verifyUserPassword(creds.getUser(), creds.getPassword());
	}

	public abstract WebswingUser verifyUserPassword(String user, String password) throws WebswingAuthenticationException;

	@Override
	public void destroy() {
	}

	public static class UserPasswordCredentials implements WebswingCredentials {

		private String user;
		private String password;

		public UserPasswordCredentials(String user, String password) {
			super();
			this.user = user;
			this.password = password;
		}

		public String getUser() {
			return user;
		}

		public String getPassword() {
			return password;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((password == null) ? 0 : password.hashCode());
			result = prime * result + ((user == null) ? 0 : user.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			UserPasswordCredentials other = (UserPasswordCredentials) obj;
			if (password == null) {
				if (other.password != null)
					return false;
			} else if (!password.equals(other.password))
				return false;
			if (user == null) {
				if (other.user != null)
					return false;
			} else if (!user.equals(other.user))
				return false;
			return true;
		}

	}
}
