package org.webswing.server.services.security.modules;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.api.WebswingSecurityModuleConfig;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheResolver;

public abstract class AbstractSecurityModule<T extends WebswingSecurityModuleConfig> implements WebswingSecurityModule {
	public static final String REDIRECT_URL = "redirectUrl";
	public static final String SUCCESS_URL = "successUrl";
	private static final Logger log = LoggerFactory.getLogger(AbstractSecurityModule.class);
	private static final String LOGIN_REQUEST_MSG = "LoginRequestMsg";
	private static final ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
	}

	private final T config;
	private final DefaultMustacheFactory mf;
	private final Map<String, Mustache> compiledTemplates = new HashMap<>();

	public AbstractSecurityModule(T config) {
		this.config = config;
		mf = new DefaultMustacheFactory(new MustacheResolver() {

			@Override
			public Reader getReader(String resourceName) {
				URL url = findTemplate(resourceName);
				if (url != null) {
					try {
						InputStream is = url.openStream();
						return new InputStreamReader(is);
					} catch (IOException e) {
						log.error("Failed to open Template from url:" + url);
					}
				}
				return null;
			}
		});
	}

	public T getConfig() {
		return config;
	}

	@Override
	public void init() {
	}

	@Override
	public void destroy() {
		compiledTemplates.clear();
	}

	@Override
	public AbstractWebswingUser doLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Map<String, Object> msg = getLoginRequest(request);
			if (msg != null) {
				config.getContext().setToSecuritySession(LOGIN_REQUEST_MSG, msg);
			}
			boolean preValid = preVerify(request, response);
			if (preValid) {
				AbstractWebswingUser user = authenticate(request);
				if (user != null) {
					boolean postValid = postVerify(user, request, response);
					if (postValid) {
						onAuthenticationSuccess(request, response);
						return decorateUser(user, request, response);
					}
				}
				onAuthenticationFailed(request, response, null);
			}
		} catch (WebswingAuthenticationException e) {
			onAuthenticationFailed(request, response, e);
		}
		return null;
	}

	protected AbstractWebswingUser decorateUser(AbstractWebswingUser user, HttpServletRequest request, HttpServletResponse response) {
		return user;
	}

	protected boolean postVerify(AbstractWebswingUser user, HttpServletRequest request, HttpServletResponse response) throws WebswingAuthenticationException {
		return true;
	}

	protected boolean preVerify(HttpServletRequest request, HttpServletResponse response) throws WebswingAuthenticationException {
		return true;
	}

	protected abstract AbstractWebswingUser authenticate(HttpServletRequest request) throws WebswingAuthenticationException;

	protected void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		if (!isAjax(request)) {
			Map<String, Object> msg = getLoginRequest(request);
			if (msg != null && msg.containsKey(SUCCESS_URL)) {
				sendRedirect(request, response, (String) msg.get(SUCCESS_URL));
			} else {
				sendRedirect(request, response, config.getContext().getSecuredPath());
			}
		}
	}

	protected void onAuthenticationFailed(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		if (isAjax(request)) {
			serveLoginPartial(request, response, exception);
		} else {
			serveLoginPage(request, response, exception);
		}
	}

	protected abstract void serveLoginPage(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException;

	protected abstract void serveLoginPartial(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException exception) throws IOException;

	protected boolean isAjax(HttpServletRequest request) {
		String requestedWithHeader = request.getHeader("X-Requested-With");
		if (requestedWithHeader != null && requestedWithHeader.equals("XMLHttpRequest")) {
			return true;
		}
		return false;
	}

	protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		if (isAjax(request)) {
			Map<String, Object> message = new HashMap<>();
			message.put(REDIRECT_URL, url);
			try {
				mapper.writeValue(response.getOutputStream(), message);
			} catch (Exception e) {
				throw new IOException("Failed to send login redirect message", e);
			}
		} else {
			response.sendRedirect(url);
		}
	}

	protected void sendHtml(HttpServletRequest request, HttpServletResponse response, String template, Object variables) throws IOException {
		if (isAjax(request)) {
			Map<String, Object> message = new HashMap<>();
			try {
				Writer w = new StringWriter();
				processTemplate(w, template, variables);
				message.put("partialHtml", w.toString());
				mapper.writeValue(response.getOutputStream(), message);
			} catch (Exception e) {
				throw new IOException("Failed to send login template message", e);
			}
		} else {
			Writer w = new OutputStreamWriter(response.getOutputStream());
			processTemplate(w, template, variables);
		}
	}

	protected void processTemplate(Writer w, String template, Object variables) throws IOException {
		try {
			if (template != null && w != null) {
				Mustache mustache = compileTemplate(template);
				mustache.execute(w, variables);
				w.flush();
			}
		} finally {
			if (w != null) {
				w.close();
			}
		}
	}

	protected Mustache compileTemplate(String name) throws IOException {
		Mustache mustache = compiledTemplates.get(name);
		if (mustache == null) {
			mustache = mf.compile(name);
			compiledTemplates.put(name, mustache);
		}
		return mustache;
	}

	public URL findTemplate(String name) {
		URL url = getConfig().getContext().getWebResource(name);
		if (url == null) {
			url = getClass().getClassLoader().getResource(name);
		}
		return url;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getLoginRequest(HttpServletRequest request) {
		if (isAjax(request) && request.getContentType().equals("application/json")) {
			try {
				return mapper.readValue(request.getReader(), Map.class);
			} catch (Exception e) {
				log.debug("Failed to read login request data.", e);
			}
		}
		return (Map<String, Object>) getConfig().getContext().getFromSecuritySession(LOGIN_REQUEST_MSG);
	}

	public static ObjectMapper getMapper() {
		return mapper;
	}

}
