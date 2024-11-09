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
import org.bson.conversions.Bson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Projections;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@WebServlet("/fetchProfile")
public class FetchProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FetchProfileServlet.class.getName()); 
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
    
    public FetchProfileServlet() {
        super();
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    	
    	FileHandler fileHandler = new FileHandler("C:\\logs\\fetchProfile.log");
		fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
    	
    	MongoDatabase database = mongoClient.getDatabase("Users");
	    MongoCollection collection = database.getCollection("staffList");
	    
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String userID = request.getParameter("userId");
	            
        Document query = new Document("id", Integer.parseInt(userID));       
        
        Bson projection = Projections.include("name", "mob", "email", "profileImage", "aboutMe");
        try {
	        Document profileData = (Document) collection.find(query).projection(projection).first();    
	        ObjectMapper mapper = new ObjectMapper();
	        String json = mapper.writeValueAsString(profileData);
	        logger.log(Level.INFO, "profile data of user (" + userID + ") : " + json);
	        response.setContentType("application/json");
	        response.setStatus(HttpServletResponse.SC_OK);
	        response.getWriter().write(json);
        } catch(Exception e) {
        	logger.log(Level.INFO, "Error in obtaining user's (" + userID + ") profile");
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error in obtaining user's profile");
        	
        }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		doGet(request, response);
	}

}
