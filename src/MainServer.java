import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.DefaultServlet;
import java.io.File;

public class MainServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server(3000);
        
        File publicDir = new File("public");
        System.out.println("Public folder: " + publicDir.getAbsolutePath());
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase(publicDir.getAbsolutePath());
        
        // API Servlets
        context.addServlet(new ServletHolder(new servlet.LoginServlet()), "/login");
        context.addServlet(new ServletHolder(new servlet.LogoutServlet()), "/logout");
        context.addServlet(new ServletHolder(new servlet.SessionCheckServlet()), "/session-check");
        context.addServlet(new ServletHolder(new servlet.LiveMatchesServlet()), "/live");
        context.addServlet(new ServletHolder(new servlet.StandingsServlet()), "/standings");
        context.addServlet(new ServletHolder(new servlet.TeamsServlet()), "/teams");
        context.addServlet(new ServletHolder(new servlet.TeamDetailsServlet()), "/teams/*");
        context.addServlet(new ServletHolder(new servlet.TournamentServlet()), "/tournament/*");
        context.addServlet(new ServletHolder(new servlet.FormationsServlet()), "/formations");
        
        // Static files
        context.addServlet(new ServletHolder(new DefaultServlet()), "/");
        
        server.setHandler(context);
        server.start();
        
        System.out.println("========================================");
        System.out.println("Football TMS: http://localhost:3000");
        System.out.println("========================================");
        
        server.join();
    }
}