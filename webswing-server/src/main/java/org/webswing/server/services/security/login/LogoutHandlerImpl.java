package org.webswing.server.services.security.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.webswing.server.base.AbstractUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.WebswingTokenAdapter;
import org.webswing.server.services.security.api.WebswingUser;

public class LogoutHandlerImpl extends AbstractUrlHandler implements LogoutHandler {

	public LogoutHandlerImpl(UrlHandler parent) {
		super(parent);
	}

	@Override
	protected String getPath() {
		return "logout";
	}

	@Override
	public boolean serve(HttpServletRequest req, HttpServletResponse res) throws WsException {
		try {
			logout(req, res);
			return true;
		} catch (Exception e) {
			throw new WsException("Failed to logout", e);
		}
	}

	protected void logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		WebswingUser user = getUser();
		if (user != null) {
			Subject subject = SecurityUtils.getSubject();
			subject.login(new WebswingTokenAdapter(getSecuredPath(), user, null));
		}

	}
}
