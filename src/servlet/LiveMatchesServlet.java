package servlet;

import javax.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import config.ApiConfig;

public class LiveMatchesServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        try {
            String league = req.getParameter("league");
            String status = req.getParameter("status");
            String competitionId = mapLeagueToCompetition(league);
            
            // Build API URL
            StringBuilder apiUrl = new StringBuilder(
                ApiConfig.BASE_URL + "/competitions/" + competitionId + "/matches"
            );
            
            StringBuilder params = new StringBuilder();
            
            if (status != null && !status.trim().isEmpty()) {
                params.append("status=").append(status.toUpperCase());
            }
            
            if (params.length() > 0) {
                apiUrl.append("?").append(params);
            }
            
            System.out.println(" Calling API: " + apiUrl.toString());
            
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl.toString()).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Auth-Token", ApiConfig.API_KEY);
            conn.setRequestProperty("Accept", "application/json");
            
            int responseCode = conn.getResponseCode();
            System.out.println(" HTTP Code: " + responseCode);
            
            String responseBody;
            if (responseCode == 200) {
                responseBody = new String(conn.getInputStream().readAllBytes(), "UTF-8");
                System.out.println(" Success! " + responseBody.length() + " chars");
            } else {
                responseBody = new String(conn.getErrorStream().readAllBytes(), "UTF-8");
                System.err.println(" API Error: " + responseBody);
                resp.setStatus(responseCode);
            }
            
            resp.getWriter().write(responseBody);
            
        } catch (Exception e) {
            System.err.println(" Exception: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"Server error: " + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(200);
    }
    
    // Map league codes to competition IDs - ALL 12 LEAGUES
    private static String mapLeagueToCompetition(String league) {
        if (league == null) return "2021";
        
        switch (league.toUpperCase()) {
            case "PL": return "2021";   // Premier League
            case "PD": return "2014";   // La Liga
            case "SA": return "2019";   // Serie A
            case "BL1": return "2002";  // Bundesliga
            case "FL1": return "2015";  // Ligue 1
            case "CL": return "2001";   // Champions League
            case "ELC": return "2016";  // Championship
            case "DED": return "2003";  // Eredivisie
            case "PPL": return "2017";  // Primeira Liga
            case "BSA": return "2013";  // Brasileirão
            case "EC": return "2018";   // Euro Championship
            case "WC": return "2000";   // World Cup
            default: return "2021";     // Default to Premier League
        }
    }
    
    @Override
    public void init() {
        System.out.println(" LiveMatchesServlet loaded");
    }
}
