package ServerHandler;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	
	public Connection getConnection(){
		Connection connection = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			// check for next database here - fault tolerance
			connection = DriverManager.getConnection("jdbc:mysql://localhost/velosafe","root","");
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in getConnection "+ e);
		}
		return connection;
	}
}
