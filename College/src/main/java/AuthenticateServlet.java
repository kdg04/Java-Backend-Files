import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/authenticate")
public class AuthenticateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AuthenticateServlet.class.getName());
   
    public AuthenticateServlet() {
        super();      
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		
    	FileHandler fileHandler = new FileHandler("C:\\logs\\authenticate.log");
		fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());

    	
		StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) 
            sb.append(line);
       
        String requestBody = sb.toString();
        logger.log(Level.INFO, requestBody);
        
        JSONObject json = new JSONObject(requestBody);
        String username = json.getString("username");
        String password = json.getString("password");

        
        Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String user = null;
		byte[] hash = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			logger.log(Level.INFO, "jdbc driver registered successfully");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try { 
			
			conn =  DriverManager.getConnection("jdbc:mysql://localhost:3306/SuperAdmin", "root", "Shrihari7777");
			stmt = conn.createStatement();   
			rs = stmt.executeQuery("select * from admin");
			if(rs.next()) {
			   user = rs.getString(1);
			   hash = rs.getBytes("pwd");
			}
		}catch (SQLException e) { 
			e.printStackTrace();	
          }
			
		
		byte[] hashOfReceivedPwd = null;
		try {
			hashOfReceivedPwd = MessageDigest.getInstance("SHA-256").digest(password.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}      
        
        if (username.equals(user) && Arrays.equals(hash, hashOfReceivedPwd)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("Authentication successful");
            logger.log(Level.INFO, "Admin authentication successful");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Authentication failed");
            logger.log(Level.INFO, "Admin authentication failed");
        }
		
	}  // end of doPost
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		
		doPost(request,response);

	}
}
