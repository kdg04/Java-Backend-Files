import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/authenticateUser")
public class AuthenticateUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AuthenticateUserServlet.class.getName());
   
    public AuthenticateUserServlet() {
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
		
    	FileHandler fileHandler = new FileHandler("C:\\logs\\authenticateUser.log");
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
        logger.log(Level.INFO, "user authentication request comes in with " + requestBody.toString());
        
        org.json.JSONObject json = new org.json.JSONObject(requestBody);
        String username = json.getString("username");
        String password = json.getString("password");
        
        Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String user = null;
		String pwd = null;
		int userID = -1;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try { 			
			conn =  DriverManager.getConnection("jdbc:mysql://localhost:3306/Users", "root", "Shrihari7777");
			stmt = conn.prepareStatement("SELECT * FROM users WHERE users.user = ? AND users.pwd = ?");  
			stmt.setString(1, username);
		    stmt.setString(2, password);
		    rs = stmt.executeQuery();
			if(rs.next()) {
			   userID = rs.getInt(3);
			   response.setStatus(HttpServletResponse.SC_OK);			   
		       response.getWriter().println(userID);			   
			   logger.log(Level.INFO, "User authentication successful and id is " + userID);
			} else {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);            
			    response.getWriter().println(userID);			   
				logger.log(Level.INFO, "User authentication failed and sent response is " + userID);
	        }
		}catch (SQLException e) { 
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		    logger.log(Level.SEVERE, "Database error:", e);	
          }
		
	}  // end of doPost
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		
		doPost(request,response);

	}
}
