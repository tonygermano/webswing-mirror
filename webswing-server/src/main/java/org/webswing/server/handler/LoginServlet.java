package org.webswing.server.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;


public class LoginServlet  extends HttpServlet{

    private static final long serialVersionUID = -425026725411077089L;

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isAuthenticated()){
            String role=req.getParameter("role");
            if(role==null){
                resp.setStatus(HttpServletResponse.SC_OK);
            }else{
                boolean inRole=currentUser.hasRole(role);
                if(inRole){
                    resp.setStatus(HttpServletResponse.SC_OK);
                }else{
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        }else{
            try {
                AuthenticationToken token =  new UsernamePasswordToken(req.getParameter("username"), req.getParameter("password"));
                currentUser.login(token);
                resp.setStatus(HttpServletResponse.SC_OK);
                //WebUtils.redirectToSavedRequest(req, resp, "index.xhtml");
            } catch (AuthenticationException e) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }
}
