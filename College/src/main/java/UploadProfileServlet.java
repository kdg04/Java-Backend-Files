import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;


@WebServlet("/uploadProfile")
@MultipartConfig
public class UploadProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(UploadProfileServlet.class.getName()); 
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
    
    public UploadProfileServlet() {
        super();      
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private String extractFileName(Part part) throws IOException {
    	String contentDisposition = part.getHeader("Content-Disposition");
    	String[] items = contentDisposition.split(";", -1);
    	for (String item : items)
    		if(item.trim().startsWith("filename"))
    			return item.substring(item.indexOf('=') + 1).trim();
    	return null; 	
    }
    
    private void writeUploadedfile(InputStream is, String filePath) throws IOException {
    	try(FileOutputStream os = new FileOutputStream(filePath)) {
    		byte[] buffer = new byte[4096];
    		int bytesRead;
    		while((bytesRead = is.read(buffer)) != -1)
    			os.write(buffer, 0, bytesRead);
    	} catch(Exception e) {}		
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		
    	FileHandler fileHandler = new FileHandler("C:\\logs\\uploadImage.log");
		fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
		
		MongoDatabase database = mongoClient.getDatabase("Users");
	    MongoCollection collection = database.getCollection("staffList");
		
		Part profileImagePart;		
		String name = request.getParameter("name");
		String userID = request.getParameter("userId");
		String mob = request.getParameter("mobile");
		String email = request.getParameter("email");
		String aboutMe = request.getParameter("aboutMe");
		
		try {
			profileImagePart = request.getPart("profileImage");
		} catch(Exception e) {
			logger.log(Level.INFO, "Error in handling parts! : " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Profile Data corrupted");
			logger.log(Level.INFO, "profile image of user (" + userID + ") not delivered properly. Error! Bad Request ");
			return;						
		}
		
		final String filePath = "C:\\User Pics\\userId " + userID + ".jpg";
		try {
			writeUploadedfile(profileImagePart.getInputStream(), filePath);		
			collection.updateOne(Filters.eq("id", Integer.parseInt(userID)), 
	        		Updates.combine(
	                        Updates.set("email", email),
	                        Updates.set("mob", mob),
	                        Updates.set("aboutMe", aboutMe),
	                        Updates.set("profileImage", filePath)
	                                )
	        		            );
			logger.log(Level.INFO, "profile image of user (" + userID + ") uploaded successfully");
			logger.log(Level.INFO, "updated user profile data : { " + name + ", " + email + ", " + mob + ", " + aboutMe + " }");		
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("Profile uploaded successfully");
		} catch(IOException e) {
			logger.log(Level.INFO, "Error uploading profile image!");		
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error uploading profile image!");
		} catch(Exception e) {
			logger.log(Level.INFO, "Error updating database!");		
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error updating database!");			
		}
		
		fileHandler.close();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    	doPost(request, response);
    	
	}

}
