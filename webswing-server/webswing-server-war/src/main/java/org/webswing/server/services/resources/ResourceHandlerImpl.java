package org.webswing.server.services.resources;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.SecurityContext;

public class ResourceHandlerImpl extends AbstractUrlHandler implements ResourceHandler {

	private SecurityContext context;

	public ResourceHandlerImpl(UrlHandler parent, SecurityContext context) {
		super(parent);
		this.context = context;
	}

	@Override
	protected String getPath() {
		return "";
	}

	@Override
	public boolean serve(HttpServletRequest req, HttpServletResponse res) throws WsException {
		try {
			if (req.getMethod().equals("GET") || req.getMethod().equals("PUT")) {
				return lookup(req).respondGet(res);
			} else if (req.getMethod().equals("HEAD")) {
				return lookup(req).respondHead(res);
			}
			return false;
		} catch (IOException e) {
			throw new WsException("Failed to process resource.", e);
		}
	}

	public static interface LookupResult {
		public boolean respondGet(HttpServletResponse resp) throws IOException;

		public boolean respondHead(HttpServletResponse resp) throws IOException;

		public long getLastModified();
	}

	public static class ErrorResult implements LookupResult {
		protected final int statusCode;
		protected final String message;

		public ErrorResult(int statusCode, String message) {
			this.statusCode = statusCode;
			this.message = message;
		}

		public long getLastModified() {
			return -1;
		}

		public boolean respondGet(HttpServletResponse resp) throws IOException {
			return false;
		}

		public boolean respondHead(HttpServletResponse resp) {
			return false;
		}
	}

	public static class RedirectResult implements LookupResult {

		private String path;

		public RedirectResult(String path) {
			if(path.startsWith("/")){
				path=path.substring(1);
			}
			this.path = path;
		}

		public long getLastModified() {
			return -1;
		}

		public boolean respondGet(HttpServletResponse resp) throws IOException {
			resp.sendRedirect(path);
			return true;
		}

		public boolean respondHead(HttpServletResponse resp) throws IOException {
			resp.sendRedirect(path);
			return true;
		}
	}

	public static class StaticFile implements LookupResult {
		protected final long lastModified;
		protected final String mimeType;
		protected final int contentLength;
		protected final URL url;

		public StaticFile(long lastModified, String mimeType, int contentLength, URL url) {
			this.lastModified = lastModified;
			this.mimeType = mimeType;
			this.contentLength = contentLength;
			this.url = url;
		}

		public long getLastModified() {
			return lastModified;
		}

		protected void setHeaders(HttpServletResponse resp) {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType(mimeType);
			if (contentLength >= 0)
				resp.setContentLength(contentLength);
		}

		public boolean respondGet(HttpServletResponse resp) throws IOException {
			setHeaders(resp);
			final OutputStream os = resp.getOutputStream();
			CommonUtil.transferStreams(url.openStream(), os);
			return true;
		}

		public boolean respondHead(HttpServletResponse resp) {
			setHeaders(resp);
			return true;
		}
	}

	@Override
	public long getLastModified(HttpServletRequest req) {
		return lookup(req).getLastModified();
	}

	protected LookupResult lookup(HttpServletRequest req) {
		LookupResult r = (LookupResult) req.getAttribute("lookupResult-" + getFullPathMapping());
		if (r == null) {
			r = lookupNoCache(req);
			req.setAttribute("lookupResult-" + getFullPathMapping(), r);
		}
		return r;
	}

	protected LookupResult lookupNoCache(HttpServletRequest req) {
		String path = getPathInfo(req);
		return lookupNoCache(req, path);
	}

	protected LookupResult lookupNoCache(HttpServletRequest req, String path) {
		if (path.equals("")) {
			path = "/index.html";
		}
		if (isForbidden(path))
			return new ErrorResult(HttpServletResponse.SC_FORBIDDEN, "Forbidden");

		final URL url = context.getWebResource(path);
		if (url == null) {
			return new ErrorResult(HttpServletResponse.SC_NOT_FOUND, "Not found");
		}

		final String mimeType = getMimeType(url.getPath());

		final String realpath = url.getPath();
		if (realpath != null) {
			// Try as an ordinary file
			File f = new File(realpath);
			if (!f.isFile())
				if (req.getPathInfo().endsWith("/")) {
					return lookupNoCache(req, path + "/index.html");
				} else {
					return new RedirectResult(path + "/");
				}
			else
				return new StaticFile(f.lastModified(), mimeType, (int) f.length(), url);
		} else {
			try {
				// Try as a JAR Entry
				final ZipEntry ze = ((JarURLConnection) url.openConnection()).getJarEntry();
				if (ze != null) {
					if (ze.isDirectory())
						if (req.getPathInfo().endsWith("/")) {
							return lookupNoCache(req, path + "/index.html");
						} else {
							return new RedirectResult(path + "/");
						}
					else
						return new StaticFile(ze.getTime(), mimeType, (int) ze.getSize(), url);
				} else
					// Unexpected?
					return new StaticFile(-1, mimeType, -1, url);
			} catch (ClassCastException e) {
				// Unknown resource type
				return new StaticFile(-1, mimeType, -1, url);
			} catch (IOException e) {
				return new ErrorResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
			}
		}
	}

	protected boolean isForbidden(String path) {
		String lpath = path.toLowerCase();
		return lpath.startsWith("/web-inf/") || lpath.startsWith("/meta-inf/");
	}

	protected String getMimeType(String path) {
		String mime = getServletContext().getMimeType(path);
		return mime != null ? mime : "application/octet-stream";
	}

}
