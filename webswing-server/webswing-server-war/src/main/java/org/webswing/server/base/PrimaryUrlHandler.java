package org.webswing.server.base;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.SwingConfig;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.common.util.ConfigUtil;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.security.api.SecurityContext;
import org.webswing.server.services.security.login.WebswingSecurityProvider;
import org.webswing.server.util.SecurityUtil;
import org.webswing.server.util.ServerUtil;

public abstract class PrimaryUrlHandler extends AbstractUrlHandler implements SecurityContext, WebswingSecurityProvider {
	private static final Logger log = LoggerFactory.getLogger(PrimaryUrlHandler.class);

	private SecuredPathConfig config;

	public PrimaryUrlHandler(UrlHandler parent, ConfigurationService configService) {
		super(parent);
		config = configService.getConfiguration().get(getPathMapping());
	}

	@Override
	public boolean serve(HttpServletRequest req, HttpServletResponse res) throws WsException {
		//redirect to url that ends with '/' to ensure browser queries correct resources 
		if (req.getPathInfo() == null || (req.getContextPath() + req.getPathInfo()).equals(getFullPathMapping())) {
			try {
				String queryString = req.getQueryString() == null ? "" : ("?" + req.getQueryString());
				res.sendRedirect(getFullPathMapping() + "/" + queryString);
			} catch (IOException e) {
				log.error("Failed to redirect.", e);
			}
			return true;
		} else {
			return super.serve(req, res);
		}
	}

	public SecuredPathConfig getConfig() {
		if (config == null) {
			config = ConfigUtil.instantiateConfig(null, SecuredPathConfig.class);
		}
		return config;
	}

	public SwingConfig getSwingConfig() {
		if (getConfig().getSwingConfig() == null) {
			return ConfigUtil.instantiateConfig(null, SwingConfig.class);
		} else {
			return getConfig().getSwingConfig();
		}
	}

	@Override
	public File resolveFile(String name) {
		return CommonUtil.resolveFile(name, getSwingConfig().getHomeDir(), null);
	}

	@Override
	public URL getWebResource(String resource) {
		File webFolder = resolveFile(getConfig().getWebFolder());
		return ServerUtil.getWebResource(toPath(resource), getServletContext(), webFolder);
	}

	@Override
	public String replaceVariables(String string) {
		return CommonUtil.getConfigSubstitutor().replace(string);
	}

	@Override
	public Object getFromSecuritySession(String attributeName) {
		return SecurityUtil.getFromSecuritySession(attributeName);
	}

	@Override
	public void setToSecuritySession(String attributeName, Object value) {
		SecurityUtil.setToSecuritySession(attributeName, value);
	}

}
