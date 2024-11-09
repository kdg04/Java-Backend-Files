import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

@WebServlet("/messages")
public class ReadMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");

    public ReadMessageServlet() throws Exception {
        String connectionString = "mongodb://localhost:27017";
        String databaseName = "Users";
        String collectionName = "staffList";       
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    	
        MongoDatabase database = mongoClient.getDatabase("Users");
        MongoCollection collection = database.getCollection("staffList");
        
        Document query = new Document("id", 0);
        Document projection = new Document("msgs", true).append("_id", false);  // returns only 'msgs' array, exclude _id field 

        FindIterable<Document> result = collection.find(query).projection(projection); 

        List<Object> messageList = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().iterator();
        
        for(Document d : result) 
            messageList = d.getList("msgs", Object.class);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(messageList);

        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
