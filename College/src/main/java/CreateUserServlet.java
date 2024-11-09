import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@WebServlet("/createUser")
public class CreateUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
	private static final Logger logger = Logger.getLogger(CreateUserServlet.class.getName());
	MongoDatabase database;
	MongoCollection collection;
      
    public CreateUserServlet() {
        super();
        database = mongoClient.getDatabase("Users");
	    collection = database.getCollection("staffList");
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
    	
    	FileHandler fileHandler = new FileHandler("c:\\logs\\createUser.log");
        fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
	  	
    	ObjectMapper mapper = new ObjectMapper();
    	String jsonBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
    	
    	Map<String, Object> values = mapper.readValue(jsonBody, Map.class);   	
    	List<Object> list = new ArrayList<>();
		    	
    	Document doc = new Document("id", values.get("id"))
    			           .append("name", values.get("firstName") + " " + values.get("lastName"))
    			           .append("mob", values.get("contact"))
    			           .append("email", values.get("email"));
    	if(values.get("sem") == "") {
    		String grade = (String)values.get("grade");
    		doc.append("grade", Integer.valueOf(grade));
    	}
    	else {
    		String sem = (String)values.get("sem");
    		doc.append("sem", Integer.valueOf(sem));
    	}
    	doc.append("msgs",  list)
    	   .append("token", "")
           .append("dept", values.get("dept"))
           .append("newMsg", false)
           .append("profileImage", "")
           .append("aboutMe", "");
    	collection.insertOne(doc);
    	logger.log(Level.INFO, "user added : " + doc);
    	
    	response.setStatus(HttpServletResponse.SC_OK);
    	fileHandler.close();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    	
    	doPost(request, response);
	}
	
}
