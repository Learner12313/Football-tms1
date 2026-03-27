package servlet;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class FormationsServlet extends HttpServlet {
    
    private static final String FORMATIONS_JSON;
    
    static {
        FORMATIONS_JSON = """
        {
            "formations": [
                {
                    "id": "4-3-3",
                    "name": "4-3-3",
                    "description": "Attacking formation with 3 forwards, balanced midfield. Popular with teams that have strong wingers.",
                    "positions": [
                        {"code": "GK", "name": "Goalkeeper", "x": 50, "y": 90},
                        {"code": "LB", "name": "Left Back", "x": 15, "y": 70},
                        {"code": "CB", "name": "Center Back", "x": 35, "y": 75},
                        {"code": "CB", "name": "Center Back", "x": 65, "y": 75},
                        {"code": "RB", "name": "Right Back", "x": 85, "y": 70},
                        {"code": "CM", "name": "Center Midfield", "x": 30, "y": 50},
                        {"code": "CDM", "name": "Defensive Mid", "x": 50, "y": 55},
                        {"code": "CM", "name": "Center Midfield", "x": 70, "y": 50},
                        {"code": "LW", "name": "Left Wing", "x": 20, "y": 25},
                        {"code": "ST", "name": "Striker", "x": 50, "y": 15},
                        {"code": "RW", "name": "Right Wing", "x": 80, "y": 25}
                    ],
                    "tactics": [
                        "Wingers provide width and deliver crosses",
                        "Central midfielder supports both attack and defense",
                        "CDM protects the back four and breaks up play"
                    ]
                },
                {
                    "id": "4-4-2",
                    "name": "4-4-2",
                    "description": "Classic balanced formation with two strikers. Traditional English formation.",
                    "positions": [
                        {"code": "GK", "name": "Goalkeeper", "x": 50, "y": 90},
                        {"code": "LB", "name": "Left Back", "x": 15, "y": 70},
                        {"code": "CB", "name": "Center Back", "x": 35, "y": 75},
                        {"code": "CB", "name": "Center Back", "x": 65, "y": 75},
                        {"code": "RB", "name": "Right Back", "x": 85, "y": 70},
                        {"code": "LM", "name": "Left Midfield", "x": 15, "y": 45},
                        {"code": "CM", "name": "Center Midfield", "x": 35, "y": 50},
                        {"code": "CM", "name": "Center Midfield", "x": 65, "y": 50},
                        {"code": "RM", "name": "Right Midfield", "x": 85, "y": 45},
                        {"code": "ST", "name": "Striker", "x": 35, "y": 18},
                        {"code": "ST", "name": "Striker", "x": 65, "y": 18}
                    ],
                    "tactics": [
                        "Two strikers create partnership and occupy defenders",
                        "Flat midfield four provides width and coverage",
                        "Solid defensive structure with two banks of four"
                    ]
                },
                {
                    "id": "4-2-3-1",
                    "name": "4-2-3-1",
                    "description": "Modern formation with defensive midfield shield. Very popular in modern football.",
                    "positions": [
                        {"code": "GK", "name": "Goalkeeper", "x": 50, "y": 90},
                        {"code": "LB", "name": "Left Back", "x": 15, "y": 70},
                        {"code": "CB", "name": "Center Back", "x": 35, "y": 75},
                        {"code": "CB", "name": "Center Back", "x": 65, "y": 75},
                        {"code": "RB", "name": "Right Back", "x": 85, "y": 70},
                        {"code": "CDM", "name": "Defensive Mid", "x": 35, "y": 55},
                        {"code": "CDM", "name": "Defensive Mid", "x": 65, "y": 55},
                        {"code": "LW", "name": "Left Wing", "x": 20, "y": 35},
                        {"code": "CAM", "name": "Attacking Mid", "x": 50, "y": 32},
                        {"code": "RW", "name": "Right Wing", "x": 80, "y": 35},
                        {"code": "ST", "name": "Striker", "x": 50, "y": 15}
                    ],
                    "tactics": [
                        "Double pivot provides defensive stability",
                        "CAM is the creative hub linking midfield to attack",
                        "Lone striker needs support from wingers and CAM"
                    ]
                },
                {
                    "id": "3-5-2",
                    "name": "3-5-2",
                    "description": "Attacking formation with wingbacks. Good for possession-based teams.",
                    "positions": [
                        {"code": "GK", "name": "Goalkeeper", "x": 50, "y": 90},
                        {"code": "CB", "name": "Center Back", "x": 25, "y": 75},
                        {"code": "CB", "name": "Center Back", "x": 50, "y": 78},
                        {"code": "CB", "name": "Center Back", "x": 75, "y": 75},
                        {"code": "LWB", "name": "Left Wingback", "x": 10, "y": 50},
                        {"code": "CM", "name": "Center Midfield", "x": 30, "y": 50},
                        {"code": "CDM", "name": "Defensive Mid", "x": 50, "y": 55},
                        {"code": "CM", "name": "Center Midfield", "x": 70, "y": 50},
                        {"code": "RWB", "name": "Right Wingback", "x": 90, "y": 50},
                        {"code": "ST", "name": "Striker", "x": 35, "y": 18},
                        {"code": "ST", "name": "Striker", "x": 65, "y": 18}
                    ],
                    "tactics": [
                        "Wingbacks provide all the width in attack and defense",
                        "Three center backs cover central areas",
                        "Box midfield dominates possession in central areas"
                    ]
                },
                {
                    "id": "3-4-3",
                    "name": "3-4-3",
                    "description": "Aggressive attacking formation. High risk, high reward.",
                    "positions": [
                        {"code": "GK", "name": "Goalkeeper", "x": 50, "y": 90},
                        {"code": "CB", "name": "Center Back", "x": 25, "y": 75},
                        {"code": "CB", "name": "Center Back", "x": 50, "y": 78},
                        {"code": "CB", "name": "Center Back", "x": 75, "y": 75},
                        {"code": "LM", "name": "Left Midfield", "x": 12, "y": 50},
                        {"code": "CM", "name": "Center Midfield", "x": 35, "y": 52},
                        {"code": "CM", "name": "Center Midfield", "x": 65, "y": 52},
                        {"code": "RM", "name": "Right Midfield", "x": 88, "y": 50},
                        {"code": "LW", "name": "Left Wing", "x": 20, "y": 22},
                        {"code": "ST", "name": "Striker", "x": 50, "y": 15},
                        {"code": "RW", "name": "Right Wing", "x": 80, "y": 22}
                    ],
                    "tactics": [
                        "Three attackers press high and create chances",
                        "Wide midfielders must work both ways",
                        "High risk formation that can leave defense exposed"
                    ]
                },
                {
                    "id": "4-5-1",
                    "name": "4-5-1",
                    "description": "Packed midfield for possession control. Good for controlling games.",
                    "positions": [
                        {"code": "GK", "name": "Goalkeeper", "x": 50, "y": 90},
                        {"code": "LB", "name": "Left Back", "x": 15, "y": 70},
                        {"code": "CB", "name": "Center Back", "x": 35, "y": 75},
                        {"code": "CB", "name": "Center Back", "x": 65, "y": 75},
                        {"code": "RB", "name": "Right Back", "x": 85, "y": 70},
                        {"code": "LM", "name": "Left Midfield", "x": 15, "y": 45},
                        {"code": "CM", "name": "Center Midfield", "x": 30, "y": 50},
                        {"code": "CAM", "name": "Attacking Mid", "x": 50, "y": 40},
                        {"code": "CM", "name": "Center Midfield", "x": 70, "y": 50},
                        {"code": "RM", "name": "Right Midfield", "x": 85, "y": 45},
                        {"code": "ST", "name": "Striker", "x": 50, "y": 15}
                    ],
                    "tactics": [
                        "Packed midfield dominates possession",
                        "CAM links midfield to lone striker",
                        "Wide players support lone striker with crosses"
                    ]
                },
                {
                    "id": "5-3-2",
                    "name": "5-3-2",
                    "description": "Defensive formation with 3 CBs and wingbacks. Solid at the back.",
                    "positions": [
                        {"code": "GK", "name": "Goalkeeper", "x": 50, "y": 90},
                        {"code": "LWB", "name": "Left Wingback", "x": 10, "y": 65},
                        {"code": "CB", "name": "Center Back", "x": 30, "y": 78},
                        {"code": "CB", "name": "Center Back", "x": 50, "y": 80},
                        {"code": "CB", "name": "Center Back", "x": 70, "y": 78},
                        {"code": "RWB", "name": "Right Wingback", "x": 90, "y": 65},
                        {"code": "CM", "name": "Center Midfield", "x": 30, "y": 48},
                        {"code": "CM", "name": "Center Midfield", "x": 50, "y": 50},
                        {"code": "CM", "name": "Center Midfield", "x": 70, "y": 48},
                        {"code": "ST", "name": "Striker", "x": 35, "y": 18},
                        {"code": "ST", "name": "Striker", "x": 65, "y": 18}
                    ],
                    "tactics": [
                        "Very solid defensive structure",
                        "Wingbacks must be very fit to cover entire flank",
                        "Compact midfield blocks central passing lanes"
                    ]
                },
                {
                    "id": "4-1-4-1",
                    "name": "4-1-4-1",
                    "description": "Defensive formation with CDM anchor. Good for counter-attacking.",
                    "positions": [
                        {"code": "GK", "name": "Goalkeeper", "x": 50, "y": 90},
                        {"code": "LB", "name": "Left Back", "x": 15, "y": 70},
                        {"code": "CB", "name": "Center Back", "x": 35, "y": 75},
                        {"code": "CB", "name": "Center Back", "x": 65, "y": 75},
                        {"code": "RB", "name": "Right Back", "x": 85, "y": 70},
                        {"code": "CDM", "name": "Defensive Mid", "x": 50, "y": 58},
                        {"code": "LM", "name": "Left Midfield", "x": 15, "y": 42},
                        {"code": "CM", "name": "Center Midfield", "x": 35, "y": 45},
                        {"code": "CM", "name": "Center Midfield", "x": 65, "y": 45},
                        {"code": "RM", "name": "Right Midfield", "x": 85, "y": 42},
                        {"code": "ST", "name": "Striker", "x": 50, "y": 15}
                    ],
                    "tactics": [
                        "CDM sits deep to protect the back four",
                        "Wide midfielders track back to form two banks of four",
                        "Lone striker needs to be good at holding up play"
                    ]
                }
            ]
        }
        """;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        resp.getWriter().write(FORMATIONS_JSON);
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setStatus(200);
    }
    
    @Override
    public void init() {
        System.out.println("✅ FormationsServlet loaded");
    }
}
