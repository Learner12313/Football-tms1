package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:tournament.db";
    
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found!");
        }
    }
    
    // Initialize database tables
    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Tournaments table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tournaments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    teams_count INTEGER DEFAULT 0,
                    matches_count INTEGER DEFAULT 0,
                    format TEXT DEFAULT 'round-robin',
                    created_by TEXT NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);
            
            // Tournament teams table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tournament_teams (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    tournament_id INTEGER NOT NULL,
                    team_name TEXT NOT NULL,
                    group_name TEXT DEFAULT 'A',
                    FOREIGN KEY (tournament_id) REFERENCES tournaments(id)
                )
            """);
            
            // Tournament fixtures table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tournament_fixtures (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    tournament_id INTEGER NOT NULL,
                    round_number INTEGER DEFAULT 1,
                    home_team TEXT NOT NULL,
                    away_team TEXT NOT NULL,
                    home_score INTEGER DEFAULT NULL,
                    away_score INTEGER DEFAULT NULL,
                    match_date TEXT,
                    venue TEXT,
                    status TEXT DEFAULT 'scheduled',
                    FOREIGN KEY (tournament_id) REFERENCES tournaments(id)
                )
            """);
            
            System.out.println("✅ Database initialized successfully");
            
        } catch (SQLException e) {
            System.err.println("❌ Database init error: " + e.getMessage());
        }
    }
    
    // Get database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    // Create a new tournament
    public static int createTournament(String name, int teamsCount, String format, String createdBy) {
        String sql = "INSERT INTO tournaments (name, teams_count, format, created_by) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, teamsCount);
            pstmt.setString(3, format);
            pstmt.setString(4, createdBy);
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating tournament: " + e.getMessage());
        }
        return -1;
    }
    
    // Add team to tournament
    public static boolean addTeam(int tournamentId, String teamName, String groupName) {
        String sql = "INSERT INTO tournament_teams (tournament_id, team_name, group_name) VALUES (?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tournamentId);
            pstmt.setString(2, teamName);
            pstmt.setString(3, groupName);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding team: " + e.getMessage());
        }
        return false;
    }
    
    // Add fixture
    public static boolean addFixture(int tournamentId, int roundNum, String homeTeam, String awayTeam, String venue) {
        String sql = "INSERT INTO tournament_fixtures (tournament_id, round_number, home_team, away_team, venue) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tournamentId);
            pstmt.setInt(2, roundNum);
            pstmt.setString(3, homeTeam);
            pstmt.setString(4, awayTeam);
            pstmt.setString(5, venue);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding fixture: " + e.getMessage());
        }
        return false;
    }
    
    // Get all tournaments
    public static List<Map<String, Object>> getAllTournaments() {
        List<Map<String, Object>> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournaments ORDER BY created_at DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> tournament = new HashMap<>();
                tournament.put("id", rs.getInt("id"));
                tournament.put("name", rs.getString("name"));
                tournament.put("teams_count", rs.getInt("teams_count"));
                tournament.put("matches_count", rs.getInt("matches_count"));
                tournament.put("format", rs.getString("format"));
                tournament.put("created_by", rs.getString("created_by"));
                tournament.put("created_at", rs.getString("created_at"));
                tournaments.add(tournament);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting tournaments: " + e.getMessage());
        }
        return tournaments;
    }
    
    // Get tournament by ID
    public static Map<String, Object> getTournament(int tournamentId) {
        String sql = "SELECT * FROM tournaments WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tournamentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> tournament = new HashMap<>();
                tournament.put("id", rs.getInt("id"));
                tournament.put("name", rs.getString("name"));
                tournament.put("teams_count", rs.getInt("teams_count"));
                tournament.put("matches_count", rs.getInt("matches_count"));
                tournament.put("format", rs.getString("format"));
                tournament.put("created_by", rs.getString("created_by"));
                tournament.put("created_at", rs.getString("created_at"));
                return tournament;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting tournament: " + e.getMessage());
        }
        return null;
    }
    
    // Get teams in tournament
    public static List<Map<String, Object>> getTournamentTeams(int tournamentId) {
        List<Map<String, Object>> teams = new ArrayList<>();
        String sql = "SELECT * FROM tournament_teams WHERE tournament_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tournamentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> team = new HashMap<>();
                team.put("id", rs.getInt("id"));
                team.put("team_name", rs.getString("team_name"));
                team.put("group_name", rs.getString("group_name"));
                teams.add(team);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting teams: " + e.getMessage());
        }
        return teams;
    }
    
    // Get fixtures for tournament
    public static List<Map<String, Object>> getTournamentFixtures(int tournamentId) {
        List<Map<String, Object>> fixtures = new ArrayList<>();
        String sql = "SELECT * FROM tournament_fixtures WHERE tournament_id = ? ORDER BY round_number";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tournamentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> fixture = new HashMap<>();
                fixture.put("id", rs.getInt("id"));
                fixture.put("round_number", rs.getInt("round_number"));
                fixture.put("home_team", rs.getString("home_team"));
                fixture.put("away_team", rs.getString("away_team"));
                fixture.put("home_score", rs.getObject("home_score"));
                fixture.put("away_score", rs.getObject("away_score"));
                fixture.put("venue", rs.getString("venue"));
                fixture.put("status", rs.getString("status"));
                fixtures.add(fixture);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting fixtures: " + e.getMessage());
        }
        return fixtures;
    }
    
    // Delete tournament
    public static boolean deleteTournament(int tournamentId) {
        try (Connection conn = getConnection()) {
            // Delete fixtures first
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tournament_fixtures WHERE tournament_id = ?")) {
                pstmt.setInt(1, tournamentId);
                pstmt.executeUpdate();
            }
            
            // Delete teams
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tournament_teams WHERE tournament_id = ?")) {
                pstmt.setInt(1, tournamentId);
                pstmt.executeUpdate();
            }
            
            // Delete tournament
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tournaments WHERE id = ?")) {
                pstmt.setInt(1, tournamentId);
                return pstmt.executeUpdate() > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting tournament: " + e.getMessage());
        }
        return false;
    }
    
    // Update match score
    public static boolean updateMatchScore(int fixtureId, Integer homeScore, Integer awayScore) {
        String sql = "UPDATE tournament_fixtures SET home_score = ?, away_score = ?, status = 'finished' WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, homeScore);
            pstmt.setObject(2, awayScore);
            pstmt.setInt(3, fixtureId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating score: " + e.getMessage());
        }
        return false;
    }
}
