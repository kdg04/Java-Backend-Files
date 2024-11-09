import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

@WebServlet("/uploadFile")
@MultipartConfig
public class UploadFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
	private static final Logger logger = Logger.getLogger(UploadFileServlet.class.getName());
	   
    public UploadFileServlet() {
        super();
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private String extractFileName(Part part) {
    	String contentDisposition = part.getHeader("content-disposition");    // filename="Homework_1.pdf"
    	String[] splitHeader = contentDisposition.split(";", -1);
    
    	for(String s : splitHeader) {
    		s = s.trim();
    		if(s.startsWith("filename")) 
        		return(s.substring(s.indexOf('=') + 1).trim().replaceAll("\"", ""));   // replace the double quotes with ""
    	}
    	return null;
    }
    
    private void writeFile(Part part, Logger logger, String ... arg) {
    	String dept = arg[0];
    	String sem = arg[1];
    	String baseURL = "C:\\User Files";
    	String url = null;
    	if(sem != null) 
    		url = baseURL + "\\" + dept + "\\" + sem;
    	else 
    		url = baseURL + "\\" + dept;
    	
    	String filename = extractFileName(part);
    	String filepath = null;
    	if(filename != null)
    		filepath = url + "\\" + filename;
    	logger.log(Level.INFO, "filepath : " + filepath);
    	InputStream is = null;
		try {
			is = part.getInputStream();
		} catch (IOException e) {	
		}
    	try {
    		File directory = new File(url);
    		if (!directory.exists()) 
    		    directory.mkdirs();           // This will create all necessary parent directories
    		
    		FileOutputStream fos = new FileOutputStream(filepath);
			int bytesRead;
			byte[] buffer = new byte[4096];
			while((bytesRead = is.read(buffer)) != -1) 
				 fos.write(buffer, 0, bytesRead);  			
		} catch (IOException e) {			
		}   		
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    	
    	FileHandler fileHandler = new FileHandler("C:\\logs\\uploadFile.log", false);   // append mode
		fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
		
		Part pdfFile = null;
		PrintWriter writer = response.getWriter();		
		MongoDatabase database = mongoClient.getDatabase("Users");
	    MongoCollection collection = database.getCollection("staffList");	    
	    String userID = request.getParameter("userId");
		logger.log(Level.INFO, "userId : " + request.getParameter("userId"));
		String dept = null;
		Integer sem = null;          // sem may be present or absent
				
		Document query = new Document("id", Integer.parseInt(userID));
        Document userInfo = (Document) collection.find(query).first();
       
        if (userInfo != null) {
            dept = userInfo.getString("dept");
            if (userInfo.containsKey("sem"))               // User is a student     if not user is a staff        
                sem = userInfo.getInteger("sem");           
        } else {                                           // User not found in the staffList collection   
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("User not found");
            return;
        }	
        
        logger.log(Level.INFO, "user id : " + userID + "  dept : " + dept + "  sem : " + sem);
		try {
			pdfFile = request.getPart("pdfFile");			
		} catch(Exception e) {
			writer.println("File corrupted");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}	
		try {
			if(sem == null)
		        writeFile(pdfFile, logger, dept);
			else
				writeFile(pdfFile, logger, dept, String.valueOf(sem));
		} catch (Exception e) {
			writer.println("File uploading failed");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		writer.println("File uploaded successfully");
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		
		doPost(request, response);
	}
}

