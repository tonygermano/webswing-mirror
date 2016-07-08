package org.webswing.server.services.rest;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;

public abstract class AbstractRestUrlHandler extends AbstractUrlHandler {
	private static final Logger log = LoggerFactory.getLogger(AbstractRestUrlHandler.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	private static final String GET = "GET";
	private static final String PUT = "PUT";
	private static final String POST = "POST";
	private static final String DELETE = "DELETE";

	public AbstractRestUrlHandler(UrlHandler parent) {
		super(parent);
	}

	@Override
	protected String getPath() {
		return "rest";
	}

	@Override
	public boolean serve(HttpServletRequest req, HttpServletResponse res) throws WsException {
		boolean served = super.serve(req, res);
		if (!served) {
			String httpMethod = req.getMethod();
			String path = getPathInfo(req);
			Method method = findHandlerMethod(httpMethod, path);
			if (method != null) {
				method.setAccessible(true);
				Object[] args = resolveParameters(method, req);
				Object result;
				try {
					result = method.invoke(this, args);
				} catch (InvocationTargetException e1) {
					result = e1.getTargetException();
				} catch (Exception e) {
					result = e;
				}
				try {
					writeResponse(method, req, res, result);
					return true;
				} catch (WsException e) {
					throw e;
				}
			}
			return false;
		}
		return served;
	}

	private Method findHandlerMethod(String httpMethod, String path2) {
		Class<? extends Annotation> methodAnnotation = httpMethod2Annotation(httpMethod);
		Method match = null;
		int extent = path2.length();
		for (Method method : this.getClass().getDeclaredMethods()) {
			if (method.getAnnotation(methodAnnotation) != null) {
				Path pathAnnotation;
				if ((pathAnnotation = method.getAnnotation(Path.class)) != null) {
					if (isSubPath(pathAnnotation.value(), path2)) {
						if (path2.length() - pathAnnotation.value().length() < extent) {
							match = method;
							extent = path2.length() - pathAnnotation.value().length();
						}
					}
				}
			}
		}
		return match;
	}

	private static Class<? extends Annotation> httpMethod2Annotation(String httpMethod) {
		if (httpMethod.equals(GET)) {
			return javax.ws.rs.GET.class;
		} else if (httpMethod.equals(PUT)) {
			return javax.ws.rs.PUT.class;
		} else if (httpMethod.equals(POST)) {
			return javax.ws.rs.POST.class;
		} else if (httpMethod.equals(DELETE)) {
			return javax.ws.rs.DELETE.class;
		} else {
			return null;
		}
	}

	private Object[] resolveParameters(Method method, HttpServletRequest req) {
		Object[] result = new Object[method.getParameterCount()];
		Parameter[] params = method.getParameters();
		for (int i = 0; i < params.length; i++) {
			QueryParam paramAnno;
			if (params[i].getType().isAssignableFrom(HttpServletRequest.class)) {
				result[i] = req;
			} else if ((paramAnno = params[i].getAnnotation(javax.ws.rs.QueryParam.class)) != null) {
				result[i] = req.getParameter(paramAnno.value());
			} else if (params[i].getAnnotation(javax.ws.rs.PathParam.class) != null && String.class.isAssignableFrom(params[i].getType())) {
				String path2 = method.getAnnotation(Path.class).value();
				result[i] = getPathInfo(req).substring(path2.length());
			} else if (String.class.isAssignableFrom(params[i].getType())) {
				try {
					result[i] = IOUtils.toString(req.getReader());
				} catch (IOException e) {
					result[i] = null;
				}
			} else {
				try {
					String content = IOUtils.toString(req.getReader());
					result[i] = mapper.readValue(content, params[i].getType());
				} catch (IOException e) {
					result[i] = null;
				}
			}
		}
		return result;
	}

	private void writeResponse(Method method, HttpServletRequest req, HttpServletResponse res, Object result) throws WsException {
		if (result != null && result instanceof Throwable) {
			if (result instanceof WsException) {
				throw (WsException) result;
			} else {
				log.error("Invocation of REST method failed.", (Throwable) result);
				throw new WsException("Invocation of REST method failed.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			res.setStatus(HttpServletResponse.SC_OK);
			if (!method.getReturnType().equals(Void.TYPE) && result != null) {
				try {
					if (method.getReturnType().equals(String.class)) {
						IOUtils.write(result.toString(), res.getOutputStream());
					} else {
						mapper.writerWithDefaultPrettyPrinter().writeValue(res.getOutputStream(), result);
					}
				} catch (Exception e) {
					log.error("Failed to serialize REST execution result." + req.getRequestURI(), e);
					throw new WsException("Serializing of REST result failed.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}
		}
	}

}
