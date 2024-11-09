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
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Projections;

@WebServlet("/newmessagestatus")
public class NewMsgStatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
	private static final Logger logger = Logger.getLogger(NewMsgStatusServlet.class.getName());
   
    public NewMsgStatusServlet() {
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
    	
    	FileHandler fileHandler = new FileHandler("C:\\logs\\NewAdminMsgs.log", false);   // append mode
		fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
    	
    	MongoDatabase database = mongoClient.getDatabase("Users");
        MongoCollection collection = database.getCollection("staffList");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        database.getCollection("staffList").createIndex(new Document("id", 1), new IndexOptions().unique(true));
        
        
        Document query = new Document("id", 0);
        Document projection = new Document("newMsg", true).append("_id", false);  // returns only newMsg field, exclude _id field 

        Document newMsg = (Document) collection.find(query).projection(projection).first(); 
        logger.log(Level.INFO, newMsg.toJson());
        
        
        /*
        Bson filter = Filters.and(Filters.eq("id", 0), Filters.eq("newMsg", true));       // constructing the filter object
        Bson projection = Projections.fields(Projections.excludeId(), Projections.include("msgs"));   // exclude object _id
        
        FindIterable<Document> newMsg = collection.find(filter).projection(projection);
        newMsg.forEach(d -> logger.log(Level.INFO, d.toJson()));
        */
        
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(newMsg);

        response.setContentType("application/json");
        response.getWriter().write(json);
		}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
