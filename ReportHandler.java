package ServerHandler;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ReportHandler {
	public static void reportLost(HttpServletRequest request, HttpServletResponse response){
		try{
		System.out.println("report handler reached");
		JSONParser parser = new JSONParser();
		String report_details = request.getParameter("report_details");
		Object obj = parser.parse(report_details);
		JSONObject jsonObj = (JSONObject) obj;
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
//		int lost_bin_number =  ((Long) jsonObj.get("lost_bin_number")).intValue();
		String coordinates = lost_location.substring(15);
		System.out.println(coordinates);
		String cors[] = coordinates.split(",");
		Double cor_x = Double.parseDouble(cors[0].substring(1));
		Double cor_y = Double.parseDouble(cors[1].substring(0,cors[1].length() - 2));

		System.out.println(Double.toString(cor_x));
		System.out.println(Double.toString(cor_y));

		int lost_bin_number = getBinNumber(cor_x,cor_y);
		System.out.println(lost_time_date);
		System.out.println(first_name);
		System.out.println(bike_model);

		Statement stmt = null;
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/velosafe","root","");

		String insertSql = "INSERT INTO report_record(lost_location, lost_bin_number,"
				+ "lost_time_date, lost_time_time, first_name, last_name, contact_number, area, bike_make, "
				+ "bike_model, bike_frame, bike_color) VALUES ('" + lost_location + "',"  + lost_bin_number  + ",'" + lost_time_date+ "','" +
				lost_time_time + "','" + first_name + "','" + last_name + "','" + contact_number + "','" +
				area + "','" + bike_make + "','" + bike_model + "','" + bike_frame + "','" + bike_color + "');";

		System.out.println(insertSql);

		stmt = connection.createStatement();
		stmt.executeUpdate(insertSql);

		String updateSql = "update region_bins set region_weight = region_weight + 1 where region_id = " + Integer.toString(lost_bin_number);
		stmt.executeUpdate(updateSql);


		Double totalWeight = 0.0;
		Double meanWeight = 0.0;
		int totalBins = 0;
		Double minWeight = 10000.0;
		Double maxWeight = 0.0;
		Double currentWeight = 0.0;

		PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement("select * from region_bins");
		ResultSet region_bins_result = preparedStatement.executeQuery();

		while(region_bins_result.next()){
			 currentWeight = region_bins_result.getDouble(5);
			 totalBins = totalBins + 1;
			 totalWeight = totalWeight + currentWeight;
			 if(currentWeight>maxWeight)
			 {
				 maxWeight = currentWeight;
			 }
			 if(currentWeight<minWeight)
			 {
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
			if(region_weight.intValue()==minWeight.intValue())
			 {
				regionColor =  "HUE_GREEN";
			 }
			 else if(region_weight.intValue()==maxWeight.intValue())
			 {
				 regionColor =  "HUE_RED";
			 }
			 else if(weightInterval==1.0)
			 {
				 regionColor =  "HUE_GREEN";
			 }
			 else if(weightInterval==2.0)
			 {
				 regionColor =  "HUE_ORANGE";
			 }
			 else if(weightInterval==3.0)
			 {
				 regionColor =  "HUE_RED";
			 }
			stmt = connection.createStatement();
			//stmt.executeUpdate(insertSql);

			String updateColor = "update region_bins set REGION_COLOR='" + regionColor + "' where REGION_ID=" + Integer.toString(region_id);
			stmt.executeUpdate(updateColor);

		}





		}	catch (Exception e){
			e.printStackTrace();
		}

	}

	private static int getBinNumber(double x, double y){
		try{
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/velosafe","root","123");
		PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement("select * from region_bins");
		ResultSet region_bins_result = preparedStatement.executeQuery();
		int size = 0;
		while(region_bins_result.next()){
			size = size + 1;
		}
		System.out.println(size);
		Double xx[] = new Double[size];
		Double yy[] = new Double[size];
		region_bins_result.beforeFirst();
//		Double x[];
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

		System.out.println(min_pos);
		System.out.println(min);
		return min_pos + 1;
		}
		catch(Exception e){

		}

		return 2;
	}


}
