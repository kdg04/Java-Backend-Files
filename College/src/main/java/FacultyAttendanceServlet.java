import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import mongoutil.MongoUtil;

class FacultyAttndStats{
	int todaysCount;
	List<Integer> dailyAttdn = new ArrayList<>();
		
	public FacultyAttndStats(int todaysCount, List<Integer> dailyAttdn) {
		this.todaysCount = todaysCount;
		this.dailyAttdn = dailyAttdn;		
	}
	
	public int getTodaysCount() { 
		return todaysCount;
	}

	public List<Integer> getDailyAttdn() {
		return dailyAttdn;
	}
}

@WebServlet("/FacultyAttendance")
public class FacultyAttendanceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public FacultyAttendanceServlet() {}
    
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
    	
    	MongoDatabase db = MongoUtil.getDatabase();
    	MongoCollection staffAttendance = db.getCollection("faculty_attendance");

    	List<Document> documents = (List<Document>) staffAttendance.find().into(new ArrayList<>());
    	// find the last working day. The last working day cannot be less than the first day of the month, check this from the fisrt document of the list
    	List<Integer> firstAttendanceSheet = documents.get(0).getList("attendance", Integer.class);
    	
    	int attdnListSize = firstAttendanceSheet.size();
    	int last;   // last -> last working day    	
   	    for(last = firstAttendanceSheet.size() - 1; last > 0 && firstAttendanceSheet.get(last) == -1; last--);

        int todayCount = 0;
        List<Integer> dailyAttdn = new ArrayList<>(Collections.nCopies(attdnListSize, 0));
        
        int val = 0;
        for(Document doc : documents) {
        	 List<Integer> attendanceList = doc.getList("attendance", Integer.class);
        	 todayCount += attendanceList.get(last);  
        	       	 
        	 for(int i = 0; i < attdnListSize; i++) 
        		 dailyAttdn.set(i, (dailyAttdn.get(i) + attendanceList.get(i)) );     	 
        }
        
        for(int i = 0; i < attdnListSize; i++) 
   		   if(dailyAttdn.get(i) < 0)
   			   dailyAttdn.set(i, 0);
   		        
        if(todayCount < 0)  // implies the last working day, in this case the first day of the month is a holiday
		   todayCount = 0;        
        
		FacultyAttndStats stats = new FacultyAttndStats(todayCount, dailyAttdn);
		
		response.setContentType("application/json");
		response.getWriter().write(new ObjectMapper().writeValueAsString(stats));
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		doGet(request, response);
	}

}
