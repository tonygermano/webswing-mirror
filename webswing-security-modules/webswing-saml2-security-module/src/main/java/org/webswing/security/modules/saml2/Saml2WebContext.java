package org.webswing.security.modules.saml2;

import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.J2ESessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.TechnicalException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;

public class Saml2WebContext implements WebContext {

	private final HttpServletRequest request;

	private final HttpServletResponse response;
	private final SessionStore sessionStore;
	private String content = "";

	public Saml2WebContext(final HttpServletRequest request, final SessionStore sessionStore) {
		this(request, null, sessionStore);
	}

	public Saml2WebContext(final HttpServletRequest request, final HttpServletResponse response, final SessionStore sessionStore) {
		this.request = request;
		this.response = response;
		if (sessionStore == null) {
			this.sessionStore = new J2ESessionStore();
		} else {
			this.sessionStore = sessionStore;
		}
	}

	@Override
	public SessionStore getSessionStore() {
		return this.sessionStore;
	}

	@Override
	public String getRequestParameter(final String name) {
		return this.request.getParameter(name);
	}

	@Override
	public Object getRequestAttribute(final String name) {
		return this.request.getAttribute(name);
	}

	@Override
	public void setRequestAttribute(final String name, final Object value) {
		this.request.setAttribute(name, value);
	}

	@Override
	public Map<String, String[]> getRequestParameters() {
		return this.request.getParameterMap();
	}

	@Override
	public String getRequestHeader(final String name) {
		final Enumeration<String> names = request.getHeaderNames();
		if (names != null) {
			while (names.hasMoreElements()) {
				final String headerName = names.nextElement();
				if (headerName != null && headerName.equalsIgnoreCase(name)) {
					return this.request.getHeader(headerName);
				}
			}
		}
		return null;
	}

	@Override
	public String getRequestMethod() {
		return this.request.getMethod();
	}

	@Override
	public String getRemoteAddr() {
		return this.request.getRemoteAddr();
	}

	/**
	 * Return the HTTP request.
	 *
	 * @return the HTTP request
	 */
	public HttpServletRequest getRequest() {
		return this.request;
	}

	/**
	 * Return the HTTP response.
	 *
	 * @return the HTTP response
	 */
	public HttpServletResponse getResponse() {
		return this.response;
	}

	@Override
	public void writeResponseContent(final String content) {
		this.content += content;
	}

	@Override
	public void setResponseStatus(final int code) {
		if (response != null) {
			this.response.setStatus(code);
		}
	}

	@Override
	public void setResponseHeader(final String name, final String value) {
		if (response != null) {
			this.response.setHeader(name, value);
		}
	}

	@Override
	public void setResponseContentType(final String content) {
		if (response != null) {
			this.response.setContentType(content);
		}
	}

	@Override
	public String getServerName() {
		return this.request.getServerName();
	}

	@Override
	public int getServerPort() {
		return this.request.getServerPort();
	}

	@Override
	public String getScheme() {
		return this.request.getScheme();
	}

	@Override
	public boolean isSecure() {
		return this.request.isSecure();
	}

	@Override
	public String getFullRequestURL() {
		final StringBuffer requestURL = request.getRequestURL();
		final String queryString = request.getQueryString();
		if (queryString == null) {
			return requestURL.toString();
		}
		return requestURL.append('?').append(queryString).toString();
	}

	@Override
	public Collection<Cookie> getRequestCookies() {
		final Collection<Cookie> pac4jCookies = new LinkedHashSet<>();
		final javax.servlet.http.Cookie[] cookies = this.request.getCookies();

		if (cookies != null) {
			for (final javax.servlet.http.Cookie c : cookies) {
				final Cookie cookie = new Cookie(c.getName(), c.getValue());
				cookie.setVersion(c.getVersion());
				cookie.setComment(c.getComment());
				cookie.setDomain(c.getDomain());
				cookie.setHttpOnly(c.isHttpOnly());
				cookie.setMaxAge(c.getMaxAge());
				cookie.setPath(c.getPath());
				cookie.setSecure(c.getSecure());
				pac4jCookies.add(cookie);
			}
		}
		return pac4jCookies;
	}

	@Override
	public void addResponseCookie(final Cookie cookie) {
		if (response != null) {
			final javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(cookie.getName(), cookie.getValue());
			c.setVersion(cookie.getVersion());
			c.setSecure(cookie.isSecure());
			c.setPath(cookie.getPath());
			c.setMaxAge(cookie.getMaxAge());
			c.setHttpOnly(cookie.isHttpOnly());
			c.setComment(cookie.getComment());
			c.setDomain(cookie.getDomain());
			this.response.addCookie(c);
		}
	}

	/**
	 * This is not implemented using {@link HttpServletRequest#getServletPath()} or
	 * {@link HttpServletRequest#getPathInfo()} because they both have strange behaviours
	 * in different contexts (inside servlets, inside filters, various container implementation, etc)
	 */
	@Override
	public String getPath() {
		final String fullPath = request.getRequestURI();
		// it shouldn't be null, but in case it is, it's better to return empty string
		if (fullPath == null) {
			return "";
		}
		final String context = request.getContextPath();
		// this one shouldn't be null either, but in case it is, then let's consider it is empty
		if (context != null) {
			return fullPath.substring(context.length());
		}
		return fullPath;
	}

	@Override
	public String getRequestContent() {
		try {
			return request.getReader().lines().reduce("", (accumulator, actual) -> accumulator.concat(actual));
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	@Override
	public String getProtocol() {
		return request.getProtocol();
	}
}
