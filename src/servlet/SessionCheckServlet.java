package servlet;

import javax.servlet.http.*;
import java.io.*;

public class SessionCheckServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        HttpSession session = req.getSession(false);
        PrintWriter out = resp.getWriter();
        
        if (session != null && session.getAttribute("username") != null) {
            out.write("{\"loggedIn\":true,\"username\":\"" + session.getAttribute("username") + "\",\"role\":\"" + session.getAttribute("role") + "\"}");
        } else {
            out.write("{\"loggedIn\":false}");
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setStatus(200);
    }
    
    @Override
    public void init() { System.out.println(" SessionCheckServlet loaded"); }
}