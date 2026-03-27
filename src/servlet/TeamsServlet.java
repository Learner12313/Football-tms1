package servlet;

import javax.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import config.ApiConfig;

public class TeamsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String league = req.getParameter("league");
        String competitionId = mapLeague(league);
        
        String url = ApiConfig.BASE_URL + "/competitions/" + competitionId + "/teams";
        
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
    
    private String mapLeague(String league) {
        if (league == null) return "2021";
        switch (league.toUpperCase()) {
            case "PL": return "2021";
            case "PD": return "2014";
            case "SA": return "2019";
            case "BL1": return "2002";
            case "FL1": return "2015";
            case "CL": return "2001";
            default: return "2021";
        }
    }
    
    @Override
    public void init() { System.out.println("TeamsServlet loaded"); }
}