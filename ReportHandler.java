package ServerHandler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Class Name : ReportHandler
 * Purpose : To receive the details of the theft report, and save the details to the database, as well as
 * 			 update the data in region_bins
 */
public class ReportHandler {
	
	/**
     * Method Name : reportLost
     * Purpose : To receive the details of the theft report, and save the details to the database, as well as
     * 			 update the data in region_bins
     * Parameters : HttpServletRequest request, HttpServletResponse response
     * Return value : Null
     */
	public static void reportLost(HttpServletRequest request, HttpServletResponse response){
		try{
			Connection connection = null;
			JSONParser parser = new JSONParser();
			String report_details = request.getParameter("report_details");
			Object obj = parser.parse(report_details);
			JSONObject jsonObj = (JSONObject) obj;
			DBConnection dbConnection = new DBConnection();
			connection = dbConnection.getConnection();
			int lost_bin_number = insertReportToDB(connection, jsonObj);
			updateRegionBin(connection, lost_bin_number);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in reportLost in ReportHandler: "+ e);
		}

	}

	/**
     * Method Name : updateRegionBin
     * Purpose : To add theft count to the region and to update the region color
     * Parameters : Connection connection, int lost_bin_number
     * Return value : Null
     */
	private static void updateRegionBin(Connection connection, int lost_bin_number) {
		Statement stmt = null;
		try{
			String updateSql = "update region_bins set region_weight = region_weight + 1 where region_id = " 
					+ Integer.toString(lost_bin_number);
			stmt = connection.createStatement();
			stmt.executeUpdate(updateSql);
			updateRegionColor(connection);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in updateRegionBin in ReportHandler: "+ e);
		}
	}
	
