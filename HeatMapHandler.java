package ServerHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Class Name : HeatMapHandler
 * Purpose : To receive user's heat map request and send the related information to user
 */
public class HeatMapHandler {
	
	/**
     * Method Name : getBins
     * Purpose : To return the information about heat map back to the user
     * Parameters : HttpServletRequest request, HttpServletResponse response
     * Return value : Null
     */
	public static void getBins(HttpServletRequest request, HttpServletResponse response){
		try{
			DataOutputStream out = new DataOutputStream(response.getOutputStream ());
			Connection connection = null;
			response.setStatus(HttpServletResponse.SC_OK);
			DBConnection dbConnection = new DBConnection();
			connection = dbConnection.getConnection();
			ResultSet regionBinsResult = RegionBin.executeBinsQuery(connection);	
			JSONArray regionJsonArray = new JSONArray();
			regionJsonArray = constructBinsJSON(regionBinsResult);
			out.writeBytes(regionJsonArray.toString());
			out.flush();
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in getBins in HeatMapHandler: "+ e);
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
}
