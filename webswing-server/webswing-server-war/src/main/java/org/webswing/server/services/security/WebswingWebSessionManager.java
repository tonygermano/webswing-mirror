package org.webswing.server.services.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.servlet.Cookie.SameSiteOptions;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.webswing.Constants;
import org.webswing.server.util.SecurityUtil;
import javax.servlet.http.HttpServletRequest;

public class WebswingWebSessionManager extends DefaultWebSessionManager {

	private static final String WEBSWING_SESSION_ID = "WebswingSessionId";

	public WebswingWebSessionManager() {
		String proxypath = System.getProperty(Constants.REVERSE_PROXY_CONTEXT_PATH, "").replaceAll("[^A-Za-z0-9]", "_");
		Cookie cookie = new SimpleCookie(WEBSWING_SESSION_ID + proxypath);
		cookie.setHttpOnly(true); //more secure, protects against XSS attacks
		
		boolean serverIsHttpsOnly = Boolean.getBoolean(Constants.HTTPS_ONLY);
		cookie.setSecure(serverIsHttpsOnly);
		if (serverIsHttpsOnly) {
			cookie.setSameSite(SameSiteOptions.valueOf(System.getProperty(Constants.COOKIE_SAMESITE, "NONE").toUpperCase()));
		}
		
		setSessionIdCookie(cookie);
	}

	@Override
	protected void validate(Session session, SessionKey key) throws InvalidSessionException {
		super.validate(session, key);
		if(Boolean.getBoolean(Constants.LINK_COOKIE_TO_IP) && key instanceof WebSessionKey){
			WebSessionKey webkey= (WebSessionKey) key;
			if(webkey.getServletRequest() instanceof HttpServletRequest){
				String currentIp = SecurityUtil.getClientIp((HttpServletRequest) webkey.getServletRequest());
				String sessionIp = session.getHost();
				if(!StringUtils.equals(currentIp,sessionIp)){
					InvalidSessionException ise = new InvalidSessionException("IP address does not match Session host.");
					onInvalidation(session, ise, key);
					throw ise;
				}
			}

		}
	}
}
