package org.webswing.server.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApplicationSelectorServlet extends HttpServlet {

    private static final long serialVersionUID = -4359050705016810024L;
    public static final String SELECTED_APPLICATION = "selectedApplication";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String appName = req.getPathInfo();
        if (appName != null && appName.length() > 0) {
            appName = appName.startsWith("/") ? appName.substring(1) : appName;
            req.getSession().setAttribute(SELECTED_APPLICATION, appName);
        }
        resp.sendRedirect("/");
    }
}
