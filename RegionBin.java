package ServerHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegionBin {
	
	/**
     * Method Name : executeBinsQuery
     * Purpose : To connect with database and fetch bins
     * Parameters : connection
     * Return value : Result set (Bins)
     */
	public static ResultSet executeBinsQuery(Connection connection){
		PreparedStatement preparedStatement = null;
		ResultSet region_bins_result = null;
		try{
			preparedStatement = connection.prepareStatement("select * from region_bins");
			region_bins_result = preparedStatement.executeQuery();
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in getBinsQuery in ReportHandler: "+ e);
		}
		return region_bins_result;
	}
}
