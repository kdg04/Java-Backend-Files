import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

@WebServlet("/getAttendance")
public class PersonAttendanceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
    
    public PersonAttendanceServlet() {
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
 	    MongoCollection collection = database.getCollection("staff_attendance");
        //String userID = request.getParameter("userId");
 	    String userID = "23";
        
        Document query = new Document("id", Integer.parseInt(userID));
        Bson projection = Projections.include("attendance");
        Document doc = (Document) collection.find(query).projection(projection).first();
        System.out.println(doc);
        List<Integer> attendance = doc.getList("attendance", Integer.class);
        int present = 0;
        int abscent = 0;
        for(int a : attendance) 
        	if(a == 1)
        		present++;
        	else if(a == -1)
        		abscent++;
        String json = "{\"present\":" + present + ", \"abscent\":" + abscent + "}";
        System.out.println(json);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(json);
        response.setStatus(HttpServletResponse.SC_OK);      
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		doGet(request, response);
	}

}
