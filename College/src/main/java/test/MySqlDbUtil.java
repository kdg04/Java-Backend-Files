package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlDbUtil {

	public static void main(String[] args) {
		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try { 
			
			con =  DriverManager.getConnection("jdbc:mysql://localhost/Users", "root", "Shrihari7777");
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			/*
			String sql = "Create Database if not exists Users";
			stmt.executeUpdate(sql);
			System.out.println("Database created successfully... ");
			*/
			
			/*
			String sql = "Create table if not exists Users("
					+ "user varchar(50) not null,"
					+ "pwd text not null,"
					+ "userID int not null,"
					+ "primary key (user)"
					+ ")";
			stmt.executeUpdate(sql);
			System.out.println("Client Table created successfully... ");
			
			*/
			
			/*
			stmt.executeUpdate("Insert into users(user, pwd, userID)"
					+ "values('Shrihari', 's123', 60)");
			stmt.executeUpdate("Insert into users(user, pwd, userID)"
					+ "values('Runank', 'r123', 61)");
			stmt.executeUpdate("Insert into users(user, pwd, userID)"
					+ "values('Rakesh', 'rk123', 23)");
			stmt.executeUpdate("Insert into users(user, pwd, userID)"
					+ "values('Abhisekh', 'a123', 17)");
			stmt.executeUpdate("Insert into users(user, pwd, userID)"
					+ "values('Shrikant', 'sk123', 79)");
			stmt.executeUpdate("Insert into users(user, pwd, userID)"
					+ "values('Kalpana', 'k123', 88)");
			stmt.executeUpdate("Insert into users(user, pwd, userID)"
					+ "values('Animesh', 'an123', 39)");
			stmt.executeUpdate("Insert into users(user, pwd, userID)"
					+ "values('Ajay', 'aj123', 52)");
			stmt.executeUpdate("Insert into users(user, pwd, userID)"
					+ "values('Kavita', 'sk123', 67)");
			
					
			System.out.println("Table values updated");
			*/
			
			
            ResultSet rs = stmt.executeQuery("select * from Users");
			
			while(rs.next())
		       System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getInt(3));
		    
			/*
			String username = request.getParameter("User");
			System.out.println("uname is " + username);
			
			ResultSet rs = stmt.executeQuery("select * from users2 where uname = '"+username+"'");
			ResultSet rs = stmt.executeQuery("select count(*) as count from users2 where uname = '"+username+"'");
			while(rs.next())
			  System.out.println("reached " + rs.getInt("count"));
			
			*/
			
		}catch (SQLException e) { 
			e.printStackTrace();	
          }
		 
		//writer.print("Users Table updated successfully... ");
		//writer.flush();
	}

}
