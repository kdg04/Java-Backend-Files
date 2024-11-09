package test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;


public class Test {
	
	public static byte[] hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(password.getBytes());
        return hashBytes;
    }

	public static void main(String[] args) throws NoSuchAlgorithmException {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try { 
			
			conn =  DriverManager.getConnection("jdbc:mysql://localhost:3306/SuperAdmin", "root", "Shrihari7777");
			stmt = conn.createStatement();
			
			/*
			String user = "AdminShri";
			String pwd = "Shri7777";
			*/
			
			/*
			String sql = "Create Database if not exists SuperAdmin";
			stmt.executeUpdate(sql);
			System.out.println("database created successfully...");
						 
			String sql = "Create table if not exists admin("
					+ "user VARCHAR(50) not null,"
					+ "pwd VARBINARY(255)not null,"
					+ "primary key (user)"
					+ ")";
			stmt.executeUpdate(sql);
			System.out.println("Admin Table created successfully... ");						
					
			byte[] hash = hashPassword(pwd);
			String sql = "INSERT INTO admin(user, pwd) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            pstmt.setBytes(2, hash);
            pstmt.executeUpdate();
            System.out.println("data stored successfully!");
            */
            
            ResultSet rs = stmt.executeQuery("select * from admin"); 
            
            if(rs.next()) {
            	String usr = rs.getString(1);
                byte[] barr = rs.getBytes(2);
                for(byte b : barr)
                	System.out.println(b);
            }
            
         
            
			/*
			String requestBody = "{\"username\":\"fghlk\",\"password\":\"hjkl\"}";
			JSONObject json = new JSONObject(requestBody);
	        String username = json.getString("username");
	        String password = json.getString("password");
            System.out.println("pwd : " + password);
            */

	    }catch (SQLException e) { 
			e.printStackTrace();	
        }
	}
}
