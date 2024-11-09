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
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

@WebServlet("/deptStaff")
public class DeptStaffServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
	private static final Logger logger = Logger.getLogger(DeptStaffServlet.class.getName());
       
    public DeptStaffServlet() {
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
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    	
    	MongoDatabase database = mongoClient.getDatabase("Users");
        MongoCollection collection = database.getCollection("staffList");
        
        FileHandler fileHandler = new FileHandler("C:\\logs\\deptStaff.log");
        fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
		
        String dept = request.getParameter("dept");
        logger.log(Level.INFO, "dept : " + dept);
        
        Bson projection = Projections.include("name", "mob", "email", "grade");
        List<Object> staffList = new ArrayList<>();
        Document query = new Document("dept", dept).append("grade", new Document("$exists", true));
        MongoCursor<Document> cursor = collection.find(query).projection(projection).iterator(); 
        int serialNo = 0;
        try {
            while (cursor.hasNext()) {
            	Document d = cursor.next();
            	
            	ObjectId id = d.getObjectId("_id");
            	Document doc = new Document()
            			   .append("id", id.toHexString())          			   
            			   .append("name", d.getString("name"))
            			   .append("mob", d.getString("mob"))   
            			   .append("email", d.getString(("email")))
            			   .append("grade", d.getInteger(("grade")));
               System.out.println("doc : " + doc.toJson());
            	staffList.add(doc);
            }
        } finally {
            cursor.close();
        }

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(staffList);
        logger.log(Level.INFO, "user created "  + json);
        fileHandler.close();
        response.setContentType("application/json");
        response.getWriter().write(json);
        fileHandler.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
		doGet(request, response);
	}

}
