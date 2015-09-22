/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package bench.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import bench.BenchManager;
import bench.BenchQuery;
import bench.BenchQuery.Status;

/**
 * the Class DateBaseHandler
 */
public class DataBaseHandler{
	/**@see java.sql.Connection */
	private Connection conn = null;
	/**@see java.sql.Statement*/
	public Statement stmt; 
	
	private String database = "ontoask";
	private String user = "root";
	private String password = "kernel";
	private String host = "192.168.23.105";
	private int port = 3306;
	
	/**
	 * @rm.param host 
	 * @rm.param port 
	 * @rm.param database 
	 * @rm.param user
	 * @rm.param password
	 * @throws ClassNotFoundException 
	 */
	private boolean initConn(String host, int port, String database, String user, String password){
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			if(conn == null || conn.isClosed()){
				conn = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+ database
						+"?useUnicode=true&characterEncoding=utf8", user, password);
	
				stmt = conn.createStatement();
				stmt.setQueryTimeout(20);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 用户root，密码kernel
	 * 默认连192.168.23.105，port:3306
	 * @rm.param database 数据库
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	public DataBaseHandler(String database) throws SQLException, ClassNotFoundException{
		this("192.168.23.105", 3306, database, "root", "kernel");
	}
	
	/**
	 * @rm.param host 主机
	 * @rm.param port 端口
	 * @rm.param database 连接的数据库
	 * @rm.param user 数据库访问用户
	 * @rm.param password 数据库访问密码
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * 
	 */
	public DataBaseHandler(String host, int port, String database, String user, String password) 
			throws SQLException{
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		initConn(host, port, database, user, password);
	}
	
	/**
	 * 试着连接
	 * @return 连接是否成功
	 */
	public boolean tryConn(){
		return initConn(host, port, database, user, password);
	}
	/**
	 * 将该问句保存在数据库中
	 * @rm.param bq
	 * @return 保存成功与否
	 */
	public boolean save(BenchQuery bq){
		if(!tryConn()){
			return false;
		}
		if(bq.status != Status.DEL && !bq.isTreeCodeChanged() && !bq.isTypeChanged()){
			return true;
		}else if(bq.status == Status.STD || bq.status == Status.ERR){//本身已经在数据库中
			try {
				String sql = "delete from " +BenchManager.TABLE_NAME + " where qid='"+ bq.qid+"'";
				try{
					stmt.execute(sql);
				}catch (SQLException e){
				}
				
				sql = "insert into " +BenchManager.TABLE_NAME + " (qid, text, type, status, treecode, ptncode, date) " +
						"values('"+bq.qid+"', '"+bq.text.replaceAll("'", "''")+"', '"+ bq.type+ "', '"+ bq.status+"', "+bq.treeCode+", "+bq.specificPtnCode+", '"+bq.date+"')";
				stmt.execute(sql);
			    bq.oldTreeCode = bq.treeCode;
			    bq.oldType = bq.type ;
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}else if(bq.status == Status.DEL){
			String sql = "delete from " +BenchManager.TABLE_NAME + " where qid='"+ bq.qid+"'";
			try{
				stmt.execute(sql);
			}catch (SQLException e){
			}
		   try {
			   sql = "insert into " +BenchManager.TABLE_NAME + " (qid, text, type, status, treecode, ptncode, date) " +
						"values('"+bq.qid+"', '"+bq.text.replaceAll("'", "''")+"', '"+ bq.type+ "', '"+ bq.status+"', "+bq.treeCode+", "+bq.specificPtnCode+", '"+bq.date+"')";
				stmt.execute(sql);
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	/**
	 * close
	 */
	public void close(){
		try {
			if(stmt != null){
				stmt.close();
				stmt = null;
			}
			if(conn != null){
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			stmt = null;
			conn = null;
		}finally{
			stmt = null;
			conn = null;
		}
	}
}
