import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

@WebServlet("/userMessages")
public class ReadUserMsgServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ReadUserMsgServlet.class.getName()); 
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");

    public ReadUserMsgServlet() throws Exception {
        super();      
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    	
        MongoDatabase database = mongoClient.getDatabase("Users");
        MongoCollection collection = database.getCollection("staffList");
        String userID = request.getParameter("userId");
        
        FileHandler fileHandler = new FileHandler("c:\\logs\\UserMsgs.log");
        fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
		logger.log(Level.INFO, userID);
		

        List<Object> messageList = new ArrayList<>();
        Document query = new Document("id", Integer.parseInt(userID));
        Document projection = new Document("msgs", true).append("_id", false);  // returns only 'msgs' array, exclude _id field 

        FindIterable<Document> result = collection.find(query).projection(projection); 
        for(Document d : result) 
            messageList = d.getList("msgs", Object.class);
       
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(messageList);
        logger.log(Level.INFO, json);

        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		
		doGet(request, response);
	}

}
