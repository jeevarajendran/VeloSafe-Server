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

public class RegistrationHandler {

	public static void registerUser(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			System.out.println("GOT HERE!!!");
			JSONParser parser = new JSONParser();
			String reg_details = request.getParameter("reg_details");
			Object obj = parser.parse(reg_details);
			JSONObject jsonObj = (JSONObject) obj;
			String user_email = (String) jsonObj.get("user_email");
			String user_password = (String) jsonObj.get("user_password");
			String user_firstName = (String) jsonObj.get("user_firstName");
			String user_lastName = (String) jsonObj.get("user_lastName");
			String user_contactNo = (String) jsonObj.get("user_contactNo");
			String user_area = (String) jsonObj.get("user_area");
			String bike_make = (String) jsonObj.get("bike_make");
			String bike_modelNo = (String) jsonObj.get("bike_modelNo");
			String bike_frameNo = (String) jsonObj.get("bike_frameNo");
			String bike_color = (String) jsonObj.get("bike_color");


			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/velosafe","root","");

			PreparedStatement preparedStatement = null;
			preparedStatement = connection.prepareStatement("select user_email from user_info where user_email='" + user_email + "'");
			ResultSet registeredCheck = preparedStatement.executeQuery();
			registeredCheck.beforeFirst();
			if(registeredCheck.next()){
				System.out.println("This email has already registered!");
				DataOutputStream out = new DataOutputStream(response.getOutputStream());
				out.writeBytes("Already registered");
				out.flush();
				out.close();
				System.out.println("finished");
			}
			else{
				System.out.println("INSERTING!!");
				Statement stmt = null;
				String insertSql = "INSERT INTO user_info(user_email, user_password, user_firstName, "
						+ "user_lastName, user_contactNo, user_area, bike_make, bike_modelNo, bike_frameNo,"
						+ "bike_color) VALUES ('"+ user_email + "','"  + user_password + "','"  + user_firstName
						 + "','"  + user_lastName + "','"  + user_contactNo + "','"  + user_area + "','"  + bike_make
						 + "','"  + bike_modelNo + "','"  + bike_frameNo + "','"  + bike_color+ "');";
				System.out.println(insertSql);
				DataOutputStream out = new DataOutputStream(response.getOutputStream());
				stmt = connection.createStatement();
				stmt.executeUpdate(insertSql);
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
	   		 		String region_color = region_bins_result.getString(6);
	   		 		JSONObject regionJsonObject = new JSONObject();
	   		 		regionJsonObject.put("region_name", region_name);
	   		 		regionJsonObject.put("region_cord_x", region_cord_x);
	   		 		regionJsonObject.put("region_cord_y", region_cord_y);
	   		 		regionJsonObject.put("region_weight", region_weight);
	   		 		regionJsonObject.put("region_isSafe", region_color);
	   		 		regionJsonArray.add(regionJsonObject);
	   		 		System.out.println("Is regios Safe :"+regionJsonArray);
	   			 }
	   		 	 System.out.println(regionJsonArray.toString());

	   		 	 out.writeBytes(regionJsonArray.toString());
	   		 	 out.flush();
	   		 	 out.close();


			}




//			response.setStatus(HttpServletResponse.SC_OK);
//			DataOutputStream out = new DataOutputStream(response.getOutputStream ());
//			JSONObject jsonObj2 = new JSONObject();
//            long serialVersionUID = 1L;
//            Connection connection = null;
//   		 	PreparedStatement preparedStatement = null;
//   		 	Class.forName("com.mysql.jdbc.Driver");
//   		 	connection = DriverManager.getConnection("jdbc:mysql://localhost/velosafe","root","123");
//   		 	preparedStatement = connection.prepareStatement("select * from region_bins");
//   		 	ResultSet region_bins_result = preparedStatement.executeQuery();
//   		 	JSONArray regionJsonArray = new JSONArray();
//   		 	region_bins_result.beforeFirst();
//
//   		 	while(region_bins_result.next())
//   		 	{
//   		 		String region_name = region_bins_result.getString(2);
//   		 		Double region_cord_x = region_bins_result.getDouble(3);
//   		 		Double region_cord_y = region_bins_result.getDouble(4);
//   		 		Double region_weight = region_bins_result.getDouble(5);
//   		 		JSONObject regionJsonObject = new JSONObject();
//   		 		regionJsonObject.put("region_name", region_name);
//   		 		regionJsonObject.put("region_cord_x", region_cord_x);
//   		 		regionJsonObject.put("region_cord_y", region_cord_y);
//   		 		regionJsonObject.put("region_weight", region_weight);
//   		 		regionJsonArray.add(regionJsonObject);
//   			 }
//   		 	 System.out.println(regionJsonArray.toString());
//
//   		 	 out.writeBytes(regionJsonArray.toString());
//   		 	 out.flush();
//   		 	 out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
