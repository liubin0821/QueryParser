package com.myhexin.qparser.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/*
java -cp mysql-connector-java-5.1.18.jar:. SamplingQueryUtil com.mysql.jdbc.Driver  jdbc:mysql://192.168.23.52:3306/configFile?zeroDateTimeBehavior=convertToNull qnateam qnateam ~/test/test.txt
java -cp mysql-connector-java-5.1.18.jar:. SamplingQueryUtil com.mysql.jdbc.Driver  jdbc:mysql://192.168.201.147:3306/configFile?zeroDateTimeBehavior=convertToNull qnateam qnateam ~/test/test.txt


*/
public class SamplingQueryUtil {

	
	private static void save(Connection con, List<String> list) {
		try{

			if(con==null) return;
			PreparedStatement pstmt = con.prepareStatement("INSERT INTO configFile.sampling_queries (query, STATUS) VALUES (?,0)");
			for(String rt : list) {
				pstmt.setString(1, rt);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con!=null) {
				try {
					con.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static Connection getConnection(String  mysql_driverClass, String mysql_jdbcUrl, String mysql_username, String mysql_password) throws Exception {
		if (mysql_driverClass == null || mysql_jdbcUrl == null
				|| mysql_username == null || mysql_password == null) { return null; }

		Class.forName(mysql_driverClass);
		Connection con = DriverManager.getConnection(mysql_jdbcUrl,
				mysql_username, mysql_password);
		return con;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		if(args.length<5) {
			System.out.println("driver url user password fileName");
			System.exit(1);
		}
		
		String driver = args[0];
		String url = args[1];
		String user = args[2];
		String password = args[3];
		String fileName = args[4];
		
		int batchSize = 1000;
		if(args.length>5) {
			batchSize = Integer.parseInt(args[5]);
		}
		
		System.out.println("driver = " + driver);
		System.out.println("url = " + url);
		System.out.println("user = " + user);
		System.out.println("password = " + password);
		System.out.println("fileName = " + fileName);
		System.out.println("batchSize = " + batchSize);
		
		Connection con =  getConnection(driver, url, user, password);
		con.setAutoCommit(false);
		List<String> list = new ArrayList<String>(1000);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String s = null;
		long count = 0;
		while( (s=br.readLine()) !=null) {
			list.add(s);
			count ++ ;
			if(list.size()>=batchSize) {
				save(con, list);
				list.clear();
				System.out.println( count + " queries saved. ");
			}
		}
		
		br.close();
		con.close();

	}

}
