package ServerHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseTest {
	
	public static void main(String args[]){
		try
		{
		long serialVersionUID = 1L;
		 Connection connection = null;
		 System.out.println("Database Connection Successful...! *");
		 PreparedStatement preparedStatement = null;
		 System.out.println("Database Connection Successful...! **");
		 Class.forName("com.mysql.jdbc.Driver");
		 System.out.println("Database Connection Successful...! ***");
		 connection = DriverManager.getConnection("jdbc:mysql://localhost/velosafe","root","");
		 System.out.println("Database Connection Successful...!");
		 preparedStatement = connection.prepareStatement("select * from region_bins");
		 ResultSet region_bins_result = preparedStatement.executeQuery();
		 System.out.println(region_bins_result.toString());
		}
		catch(Exception e)
		{
			System.out.println("Exception **** "+ e);
		}
	}

}
