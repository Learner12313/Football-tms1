package servlet;

import javax.servlet.http.*;
import java.io.*;

public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String role = req.getParameter("role");
        
        PrintWriter out = resp.getWriter();
        
        // Validate credentials (simple plain text for demo)
        boolean valid = false;
        String userRole = "";
        
        if (username != null && password != null) {
            username = username.toLowerCase();
            
            if ("admin".equals(username) && "password123".equals(password)) {
                valid = true;
                userRole = "admin";
            } else if ("manager".equals(username) && "manager123".equals(password)) {
                valid = true;
                userRole = "manager";
            } else if ("viewer".equals(username) && "view123".equals(password)) {
                valid = true;
                userRole = "user";
            }
        }
        
        if (valid) {
            HttpSession session = req.getSession(true);
            session.setAttribute("username", username);
            session.setAttribute("role", userRole);
            session.setMaxInactiveInterval(3600); // 1 hour
            out.write("{\"success\":true,\"message\":\"Login successful\",\"role\":\"" + userRole + "\"}");
        } else {
            resp.setStatus(401);
            out.write("{\"success\":false,\"error\":\"Invalid credentials\"}");
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(200);
    }
    
    @Override
    public void init() {
        System.out.println(" LoginServlet loaded");
    }
}