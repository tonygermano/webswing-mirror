package org.webswing.server.handler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.server.ConfigurationManager;
import org.webswing.server.util.ServerUtil;

public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = -425026725411077089L;
    public static final String anonymUserName = "anonym";
    public static final String anonymSecretPassword = UUID.randomUUID().toString();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Subject currentUser = SecurityUtils.getSubject();
        try {
            AuthenticationToken token = new UsernamePasswordToken(req.getParameter("username"), req.getParameter("password"));
            if (accessingAnonymousSwingApp(req)) {
                //TODO: anonym user is dynamicly created in WebSwingPropertiesRealm constructor. If this is replaced it wont work. Find better solution!
                token = new UsernamePasswordToken(anonymUserName, anonymSecretPassword);
            }
            currentUser.login(token);
            authorizeAccess(req, resp);
            //WebUtils.redirectToSavedRequest(req, resp, "index.xhtml");
        } catch (AuthenticationException e) {
            if (currentUser.isAuthenticated()) {
                authorizeAccess(req, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Authentication failed. ");
                if (e instanceof IncorrectCredentialsException || e instanceof UnknownAccountException) {
                    resp.getWriter().write("Provided credentials are incorrect.");
                }
            }
        }
    }

    private void authorizeAccess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String role = req.getParameter("role");
        if (checkRole(role, SecurityUtils.getSubject())) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("User is not authorized to access this location.");
        }
    }

    private boolean checkRole(String role, Subject currentUser) {
        if (role == null) {
            return true;
        } else {
            boolean inRole = currentUser.hasRole(role);
            if (inRole) {
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean accessingAnonymousSwingApp(HttpServletRequest req) {
        if ("swing".equals(req.getParameter("mode"))) {
            String app = ServerUtil.getPreSelectedApplication(req, false);
            Map<String, SwingApplicationDescriptor> applicationsMap = ConfigurationManager.getInstance().getApplications();
            if (app != null) {
                SwingApplicationDescriptor swingApplicationDescriptor = applicationsMap.get(app);
                if (swingApplicationDescriptor != null) {
                    return !swingApplicationDescriptor.isAuthentication() && !swingApplicationDescriptor.isAuthorization();
                }
            } else {
                for (SwingApplicationDescriptor desc : applicationsMap.values()) {
                    if (desc.isAuthentication() || desc.isAuthorization()) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
