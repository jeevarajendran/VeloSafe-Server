package ServerHandler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

public class HeatMapHandler {

	public static void getBins(HttpServletRequest request, HttpServletResponse response)
	{
		try {


			 System.out.println("Database Connection Successful...! *");
			 response.setStatus(HttpServletResponse.SC_OK);
			 DataOutputStream out = new DataOutputStream(response.getOutputStream ());

			 Connection connection = null;
			 PreparedStatement preparedStatement = null;
			 Class.forName("com.mysql.jdbc.Driver");
			 connection = DriverManager.getConnection("jdbc:mysql://localhost/velosafe","root","");
			 preparedStatement = connection.prepareStatement("select * from region_bins");
			 ResultSet region_bins_result = preparedStatement.executeQuery();
			 JSONArray regionJsonArray = new JSONArray();

			 region_bins_result.beforeFirst();

			 while(region_bins_result.next())
			 {
				 String region_name = region_bins_result.getString(2);
				 Double region_cord_x = region_bins_result.getDouble(3);
				 Double region_cord_y = region_bins_result.getDouble(4);
				 Double region_weight = region_bins_result.getDouble(5);
				 String region_isSafe = region_bins_result.getString(6);
				 JSONObject regionJsonObject = new JSONObject();
				 regionJsonObject.put("region_name", region_name);
				 regionJsonObject.put("region_cord_x", region_cord_x);
				 regionJsonObject.put("region_cord_y", region_cord_y);
				 regionJsonObject.put("region_weight", region_weight);
				 regionJsonObject.put("regoin_isSafe", region_isSafe);

				 regionJsonArray.add(regionJsonObject);
   			 }
			 System.out.println(regionJsonArray.toString());
			 out.writeBytes(regionJsonArray.toString());
			 out.flush();
			 out.close();
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
