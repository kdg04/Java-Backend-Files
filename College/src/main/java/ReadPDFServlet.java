import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@WebServlet("/readPDF")
public class ReadPDFServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ReadPDFServlet.class.getName()); 
	MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/Users");
	String baseURL = "C:\\User Files\\";
     
    public ReadPDFServlet() {
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
    	String fileID = request.getParameter("fileId");
    	
    	FileHandler fileHandler = new FileHandler("c:\\logs\\PdfFiles.log");
        fileHandler.setLevel(Level.INFO);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.INFO);
		fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
		MongoDatabase database = mongoClient.getDatabase("Users");
	    MongoCollection collection = database.getCollection("pdf_files");
		
		logger.log(Level.INFO, "fileId : " + fileID);

	
	    Document doc = (Document) collection.find(new Document("_id", new ObjectId(fileID))).first();
    	String filename = doc.getString("name");
    	String filePath = baseURL + filename.trim();
    	response.setContentType("application/pdf");
    	//response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");   // for downloading the file
    	
    	try(FileInputStream fis = new FileInputStream(filePath);
           	ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while((bytesRead = fis.read(buffer)) != -1) 
               	  baos.write(buffer, 0, bytesRead);  
                response.getOutputStream().write(baos.toByteArray());
            } catch(Exception e){
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  	
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		
		doGet(request, response);
	}

}
