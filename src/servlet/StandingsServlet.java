package servlet;

import javax.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import config.ApiConfig;

public class StandingsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String league = req.getParameter("league");
        String competitionId = mapLeagueToCompetition(league);
        
        String apiUrl = ApiConfig.BASE_URL + "/competitions/" + competitionId + "/standings";
        
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
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
    
    private String mapLeagueToCompetition(String league) {
        if (league == null) return "2021";
        switch (league.toUpperCase()) {
            case "PL": return "2021";
            case "PD": return "2014";
            case "SA": return "2019";
            case "BL1": return "2002";
            case "FL1": return "2015";
            case "CL": return "2001";
            case "ELC": return "2016";
            case "DED": return "2003";
            case "PPL": return "2017";
            case "BSA": return "2013";
            case "EC": return "2018";
            case "WC": return "2000";
            default: return "2021";
        }
    }
    
    @Override
    public void init() { System.out.println(" StandingsServlet loaded"); }
}
