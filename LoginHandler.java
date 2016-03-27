package ServerHandler;

import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LoginHandler {
	public static void loginUser(HttpServletRequest request, HttpServletResponse response){
		System.out.println("login user ing");
		try{
			JSONParser parser = new JSONParser();
			String login_details = request.getParameter("login_details");
			Object obj = parser.parse(login_details);
			JSONObject jsonObj = (JSONObject) obj;
			String user_email = (String) jsonObj.get("email");
			String user_password = (String) jsonObj.get("password");
			System.out.println("email: " + user_email);
			System.out.println("password: " + user_password);
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/velosafe","root","");

			PreparedStatement preparedStatement = null;
			preparedStatement = connection.prepareStatement("select user_password from user_info where user_email='" + user_email + "'");
			ResultSet passwordCheck = preparedStatement.executeQuery();
			passwordCheck.beforeFirst();
			DataOutputStream out = new DataOutputStream(response.getOutputStream());
			if(passwordCheck.next()){
				String originpassword = passwordCheck.getString(1);
				if (originpassword.equals(user_password)){
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
		   			 }
					String Msg = "login successful&" + regionJsonArray.toString();
					out.writeBytes(Msg);
					out.flush();
					out.close();
				}
				else{
					out.writeBytes("not match&");
					out.flush();
					out.close();
				}
			}
			else{
				out.writeBytes("not registerd&");
				out.flush();
				out.close();
			}




			}	catch (Exception e){
				e.printStackTrace();
			}

	}


}
