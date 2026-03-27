package servlet;

import javax.servlet.http.*;
import java.io.*;

public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json");
        resp.getWriter().write("{\"success\":true,\"message\":\"Logged out\"}");
    }
    
    @Override
    public void init() {
        System.out.println(" LogoutServlet loaded");
    }
}
