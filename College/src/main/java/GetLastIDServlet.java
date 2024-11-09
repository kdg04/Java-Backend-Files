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
import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@WebServlet("/addUser")
public class GetLastIDServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
	private static final Logger logger = Logger.getLogger(GetLastIDServlet.class.getName());
	MongoDatabase database;
	MongoCollection collection;
      
    public GetLastIDServlet() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    	String fileID = request.getParameter("fileId");
    	
    	String jsonBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
    	FileHandler fileHandler = new FileHandler("c:\\logs\\addUserGet.log");
        fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
		logger.log(Level.INFO, "request body : " + jsonBody);
    	
    	
	    Document groupStage = new Document("$group", new Document("_id", null).append("maxId", new Document("$max", "$id")));
	    
	    Document result = (Document) collection.aggregate(Arrays.asList(groupStage)).first();
        int maxId = result.getInteger("maxId");
        response.getWriter().println(maxId);
        response.setStatus(HttpServletResponse.SC_OK);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	
    	
    	ObjectMapper mapper = new ObjectMapper();
    	String jsonBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);

    	Map<String, Object> data = mapper.readValue(jsonBody, Map.class);
    	Map<String, Object> values = (Map<String, Object>) data.get("values");
    	List<Object> list = new ArrayList<>();
    	
    	FileHandler fileHandler = new FileHandler("c:\\logs\\addUserPost.log");
        fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
		
		logger.log(Level.INFO, "request body : " + jsonBody);
    	
    	Document doc = new Document("id", values.get("id"))
    			           .append("name", values.get("firstName") + " " + values.get("lastName"))
    			           .append("mob", values.get("contact"))
    			           .append("email", values.get("email"))
    			           .append("msgs",  list)
    			           .append("token", "")
    			           .append("dept", values.get("dept"))
    			           .append("newMsg", false)
    			           .append("profileImage", "")
    			           .append("aboutMe", "");
    	if(values.containsKey("sem"))
    		doc.append("sem", (int)values.get("sem"));
    	else
    		doc.append("grade", (int)values.get("grade"));
    	collection.insertOne(doc);
    	logger.log(Level.INFO, "doc added : " + doc);
    	response.setStatus(HttpServletResponse.SC_OK);
	}

}
