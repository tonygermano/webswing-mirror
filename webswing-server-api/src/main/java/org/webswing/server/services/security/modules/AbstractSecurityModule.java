package org.webswing.server.services.security.modules;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.api.WebswingSecurityModuleConfig;
import org.webswing.server.services.security.api.WebswingUser;
import org.webswing.server.services.security.modules.AbstractSecurityModule.UserPasswordCredentials;

public abstract class AbstractSecurityModule implements WebswingSecurityModule<UserPasswordCredentials> {

	private WebswingSecurityModuleConfig config;

	public AbstractSecurityModule(WebswingSecurityModuleConfig config) {
		this.config = config;
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
			URL resource = config.getContext().getWebResource(getLoginResource());
			if (resource != null) {
				String template = toString(resource.openStream(), 4 * 1024);
				String errorMessage = e == null ? "" : e.getLocalizedMessage();
				response.getOutputStream().print(template.replace("${errorMessage}",errorMessage));
			}
			return null;
		} else {
			return new UserPasswordCredentials(username, password);
		}
	}

	public static String toString(final InputStream is, final int bufferSize) throws IOException {
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		Reader in = null;
		try {
			in = new InputStreamReader(is, "UTF-8");
			for (;;) {
				int rsz = in.read(buffer, 0, buffer.length);
				if (rsz < 0)
					break;
				out.append(buffer, 0, rsz);
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return out.toString();
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
