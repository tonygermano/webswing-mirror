package org.webswing.server.services.rest;

import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.*;
import org.glassfish.jersey.server.internal.ContainerUtils;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.AbstractWebswingUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RestUrlHandlerImpl extends AbstractUrlHandler implements Container, RestUrlHandler {
	private static final Logger log = LoggerFactory.getLogger(RestUrlHandlerImpl.class);

	private ApplicationHandler appHandler;

	public RestUrlHandlerImpl(UrlHandler parent, AbstractBinder binder, Class... resources) {
		super(parent);
		this.appHandler = new ApplicationHandler(new RestConfiguration(binder, resources));
	}

	@Override
	public boolean serve(HttpServletRequest request, HttpServletResponse response) throws WsException {
		final StringBuffer requestUrl = request.getRequestURL();
		final String requestURI = request.getRequestURI();
		final UriBuilder absoluteUriBuilder;
		try {
			absoluteUriBuilder = UriBuilder.fromUri(requestUrl.toString());
		} catch (final IllegalArgumentException iae) {
			log.warn("Rest request url [" + requestUrl.toString() + "] is invalid.", iae);
			return false;
		}

		final String basePath = getFullPathMapping() + "/";

		final URI baseUri;
		final URI requestUri;
		try {
			baseUri = absoluteUriBuilder.replacePath(basePath).build();
			String queryParameters = ContainerUtils.encodeUnsafeCharacters(request.getQueryString());
			if (queryParameters == null) {
				queryParameters = "";
			}

			requestUri = absoluteUriBuilder.replacePath(requestURI).replaceQuery(queryParameters).build();
		} catch (final UriBuilderException | IllegalArgumentException ex) {
			log.warn("Rest request url [" + requestURI.toString() + "] is invalid.", ex);
			return false;
		}

		try {
			final ResponseWriter responseWriter = new ResponseWriter(response);
			final ContainerRequest requestContext = new ContainerRequest(baseUri, requestUri, request.getMethod(), getSecurityContext(request), new MapPropertiesDelegate());
			requestContext.setEntityStream(request.getInputStream());
			addRequestHeaders(request, requestContext);
			requestContext.setWriter(responseWriter);
			appHandler.handle(requestContext);
			return responseWriter.isServed();
		} catch (final Exception e) {
			log.error("Rest API call failed.",e);
			throw new WsException(e.getMessage(), e);
		}
	}

	private SecurityContext getSecurityContext(HttpServletRequest request) {
		return new SecurityContext() {
			private final AbstractWebswingUser user = getUser();
			private final boolean secure = request.isSecure();

			@Override
			public Principal getUserPrincipal() {
				if (user != null) {
					return new Principal() {
						@Override
						public String getName() {
							return user.getUserId();
						}
					};
				} else {
					return null;
				}
			}

			@Override
			public boolean isUserInRole(String role) {
				return user == null ? false : user.hasRole(role);
			}

			@Override
			public boolean isSecure() {
				return secure;
			}

			@Override
			public String getAuthenticationScheme() {
				return SecurityContext.FORM_AUTH;
			}
		};
	}

	@Override
	protected String getPath() {
		return "";
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public ResourceConfig getConfiguration() {
		return appHandler.getConfiguration();
	}

	@Override
	public ApplicationHandler getApplicationHandler() {
		return appHandler;
	}

	@Override
	public void reload() {
		reload(getConfiguration());

	}

	@Override
	public void reload(ResourceConfig configuration) {
		appHandler.onShutdown(this);

		appHandler = new ApplicationHandler(configuration);
		appHandler.onReload(this);
		appHandler.onStartup(this);
	}

	private void addRequestHeaders(final HttpServletRequest request, final ContainerRequest requestContext) {
		final Enumeration<String> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			final String name = names.nextElement();

			final Enumeration<String> values = request.getHeaders(name);
			while (values.hasMoreElements()) {
				final String value = values.nextElement();
				if (value != null) { // filter out null values
					requestContext.header(name, value);
				}
			}
		}
	}

	private class ResponseWriter implements ContainerResponseWriter {
		private final HttpServletResponse response;
		private boolean served = false;

		public ResponseWriter(HttpServletResponse response) {
			this.response = response;
		}

		@Override
		public OutputStream writeResponseStatusAndHeaders(long contentLength, ContainerResponse responseContext) throws ContainerException {
			if (responseContext.getStatus() != 404) {
				served = true;
			}

			// first set the content length, so that if headers have an explicit value, it takes precedence over this one
			if (responseContext.hasEntity() && contentLength != -1 && contentLength < Integer.MAX_VALUE) {
				response.setContentLength((int) contentLength);
			}

			//write headers
			final MultivaluedMap<String, String> headers = responseContext.getStringHeaders();
			for (final Map.Entry<String, List<String>> e : headers.entrySet()) {
				final Iterator<String> it = e.getValue().iterator();
				if (!it.hasNext()) {
					continue;
				}
				final String header = e.getKey();
				if (response.containsHeader(header)) {
					// replace any headers previously set with values from Jersey container response.
					response.setHeader(header, it.next());
				}

				while (it.hasNext()) {
					response.addHeader(header, it.next());
				}
			}
			//set status
			response.setStatus(responseContext.getStatus());
			if (!responseContext.hasEntity()) {
				return null;
			} else {
				try {
					final OutputStream outputStream = response.getOutputStream();
					return outputStream;
				} catch (final IOException e) {
					throw new ContainerException(e);
				}
			}
		}

		@Override
		public boolean suspend(long timeOut, TimeUnit timeUnit, TimeoutHandler timeoutHandler) {
			throw new UnsupportedOperationException("Method suspend is not supported by the container.");
		}

		@Override
		public void setSuspendTimeout(long timeOut, TimeUnit timeUnit) throws IllegalStateException {
			throw new UnsupportedOperationException("Method setSuspendTimeout is not supported by the container.");
		}

		@Override
		public void commit() {

		}

		@Override
		public void failure(Throwable error) {
			try {
				if (!response.isCommitted()) {
					response.reset();
				}
			} finally {
				rethrow(error);
			}
		}

		@Override
		public boolean enableResponseBuffering() {
			return true;
		}

		private void rethrow(final Throwable error) {
			if (error instanceof RuntimeException) {
				throw (RuntimeException) error;
			} else {
				throw new ContainerException(error);
			}
		}

		public boolean isServed() {
			return served;
		}
	}

	@Provider
	public static class WsExceptionMapper implements ExceptionMapper<WsException> {
		@Override
		public Response toResponse(WsException ex) {
			return Response.status(ex.getReponseCode()).entity(ex.getMessage()).type("text/plain").build();
		}
	}

	private class RestConfiguration extends ResourceConfig {
		public RestConfiguration(AbstractBinder binder, Class[] resources) {
			// setup jersey's hk2 dependency injection
			register(new Feature() {
				@Override
				public boolean configure(FeatureContext context) {
					context.register(binder);
					return true;
				}
			});

			//register resources
			for (Class r : resources) {
				register(r);
			}

			//register providers
			register(WsExceptionMapper.class);
		}
	}

}
