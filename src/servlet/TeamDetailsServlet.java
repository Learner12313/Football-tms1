package servlet;

import javax.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import config.ApiConfig;

public class TeamDetailsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"Team ID required\"}");
            return;
        }
        
        String teamId = pathInfo.substring(1);
        String url = ApiConfig.BASE_URL + "/teams/" + teamId;
        
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("X-Auth-Token", ApiConfig.API_KEY);
        
        int code = conn.getResponseCode();
        String body;
        if (code == 200) {
            body = new String(conn.getInputStream().readAllBytes(), "UTF-8");
        } else {
            body = new String(conn.getErrorStream().readAllBytes(), "UTF-8");
            resp.setStatus(code);
        }
        
        resp.getWriter().write(body);
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setStatus(200);
    }
    
    @Override
    public void init() { System.out.println("TeamDetailsServlet loaded"); }
}