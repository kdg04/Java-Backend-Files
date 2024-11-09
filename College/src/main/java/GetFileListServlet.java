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
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

@WebServlet("/getFileList")
public class GetFileListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
	private static final Logger logger = Logger.getLogger(GetFileListServlet.class.getName());
	
    public GetFileListServlet() {
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
        
        FileHandler fileHandler = new FileHandler("C:\\logs\\Filenames.log", false);   // append mode
		fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
        
        Document query = new Document("id", Integer.parseInt(userID));
        Document userInfo = (Document) collection.find(query).first();
        Document pdfQuery = new Document();
        
        if (userInfo != null) {
            String dept = userInfo.getString("dept");
            if (userInfo.containsKey("sem")) {              // User is a student             
                int sem = userInfo.getInteger("sem");
                pdfQuery.put("sem", sem);
            }
            pdfQuery.put("dept", dept);
        } else {                                    // User not found in the staffList collection   
        	 logger.log(Level.INFO, "no information of user's files");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("User not found");
            return;
        }
               
        collection = database.getCollection("pdf_files");    
        Bson projection = Projections.include("name", "_id");
        FindIterable<Document> itr = collection.find(pdfQuery).projection(projection);
        List<Document> fileList = new ArrayList<>();
        for(Document d : itr) {
        	ObjectId id = d.getObjectId("_id");
        	Document doc = new Document("_id", id.toHexString()).append("name", d.getString("name"));
        	fileList.add(doc);
        }
        
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(fileList);
        logger.log(Level.INFO, json);
        response.setContentType("application/json");
        response.getWriter().write(json);
        response.setStatus(HttpServletResponse.SC_OK);
        
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    	
		doGet(request, response);
	}

}
