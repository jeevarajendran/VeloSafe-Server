package ServerHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

//database imports

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Servlet implementation class MainHandler
 */
@WebServlet("/Test")
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Test() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		try
		{
		response.getWriter().println("I have been called from browser New !!!");
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
		 JSONArray regionJsonArray = new JSONArray();
		 while(region_bins_result.next())
		 {
			 String region_name = region_bins_result.getString(2);
			 Double region_cord_x = region_bins_result.getDouble(3);
			 Double region_cord_y = region_bins_result.getDouble(4);
			 int region_weight = region_bins_result.getInt(5);
			 JSONObject regionJsonObject = new JSONObject();
			 regionJsonObject.put("region_name", region_name);
			 regionJsonObject.put("region_cord_x", region_cord_x);
			 regionJsonObject.put("region_cord_y", region_cord_y);
			 regionJsonObject.put("region_weight", region_weight);
			 regionJsonArray.add(regionJsonObject);
			 
			 System.out.println(region_name);
		 }
		 System.out.println(regionJsonArray.toString());
		}
		catch(Exception e)
		{
			System.out.println("Exception **** "+ e);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			
			HeatMapHandler heatMapHandler = new HeatMapHandler();
			heatMapHandler.getBins(request, response);
			
			//Heat map code
			

			
			 
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
