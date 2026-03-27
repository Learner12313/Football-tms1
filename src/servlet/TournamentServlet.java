package servlet;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class TournamentServlet extends HttpServlet {
    
    private static final List<Map<String, Object>> tournaments = new ArrayList<>();
    private static int nextId = 1;
    
    static {
        Map<String, Object> sample = new HashMap<>();
        sample.put("id", nextId++);
        sample.put("name", "Summer Cup 2024");
        sample.put("teams_count", 8);
        sample.put("format", "round-robin");
        sample.put("created_by", "admin");
        sample.put("created_at", "2024-01-15");
        tournaments.add(sample);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String pathInfo = req.getPathInfo();
        PrintWriter out = resp.getWriter();
        
        System.out.println("TournamentServlet GET: " + pathInfo);
        
        // GET /tournament or /tournament/ - list all
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            out.write(listAllJson());
            return;
        }
        
        // GET /tournament/{id} - view one
        String idStr = pathInfo.substring(1); // remove leading /
        try {
            int id = Integer.parseInt(idStr);
            Map<String, Object> t = findById(id);
            if (t == null) {
                resp.setStatus(404);
                out.write("{\"error\":\"Tournament not found\"}");
            } else {
                out.write(toDetailJson(t));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            out.write("{\"error\":\"Invalid ID\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        PrintWriter out = resp.getWriter();
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.setStatus(401);
            out.write("{\"success\":false,\"error\":\"Not logged in\"}");
            return;
        }
        
        // Read body
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String body = sb.toString();
        
        String name = extract(body, "name");
        String teamsCountStr = extract(body, "teamsCount");
        String format = extract(body, "format");
        
        if (name == null || name.isEmpty()) name = "Tournament " + nextId;
        int teamsCount = 4;
        try {
            teamsCount = teamsCountStr != null && !teamsCountStr.isEmpty() ? Integer.parseInt(teamsCountStr) : 4;
        } catch (Exception e) {}
        if (format == null || format.isEmpty()) format = "round-robin";
        
        Map<String, Object> t = new HashMap<>();
        t.put("id", nextId++);
        t.put("name", name);
        t.put("teams_count", teamsCount);
        t.put("format", format);
        t.put("created_by", session.getAttribute("username"));
        t.put("created_at", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        
        tournaments.add(t);
        
        System.out.println("Created tournament: " + name);
        out.write("{\"success\":true,\"tournamentId\":" + t.get("id") + "}");
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.length() <= 1) {
            resp.setStatus(400);
            out.write("{\"success\":false,\"error\":\"ID required\"}");
            return;
        }
        
        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean removed = tournaments.removeIf(t -> (int)t.get("id") == id);
            if (removed) {
                out.write("{\"success\":true}");
            } else {
                resp.setStatus(404);
                out.write("{\"success\":false,\"error\":\"Not found\"}");
            }
        } catch (Exception e) {
            resp.setStatus(400);
            out.write("{\"success\":false,\"error\":\"Invalid ID\"}");
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(200);
    }
    
    // ===== HELPERS =====
    
    private Map<String, Object> findById(int id) {
        for (Map<String, Object> t : tournaments) {
            if ((int)t.get("id") == id) return t;
        }
        return null;
    }
    
    private String listAllJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < tournaments.size(); i++) {
            if (i > 0) sb.append(",");
            Map<String, Object> t = tournaments.get(i);
            sb.append("{");
            sb.append("\"id\":").append(t.get("id")).append(",");
            sb.append("\"name\":\"").append(escape(t.get("name").toString())).append("\",");
            sb.append("\"teams_count\":").append(t.get("teams_count")).append(",");
            sb.append("\"format\":\"").append(t.get("format")).append("\",");
            sb.append("\"created_by\":\"").append(t.get("created_by")).append("\",");
            sb.append("\"created_at\":\"").append(t.get("created_at")).append("\"");
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }
    
    private String toDetailJson(Map<String, Object> t) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(t.get("id")).append(",");
        sb.append("\"name\":\"").append(escape(t.get("name").toString())).append("\",");
        sb.append("\"teams_count\":").append(t.get("teams_count")).append(",");
        sb.append("\"format\":\"").append(t.get("format")).append("\",");
        sb.append("\"created_by\":\"").append(t.get("created_by")).append("\",");
        sb.append("\"created_at\":\"").append(t.get("created_at")).append("\",");
        sb.append("\"fixtures\":[");
        
        // Generate round-robin fixtures
        int teams = (int) t.get("teams_count");
        int fixtureId = 1;
        for (int i = 1; i <= teams; i++) {
            for (int j = i + 1; j <= teams; j++) {
                if (fixtureId > 1) sb.append(",");
                sb.append("{");
                sb.append("\"id\":").append(fixtureId).append(",");
                sb.append("\"round_number\":").append(fixtureId).append(",");
                sb.append("\"home_team\":\"Team ").append(i).append("\",");
                sb.append("\"away_team\":\"Team ").append(j).append("\",");
                sb.append("\"home_score\":null,");
                sb.append("\"away_score\":null,");
                sb.append("\"venue\":\"TBD\",");
                sb.append("\"status\":\"scheduled\"");
                sb.append("}");
                fixtureId++;
            }
        }
        
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }
    
    private String extract(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        
        start = json.indexOf(":", start) + 1;
        while (start < json.length() && json.charAt(start) == ' ') start++;
        
        if (start >= json.length()) return "";
        
        if (json.charAt(start) == '"') {
            int end = json.indexOf("\"", start + 1);
            return end > start ? json.substring(start + 1, end) : "";
        } else {
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}' && json.charAt(end) != '\n') {
                end++;
            }
            return json.substring(start, end).trim();
        }
    }
    
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    @Override
    public void init() {
        System.out.println("TournamentServlet loaded");
    }
}