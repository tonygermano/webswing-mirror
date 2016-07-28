package org.webswing.server.services.security.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.LoginTokenAdapter;
import org.webswing.server.services.security.SecurityManagerService;
import org.webswing.server.services.security.api.AbstractWebswingUser;

public class LoginHandlerImpl extends AbstractUrlHandler implements LoginHandler {
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
			login(req, res);
			return true;
		} catch (Exception e) {
			log.error("Failed to process login request. " + getFullPathMapping(), e);
			throw new WsException("Failed to login", e);
		}
	}

	protected void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AbstractWebswingUser user = getUser();
		Subject subject = SecurityUtils.getSubject();
		if (user != null && user != AbstractWebswingUser.anonymUser) {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setHeader("webswingUsername", user.getUserId());
		} else {
			try {
				AbstractWebswingUser resolvedUser = securityProvider.get().doLogin(req, resp);
				if (resolvedUser != null) {
					subject.login(new LoginTokenAdapter(getSecuredPath(), resolvedUser));
				}
			} catch (Exception ux) {
				log.error("Unexpected authentication error.", ux);
			}
		}
	}

}
