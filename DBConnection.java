package ServerHandler;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	
	public Connection getConnectionForRead(){
		Connection connection = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost/velosafe","root","");
		}
		catch(Exception e){
			try{
			connection = DriverManager.getConnection("jdbc:mysql://localhost/velosafe2", "root", "");
			}
			catch(Exception E){
				E.printStackTrace();
				System.out.println("Exception in getConnectionForRead "+ E);
			}
		}
		return connection;
	}
	
	public Connection getConnectionForWrite(String dbName){
		Connection connection = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost/"+dbName,"root","");
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in getConnectionForWrite "+ e);	
		}
		return connection;
	}
}
