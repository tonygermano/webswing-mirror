package org.webswing.server.base;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.SecurableService;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.login.WebswingSecurityProvider;
import org.webswing.server.util.SecurityUtil;
import org.webswing.server.util.ServerUtil;

public abstract class AbstractUrlHandler implements UrlHandler, SecurableService {
	private static final Logger log = LoggerFactory.getLogger(AbstractUrlHandler.class);

	private final UrlHandler parent;
	private final LinkedList<UrlHandler> childHandlers = new LinkedList<UrlHandler>();

	public AbstractUrlHandler(UrlHandler parent) {
		this.parent = parent;

	}

	@Override
	public void init() {
		synchronized (childHandlers) {
			for (UrlHandler handler : childHandlers) {
				try {
					handler.init();
				} catch (Exception e) {
					log.error("Failed to initialize child handler: " + handler.getClass().getName(), e);
				}
			}
		}
	}

	@Override
	public void destroy() {
		synchronized (childHandlers) {
			for (UrlHandler handler : childHandlers) {
				try {
					handler.destroy();
				} catch (Exception e) {
					log.error("Failed to destroy child handler: " + handler.getClass().getName(), e);
				}
			}
		}
	}

	@Override
	public boolean serve(HttpServletRequest req, HttpServletResponse res) throws WsException {
		//redirect to url that ends with '/' to ensure browser queries correct resources 
		if (isPrimaryHandler() && (req.getPathInfo() == null || (req.getContextPath() + req.getPathInfo()).equals(getFullPathMapping()))) {
			try {
				String queryString = req.getQueryString() == null ? "" : ("?" + req.getQueryString());
				res.sendRedirect(getFullPathMapping() + "/" + queryString);
			} catch (IOException e) {
				log.error("Failed to redirect.", e);
			}
			return true;
		} else {
			String pathinfo = getPathInfo(req);
			List<UrlHandler> localHandlerList = new LinkedList<UrlHandler>(childHandlers);
			for (UrlHandler child : localHandlerList) {
				if (isSubPath(toPath(child.getPathMapping()), pathinfo)) {
					boolean served = child.serve(req, res);
					if (served) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public Object secureServe(HttpServletRequest req, HttpServletResponse res) throws WsException {
		return serve(req, res);
	}

	protected boolean isPrimaryHandler() {
		return false;
	}

	public String getFullPathMapping() {
		String handlerPath = toPath(getPathMapping());
		if (this.parent != null) {
			String parentMapping = parent.getFullPathMapping();
			handlerPath = parentMapping + handlerPath;
		}
		return handlerPath;
	}

	public String getPathInfo(HttpServletRequest req) {
		String fullHandlerPath = getFullPathMapping();
		String requestPath = toPath(req.getContextPath() + req.getPathInfo());
		if (isSubPath(fullHandlerPath, requestPath)) {
			return toPath(requestPath.substring(fullHandlerPath.length()));
		} else {
			return "/";
		}
	}

	protected abstract String getPath();

	public String getPathMapping() {
		String path = toPath(getPath());
		if (parent == null) {
			String contextPath = getServletContext().getContextPath();
			if (contextPath != null && !contextPath.equals("/")) {
				path = toPath(contextPath) + path;
			}
		}
		return path;
	}

	public static boolean isSubPath(String subpath, String path) {
		return ServerUtil.isSubPath(subpath, path);
	}

	public static String toPath(String path) {
		return ServerUtil.toPath(path);
	}

	public void registerFirstChildUrlHandler(UrlHandler handler) {
		handler.init();
		childHandlers.addFirst(handler);
	}

	@Override
	public void registerChildUrlHandler(UrlHandler handler) {
		handler.init();
		childHandlers.add(handler);
	}

	@Override
	public void removeChildUrlHandler(UrlHandler handler) {
		if (childHandlers.contains(handler)) {
			childHandlers.remove(handler);
			handler.destroy();
		}
	}

	public ServletContext getServletContext() {
		return parent.getServletContext();
	}

	public String getSecuredPath() {
		if (WebswingSecurityProvider.class.isAssignableFrom(this.getClass())) {
			WebswingSecurityProvider provider = (WebswingSecurityProvider) this;
			if (provider.get() != null) {
				return getFullPathMapping();
			}
		}
		if (parent == null) {
			return "/";
		} else {
			return parent.getSecuredPath();
		}

	}

	public WebswingSecurityProvider getSecurityProvider() {
		if (WebswingSecurityProvider.class.isAssignableFrom(this.getClass())) {
			WebswingSecurityProvider provider = (WebswingSecurityProvider) this;
			if (provider.get() != null) {
				return provider;
			}
		}
		if (parent == null) {
			return (WebswingSecurityProvider) this;
		} else {
			return parent.getSecurityProvider();
		}
	}

	public long getLastModified(HttpServletRequest req) {
		return -1;
	}

	public AbstractWebswingUser getUser() {
		return SecurityUtil.getUser(this);
	}

	@Override
	public void checkPermission(WebswingAction action) throws WsException {
		AbstractWebswingUser user = getUser();
		if (user != null) {
			if (user.isPermitted(action.name())) {
				return;
			}
		}
		throw new WsException("User '" + user + "' is not allowed to execute action '" + action + "'", HttpServletResponse.SC_UNAUTHORIZED);
	}

}
