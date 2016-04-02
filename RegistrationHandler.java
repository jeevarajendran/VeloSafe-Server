package ServerHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Class Name : RegistrationHanler
 * Purpose : To receive user's personal details and bike details and store the details to the database, and
 * 			 send back the latest heat map information
 */
public class RegistrationHandler {
	
	/**
     * Method Name : registerUser
     * Purpose : To receive user's personal details and bike details and store the details to the database, and
     * 			 give back the latest heat map information
     * Parameters : HttpServletRequest request, HttpServletResponse response
     * Return value : Null
     */
	public static void registerUser(HttpServletRequest request, HttpServletResponse response){
		try{
			Connection connection = null;
			JSONParser parser = new JSONParser();
			JSONArray regionJsonArray = new JSONArray();
			String reg_details = request.getParameter("reg_details");
			Object obj = parser.parse(reg_details);
			JSONObject jsonObj = (JSONObject) obj;
			String user_email = (String) jsonObj.get("user_email");
			DBConnection dbConnection = new DBConnection();
			connection = dbConnection.getConnection();
			DataOutputStream out = new DataOutputStream(response.getOutputStream());
			ResultSet registeredCheck = getUserEmailfromDB(connection,user_email);
			registeredCheck.beforeFirst();
			if(registeredCheck.next()){
				out.writeBytes("Already registered");
				out.flush();
				out.close();
			}
			else{
				insertUserToDB(connection, jsonObj);
				ResultSet region_bins_result = RegionBin.executeBinsQuery(connection);
		   		regionJsonArray = constructBinsJSON(region_bins_result);	
	   		 	out.writeBytes(regionJsonArray.toString());
	   		 	out.flush();
	   		 	out.close();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in registerUser in RegistrationHandler: "+ e);
		}	
	}
	
	/**
     * Method Name : insertUserToDB
     * Purpose : To insert user details to database
     * Parameters : connection, jsonObj
     * Return value : null 
     */
	public static void insertUserToDB(Connection connection, JSONObject jsonObj){
		try{
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
			Statement stmt = null;
			String insertSql = "INSERT INTO user_info(user_email, user_password, user_firstName, "
					+ "user_lastName, user_contactNo, user_area, bike_make, bike_modelNo, bike_frameNo,"
					+ "bike_color) VALUES ('"+ user_email + "','"  + user_password + "','"  + user_firstName
					+ "','"  + user_lastName + "','"  + user_contactNo + "','"  + user_area + "','"  + bike_make
					+ "','"  + bike_modelNo + "','"  + bike_frameNo + "','"  + bike_color+ "');";
			stmt = connection.createStatement();
			stmt.executeUpdate(insertSql);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in insertUserToDB in RegistrationHandler: "+ e);
		}
	}
	
	/**
     * Method Name : constructBinsJSON
     * Purpose : To get the bin results and construct them into JSONArray
     * Parameters : ResultSet regionBinsResult
     * Return value : JSONArray
     */
	@SuppressWarnings("unchecked")
	private static JSONArray constructBinsJSON(ResultSet regionBinsResult){
		JSONArray regionJsonArray = new JSONArray();
		try{
			regionBinsResult.beforeFirst();
			while(regionBinsResult.next()){
				String region_name = regionBinsResult.getString(2);
				Double region_cord_x = regionBinsResult.getDouble(3);
				Double region_cord_y = regionBinsResult.getDouble(4);
				Double region_weight = regionBinsResult.getDouble(5);
				String region_isSafe = regionBinsResult.getString(6);
				JSONObject regionJsonObject = new JSONObject();
				regionJsonObject.put("region_name", region_name);
				regionJsonObject.put("region_cord_x", region_cord_x);
				regionJsonObject.put("region_cord_y", region_cord_y);
				regionJsonObject.put("region_weight", region_weight);
				regionJsonObject.put("region_isSafe", region_isSafe);
				regionJsonArray.add(regionJsonObject);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in constructJSON in HeatMapHandler: "+ e);
		}
		return regionJsonArray;
	}
	
	/**
     * Method Name : getUserEmailfromDB
     * Purpose : To connect with database and fetch user Email
     * Parameters : Connection connection, String user_email
     * Return value : Result set (list of user emails)
     */
	public static ResultSet getUserEmailfromDB(Connection connection, String user_email){
		PreparedStatement preparedStatement = null;
		ResultSet registeredCheck = null;
		try{
			preparedStatement = connection.prepareStatement("select user_email from user_info where user_email='"
					+ user_email + "'");
			registeredCheck = preparedStatement.executeQuery();	
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in getUserEmailfromDB in RegistrationHandler: "+ e);
		}
		return registeredCheck;
	}
}
