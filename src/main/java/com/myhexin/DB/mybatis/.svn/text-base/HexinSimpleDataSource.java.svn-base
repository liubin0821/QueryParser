package com.myhexin.DB.mybatis;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.myhexin.qparser.except.ExceptionUtil;


public class HexinSimpleDataSource implements DataSource {
	public static final org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger("HexinSimpleDataSource.class");
	private String driverClass;
	private String jdbcUrl;
	private String user;
	private String password;
	
	

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public void setUser(String user) {
		this.user = user;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		try{
			return getConnection(user, password);
		}catch(Exception e){
			logger_.error(ExceptionUtil.getStackTrace(e));
		}
		return null;
	}

	@Override
	public Connection getConnection(String username, String password1)throws SQLException {
		try{
			Class.forName(driverClass);
			return DriverManager.getConnection(jdbcUrl, username, password1);
		}catch(Exception e){
			logger_.error(ExceptionUtil.getStackTrace(e));
		}
		return null;
	}

	public void close() {
		logger_.info("DataSource.closed");
	}
}
