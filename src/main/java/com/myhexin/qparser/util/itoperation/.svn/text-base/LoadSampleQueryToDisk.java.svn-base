package com.myhexin.qparser.util.itoperation;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;


public class LoadSampleQueryToDisk {
	
	private static Long count(){
		 String sql="select count(id) from sampling_queries";
		 Connection conn=getConnection();
		 ResultSet set=null;
		 Statement stmt=null;
		 long c = 0;
		 try {
			stmt=(Statement) conn.createStatement();
			set=stmt.executeQuery(sql);
			if(set.next()){
				c = set.getLong(1);
			}
	     } catch (SQLException e) {
			  e.printStackTrace();
		 }finally{
			closeAll(conn,stmt,set);
		 }
		 return c;
		} 
	
	
	public static void main(String[] args) throws Exception {
		saveQueryToDisk(args[0]);
	}
	
	private static void saveQueryToDisk(String fileName) throws Exception{
		FileOutputStream fos = new FileOutputStream(fileName);
		long count = count();
		PrintWriter pr = new PrintWriter(fos);
		 String sql="select query from sampling_queries";
		 Connection conn=getConnection();
		 ResultSet set=null;
		 Statement stmt=null;
		 try {
			stmt=(Statement) conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);
			set=stmt.executeQuery(sql);
			long i=0;
			while(set.next()){
				String s = set.getString(1);
				pr.println(s);
				if(i++%10000 ==0) {
					System.out.println(i + "/" + count  + " saved");
					pr.flush();
				}
			}
			System.out.println(i + "/" + count  + " saved");
	     } catch (SQLException e) {
			  e.printStackTrace();
		 }finally{
			closeAll(conn,stmt,set);
		 }
		 fos.close();
		} 
   
   private static Connection getConnection(){
	    try {
			DataSource ds = ApplicationContextHelper.getBean("dataSource");
			return ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    return null; 
	   
   }
   
   private static void closeAll(Connection connection, Statement statement, ResultSet resultSet){
	    try {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
			if (statement != null && !statement.isClosed()){
				statement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		 } catch (SQLException e) {
			e.printStackTrace();
		}
   }
}