	/**
     * Method Name : updateRegionColor
     * Purpose : To update the region color based on the theft count(weight)
     * Parameters : Connection connection
     * Return value : Null
     */	
	private static void updateRegionColor(Connection connection) {
		Double totalWeight = 0.0;
		Double meanWeight = 0.0;
		int totalBins = 0;
		Double minWeight = 10000.0;
		Double maxWeight = 0.0;
		Double currentWeight = 0.0;
		Statement stmt = null;
		try{
			ResultSet region_bins_result = RegionBin.executeBinsQuery(connection);
			while(region_bins_result.next()){
				currentWeight = region_bins_result.getDouble(5);
				totalBins = totalBins + 1;
				totalWeight = totalWeight + currentWeight;
				if(currentWeight>maxWeight){
					maxWeight = currentWeight;
				}
				if(currentWeight<minWeight){
					minWeight = currentWeight;
				}
			}
			meanWeight = (maxWeight-minWeight)/3;
			region_bins_result.beforeFirst();
			while(region_bins_result.next()){
				int region_id = region_bins_result.getInt(1);
				String regionColor = null;
				Double region_weight = region_bins_result.getDouble(5);
				Double weightInterval = Math.ceil(((region_weight-minWeight)/meanWeight));
				regionColor = getColor(region_weight, minWeight, maxWeight, weightInterval);
				stmt = connection.createStatement();				
				String updateColor = "update region_bins set REGION_COLOR='" + regionColor + "' where REGION_ID=" 
						+ Integer.toString(region_id);
				stmt.executeUpdate(updateColor);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in updateRegionColor in ReportHandler: "+ e);
		}	
	}

	/**
     * Method Name : getColor
     * Purpose : To decide the color for the region 
     * Parameters : Double region_weight, Double minWeight, Double maxWeight, Double weightInterval
     * Return value : Region color
     */
	private static String getColor(Double region_weight, Double minWeight, Double maxWeight, Double weightInterval) {
		String regionColor= null;
		if(region_weight.intValue()==minWeight.intValue()){
			regionColor = "HUE_GREEN";
		}
		else if(region_weight.intValue()==maxWeight.intValue()){
			regionColor = "HUE_RED";
		}
		else if(weightInterval==1.0){
			regionColor = "HUE_GREEN";  
		}
		else if(weightInterval==2.0){
			regionColor = "HUE_ORANGE";
		}
		else if(weightInterval==3.0){
			regionColor =  "HUE_RED";
		}
		return regionColor;
	}

	/**
     * Method Name : getBinNumber
     * Purpose : To receive the details of the theft report, and save the details to the database, as well as
     * 			 update the data in region_bins
     * Parameters : double x, double y (coordinates of the theft location), Connection connection
     * Return value : bin number
	 * @param connection 
     */
	private static int getBinNumber(double x, double y, Connection connection){
		try{
			ResultSet region_bins_result = RegionBin.executeBinsQuery(connection);
			int size = 0;
			while(region_bins_result.next()){
				size = size + 1;
			}
			Double xx[] = new Double[size];
			Double yy[] = new Double[size];
			region_bins_result.beforeFirst();
			int i = 0;
			while(region_bins_result.next()){
				xx[i] = region_bins_result.getDouble(3);
				yy[i] = region_bins_result.getDouble(4);
				i = i + 1;
			}
			Double disx_sqr[] = new Double[20];
			Double disy_sqr[] = new Double[20];
			for(i=0;i<size;i++){
				disx_sqr[i] = Math.pow((x - xx[i]), 2);
				disy_sqr[i] = Math.pow((y - yy[i]), 2);
			}
			double min = disx_sqr[0] + disy_sqr[0];
			int min_pos = 0;
			for (i=0;i<size;i++){
				Double temp = disx_sqr[i] + disy_sqr[i];
				if(temp<min){
					min = temp;
					min_pos = i;
				}
			}
			return min_pos + 1;
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in getBinNumber in ReportHandler: "+ e);
		}
		return 0;
	}
	
	/**
     * Method Name : insertReportToDB
     * Purpose : To insert report details to database
     * Parameters : connection, jsonObj
     * Return value : lost bin number 
     */
	public static int insertReportToDB(Connection connection, JSONObject jsonObj){
		int lost_bin_number = 0;
		try{
			String lost_location = (String) jsonObj.get("lost_location");
			String lost_time_date = (String) jsonObj.get("lost_time_date");
			String lost_time_time = (String) jsonObj.get("lost_time_time");
			String first_name = (String) jsonObj.get("first_name");
			String last_name = (String) jsonObj.get("last_name");
			String contact_number = (String) jsonObj.get("contact_number");
			String area = (String) jsonObj.get("area");
			String bike_make = (String) jsonObj.get("bike_make");
			String bike_model = (String) jsonObj.get("bike_model");
			String bike_frame = (String) jsonObj.get("bike_frame");
			String bike_color = (String) jsonObj.get("bike_color");
			String coordinates = lost_location.substring(15);
			String cors[] = coordinates.split(",");
			Double cor_x = Double.parseDouble(cors[0].substring(1));
			Double cor_y = Double.parseDouble(cors[1].substring(0,cors[1].length() - 2));
			lost_bin_number = getBinNumber(cor_x,cor_y,connection);
			Statement stmt = null;
			String insertSql = "INSERT INTO report_record(lost_location, lost_bin_number,"
					+ "lost_time_date, lost_time_time, first_name, last_name, contact_number, area, bike_make, "
					+ "bike_model, bike_frame, bike_color) VALUES ('" + lost_location + "',"  + lost_bin_number  + ",'"
					+ lost_time_date+ "','" + lost_time_time + "','" + first_name + "','" + last_name + "','" 
					+ contact_number + "','" + area + "','" + bike_make + "','" + bike_model + "','" + bike_frame 
					+ "','" + bike_color + "');";
			stmt = connection.createStatement();
			stmt.executeUpdate(insertSql);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in insertReportToDB in ReportHandler: "+ e);
		}
		return lost_bin_number;
	}
}




