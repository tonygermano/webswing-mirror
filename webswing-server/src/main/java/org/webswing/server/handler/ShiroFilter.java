package org.webswing.server.handler;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

public class ShiroFilter extends AuthenticationFilter {

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
		if ("OPTIONS".equals(httpRequest.getMethod())) {
			return true;
		}
		return super.isAccessAllowed(request, response, mappedValue);
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		if (isLoginRequest(request, response)) {
			return true;
		} else {
			HttpServletResponse httpResponse = WebUtils.toHttp(response);
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
			return false;
		}
	}

}
