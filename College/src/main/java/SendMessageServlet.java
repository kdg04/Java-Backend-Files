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

@WebServlet("/sendmessage")
public class SendMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
	private static final Logger logger = Logger.getLogger(SendMessageServlet.class.getName());
    
    public SendMessageServlet() {
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
    	
    	FileHandler fileHandler = new FileHandler("C:\\logs\\adminMsgList.log", false);   // append mode
		fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
    	
    	MongoDatabase database = mongoClient.getDatabase("Users");
        MongoCollection collection = database.getCollection("staffList");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Parse JSON data from the request
        String requestData = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject json = new JSONObject(requestData);
        JSONArray staffIds = json.getJSONArray("selectedStaff");
        String messageDraft = json.getString("messageDraft");

        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Document adminMessage = null;
        try {
            for (Object id : staffIds) {
                adminMessage = new Document();
                adminMessage.append("date", currentDate);
                adminMessage.append("from", "Admin");
                adminMessage.append("msg", messageDraft);
                collection.updateOne(Filters.eq("id", id), 
                		Updates.combine(
                                Updates.push("msgs", adminMessage),
                                Updates.set("newMsg", true)
                                        )
                		            );              		
            }
            logger.log(Level.INFO, adminMessage.toString());
            logger.log(Level.INFO, "recepient id_list : " + staffIds);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("Admin message delivered successfully");
        } catch (Exception e) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Message delivery failed");
            logger.log(Level.INFO, "Admin message delivery failed");
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
