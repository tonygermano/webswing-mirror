package org.webswing.server.services.security.modules;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.otp.impl.AbstractOtpSecurityModule;
import org.webswing.server.services.security.otp.impl.WebswingOtpSecurityModuleConfig;

public abstract class AbstractUserPasswordSecurityModule<T extends WebswingOtpSecurityModuleConfig> extends AbstractOtpSecurityModule<T> {

	public AbstractUserPasswordSecurityModule(T config) {
		super(config);
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
	public AbstractWebswingUser getUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String username = getUserName(request);
			String password = getPassword(request);
			if (username != null || password != null) {
				AbstractWebswingUser user = verifyUserPassword(username, password);
				if (user != null) {
					return user;
				}
			}
			onAuthenticationFailed(request, response, null);
		} catch (WebswingAuthenticationException e) {
			onAuthenticationFailed(request, response, e);
		}
		return null;
	}

	protected void onAuthenticationFailed(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException {
		if (isAjax(request)) {
			serveLoginPageAjax(request, response, exception);
		} else {
			serveLoginPage(request, response, exception);
		}
	}

	private void serveLoginPage(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException {
		URL resource = getConfig().getContext().getWebResource(getLoginResource());
		if (resource != null) {
			String template = toString(resource.openStream(), 4 * 1024);
			String errorMessage = exception == null ? "" : exception.getLocalizedMessage();
			response.getOutputStream().print(template.replace("${errorMessage}", errorMessage));
		}
	}

	protected void serveLoginPageAjax(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException {
		response.sendRedirect("login");
	}

	protected boolean isAjax(HttpServletRequest request) {
		String requestedWithHeader = request.getHeader("X-Requested-With");
		if (requestedWithHeader != null && requestedWithHeader.equals("XMLHttpRequest")) {
			return true;
		}
		return false;
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

	public abstract AbstractWebswingUser verifyUserPassword(String user, String password) throws WebswingAuthenticationException;
}
