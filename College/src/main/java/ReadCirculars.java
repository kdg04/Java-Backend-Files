import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

@WebServlet("/readCirculars")
public class ReadCirculars extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
	
    public ReadCirculars() {
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
    	
        MongoDatabase database = mongoClient.getDatabase("Users");
        MongoCollection collection = database.getCollection("staffList");
        String userID = request.getParameter("userId");
        
        List<Object> circularsList = new ArrayList<>();
        Document query = new Document("id", Integer.parseInt(userID));
        Document projection = new Document("dept", true).append("_id", false);
        Document subQuery = (Document) collection.find(query).projection(projection).first();  // select the dept of the user
        
        collection = database.getCollection("circularsList");    // get the collection to obtain circulars
        FindIterable<Document> result = collection.find(subQuery);
        for(Document d : result) 
            circularsList.add(d);
        
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(circularsList);
        response.setContentType("application/json");
        response.getWriter().write(json);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
