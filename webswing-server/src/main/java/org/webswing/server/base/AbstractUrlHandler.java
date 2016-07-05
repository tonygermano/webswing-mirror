package org.webswing.server.base;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.WebswingUser;
import org.webswing.server.services.security.login.WebswingSecurityProvider;
import org.webswing.server.util.SecurityUtil;

public abstract class AbstractUrlHandler implements UrlHandler {
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
		String requestPath = toPath(req.getPathInfo());
		if (isSubPath(fullHandlerPath, requestPath)) {
			return toPath(requestPath.substring(fullHandlerPath.length()));
		} else {
			return "/";
		}
	}

	protected abstract String getPath();

	public String getPathMapping() {
		return toPath(getPath());
	}

	public static boolean isSubPath(String subpath, String path) {
		return path.equals(subpath) || path.startsWith(subpath + "/");
	}

	public static String toPath(String path) {
		String mapping = path == null ? "/" : path;
		mapping = mapping.startsWith("/") ? mapping : ("/" + mapping);
		mapping = mapping.endsWith("/") ? mapping.substring(0, mapping.length() - 1) : mapping;
		return mapping;
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

	public long getLastModified(HttpServletRequest req) {
		return -1;
	}

	public WebswingUser getUser() {
		return SecurityUtil.getUser(this);
	}

	//	@SuppressWarnings("unchecked")
	//	public <T> T findParent(Class<T> clazz) {
	//		if (parent == null || clazz.isAssignableFrom(parent.getClass())) {
	//			return (T) parent;
	//		} else {
	//			return parent.findParent(clazz);
	//		}
	//	}
}
