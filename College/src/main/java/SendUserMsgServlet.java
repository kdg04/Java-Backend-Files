import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

@WebServlet("/sendUserMessage")
public class SendUserMsgServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
	private static final Logger logger = Logger.getLogger(SendUserMsgServlet.class.getName());
    
    public SendUserMsgServlet() {
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
    	
    	FileHandler fileHandler = new FileHandler("C:\\logs\\userMsgList.log", false);   // append mode
		fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
    	
    	MongoDatabase database = mongoClient.getDatabase("Users");
        MongoCollection collection = database.getCollection("staffList");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String userID = request.getParameter("userID"); 
        logger.log(Level.INFO, "user Id : " + userID);

        String requestData = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject json = new JSONObject(requestData);
        JSONArray staffIds = json.getJSONArray("selectedStaff");
        String messageDraft = json.getString("messageDraft");
        
        Document query = new Document("id", Integer.parseInt(userID));       
        Document projection = new Document("name", true).append("_id", false);  

        Document userName = (Document) collection.find(query).projection(projection).first(); 
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Document userMessage = null;
        try {
            for (Object id : staffIds) {
                userMessage = new Document();
                userMessage.append("date", currentDate);
                userMessage.append("from", userName.getString("name"));
                userMessage.append("msg", messageDraft);
                collection.updateOne(Filters.eq("id", id), 
                		Updates.combine(
                                Updates.push("msgs", userMessage),
                                Updates.set("newMsg", true)
                                        )
                		            );              		
            }
            logger.log(Level.INFO, userMessage.toString());
            logger.log(Level.INFO, "recepient id_list : " + staffIds);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("User message delivered successfully");
        } catch (Exception e) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Message delivery failed");
            logger.log(Level.INFO, "User message delivery failed");
            logger.log(Level.INFO, e.getMessage());
        }
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		doPost(request, response);
	}

}
