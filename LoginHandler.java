package ServerHandler;

import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Class Name : LoginHanler
 * Purpose : To receive user's username and password, and return the login result to the user. If the username and  
 * 			 the password match the data in the database, return the 'login successful' message as well as the latest
 * 			 heat map information. If the username is in the database, but the password does not match the password
 *			 in the database, return username and password "not match" information. If the username is not in the database,
 *			 return the username "not registered" information.
 */
public class LoginHandler {
	
	/**
     * Method Name : loginUser
     * Purpose : To receive user's username and password, and return the login result to the user.
     * Parameters : HttpServletRequest request, HttpServletResponse response
     * Return value : Null
     */
	public static void loginUser(HttpServletRequest request, HttpServletResponse response){
		try{
			Connection connection = null;
			String msg = null;
			JSONParser parser = new JSONParser();
			String login_details = request.getParameter("login_details");
			Object obj = parser.parse(login_details);
			JSONObject jsonObj = (JSONObject) obj;
			String user_email = (String) jsonObj.get("email");
			String user_password = (String) jsonObj.get("password");
			DBConnection dbConnection = new DBConnection();
			connection = dbConnection.getConnectionForRead();
			ResultSet passwordCheck = getUserPasswordfromDB(connection, user_email);
			passwordCheck.beforeFirst();
			DataOutputStream out = new DataOutputStream(response.getOutputStream());
			msg = validatePassword(connection, passwordCheck, user_password);
			out.writeBytes(msg);
			out.flush();
			out.close();
			
		}	
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in loginUser in LoginHandler: "+ e);
		}
	}
	
	/**
     * Method Name : validatePassword
     * Purpose : To compare the password entered by the user with the password from DB
     * Parameters : Connection connection, ResultSet passwordCheck, String user_password
     * Return value : String msg
     */
	private static String validatePassword(Connection connection, ResultSet passwordCheck, String user_password) {
		JSONArray regionJsonArray = new JSONArray();
		String msg = null;
		try{
			if(passwordCheck.next()){
				String originpassword = passwordCheck.getString(1);
				if (originpassword.equals(user_password)){
					ResultSet region_bins_result = RegionBin.executeBinsQuery(connection);
			   		regionJsonArray = constructBinsJSON(region_bins_result);	
					msg = "login successful&" + regionJsonArray.toString();
				}
				else{
					msg = "not match&";
				}
			}
			else{
				msg = "not registerd&";
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in validatePassword in LoginHandler: "+ e);
		}
		return msg;
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
			System.out.println("Exception in constructJSON in LoginHandler: "+ e);
		}
		return regionJsonArray;
	}
	
	/**
     * Method Name : getUserPasswordfromDB
     * Purpose : To connect with database and fetch user password
     * Parameters : Connection connection, String user_email
     * Return value : Result set (list of user passwords)
     */
	public static ResultSet getUserPasswordfromDB(Connection connection, String user_email){
		PreparedStatement preparedStatement = null;
		ResultSet passwordCheck = null;
		try{
			preparedStatement = connection.prepareStatement("select user_password from user_info where user_email='" + user_email + "'");
			passwordCheck = preparedStatement.executeQuery();	
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in getUserPasswordfromDB in LoginHandler: "+ e);
		}
		return passwordCheck;
	}
}

