package org.webswing.server.services.security.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.SecurityManagerService;
import org.webswing.server.services.security.WebswingTokenAdapter;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.otp.api.OneTimeToken;

public class LoginHandlerImpl extends AbstractUrlHandler implements LoginHandler {
	private static final String SUCCESS_URL = "webswingSuccessUrl";
	private static final Logger log = LoggerFactory.getLogger(LoginHandlerImpl.class);
	private final WebswingSecurityProvider securityProvider;

	public LoginHandlerImpl(UrlHandler parent, SecurityManagerService securityManger, WebswingSecurityProvider securityProvider) {
		super(parent);
		this.securityProvider = securityProvider;
	}

	@Override
	protected String getPath() {
		return "login";
	}

	@Override
	public boolean serve(HttpServletRequest req, HttpServletResponse res) throws WsException {
		try {
			login(req, res, null);
			return true;
		} catch (Exception e) {
			log.error("Failed to process login request. " + getFullPathMapping(), e);
			throw new WsException("Failed to login", e);
		}
	}

	protected void login(HttpServletRequest req, HttpServletResponse resp, WebswingAuthenticationException e) throws ServletException, IOException {
		boolean verify = req.getParameter("verify") != null;
		String otp = req.getParameter("otp");
		AbstractWebswingUser user = getUser();
		Subject subject = SecurityUtils.getSubject();
		if (verify) {
			if (user != null && user != AbstractWebswingUser.anonymUser) {
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setHeader("webswingUsername", user.getUserId());
			} else {
				resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} else if (otp != null) {
			try {
				OneTimeToken token = securityProvider.get().verifyOneTimePassword(otp);
				subject.login(new WebswingTokenAdapter(getSecuredPath(), token));
				user = getUser();
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setHeader("webswingUsername", user.getUserId());
			} catch (WebswingAuthenticationException e1) {
				log.warn("Authentication failed: " + e1.getMessage());
				resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} else {
			if (req.getParameter("successUrl") != null) {
				subject.getSession().setAttribute(SUCCESS_URL, req.getParameter("successUrl"));
			}
			WebswingCredentials credentials = securityProvider.get().getCredentials(req, resp, e);
			if (credentials != null) {
				try {
					AbstractWebswingUser resolvedUser = securityProvider.get().getUser(credentials);
					if (resolvedUser != null) {
						subject.login(new WebswingTokenAdapter(getSecuredPath(), resolvedUser, credentials));
						user = getUser();
						if (user != null && user != AbstractWebswingUser.anonymUser) {
							String successPage = null;
							if (subject.getSession().getAttribute(SUCCESS_URL) != null) {
								successPage = (String) subject.getSession().getAttribute(SUCCESS_URL);
							} else {
								String fullPath = getFullPathMapping();
								successPage = fullPath.substring(0, fullPath.length() - getPath().length());
							}
							resp.sendRedirect(successPage);
							return;
						}
					}
				} catch (WebswingAuthenticationException ex) {
					login(req, resp, ex);
				} catch (Exception ux) {
					log.error("Unexpected authentication error.", ux);
					login(req, resp, new WebswingAuthenticationException("Unexpected authentication error.", ux));
				}
			}
		}
	}

}
