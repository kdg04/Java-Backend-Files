import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

@WebServlet("/updateMailBox")
public class UpdateMailBoxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
	private static final Logger logger = Logger.getLogger(UpdateMailBoxServlet.class.getName()); 
    
    public UpdateMailBoxServlet() {
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
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		FileHandler fileHandler = new FileHandler("C:\\logs\\updateMailBox.log");
		fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
		
		MongoDatabase database = mongoClient.getDatabase("Users");
	    MongoCollection collection = database.getCollection("staffList");
        response.setCharacterEncoding("UTF-8");
        
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) 
            sb.append(line);
       
        String requestBody = sb.toString();
       
        JSONObject json = new JSONObject(requestBody);
        int userId = json.getInt("userID");
        boolean deleted = json.getBoolean("deleted");
        if(deleted) {         
	        try {
	        	JSONArray newList = json.getJSONArray("messages");	
	        	collection.updateOne(Filters.eq("id", userId), 
	            		Updates.combine(
	                            Updates.set("msgs", newList.toList()),
	                            Updates.set("newMsg", false)
	                                    )
	            		            );
	        	logger.log(Level.INFO, "200! User (id: " + userId + ") message list updated");
	        	response.setStatus(HttpServletResponse.SC_OK);
	            response.getWriter().println("User Message List updated");
	        } catch(Exception e) {
	        	logger.log(Level.INFO, "500! Internal Server Error: Failed to update user (id: " + userId + ") message list <" + e.getMessage() + ">");
	        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            response.getWriter().println("Error updating user message list");
	        }      
        }
        else {
        	try {
	        	collection.updateOne(Filters.eq("id", userId), Updates.set("newMsg", false));
	        	response.setStatus(HttpServletResponse.SC_OK);
	        } catch(Exception e) {
	        	logger.log(Level.INFO, "500! Internal Server Error while updating user (id: " + userId + ") message status <" + e.getMessage() + ">");
	        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            response.getWriter().println("Error updating user message status");
	        }  
        	
        }
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		doPost(request, response);
	}

}
