package com.myhexin.qparser.servlet.op;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class OpDb {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(OpDb.class.getName());
	private static void initProp() {
    	InputStream ins = null;
    	try{
	    	ins = OpDb.class.getClassLoader().getResourceAsStream("DB/DB.properties");
	    	Properties prop = new Properties();
	    	prop.load(ins);
	    	mysql_driverClass = prop.getProperty("mysql.driverClass");
	    	mysql_jdbcUrl = prop.getProperty("mysql.jdbcUrl");
	    	mysql_username = prop.getProperty("mysql.user");
	    	mysql_password = prop.getProperty("mysql.password");
    	
    	}catch(Exception e) {
    		logger_.error("Load JDBC Properties failed :  " + e.getMessage());
    	}finally {
    		if(ins!=null) {
    			try {
					ins.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    	
    }
	
	 //测试用的，不要写到mybatis中去
    private static String mysql_driverClass =null;
	private static String mysql_jdbcUrl = null;
	private static String mysql_username = null;
	private static String mysql_password = null;

	public static Connection getConnection() throws Exception {
		if(mysql_driverClass==null || mysql_jdbcUrl==null || mysql_username==null || mysql_password==null) {
			initProp();
		}
		if(mysql_driverClass==null || mysql_jdbcUrl==null || mysql_username==null || mysql_password==null) {
			return null;
		}
		
		Class.forName(mysql_driverClass);
		Connection con = DriverManager.getConnection(mysql_jdbcUrl,mysql_username, mysql_password);
		return con;
	}
}
