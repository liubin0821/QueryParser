package resouremanager.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseHandler {
	
	private Connection conn = null;
	/**@see java.sql.Statement*/
	public Statement stmt; 
	
	private String database = null;
	private String user = "root";
	private String password = "kernel";
	private String host = null;
	private int port = 3306;
	
//	private DataBase database ;

	/**
	 * @param args
	 */
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
	 * 用户root，密码kernel
	 * @rm.param database 数据库
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	public DataBaseHandler(String host, String database) throws SQLException, ClassNotFoundException{
		this(host, 3306, database, "root", "kernel");
	}
	
	public DataBaseHandler(DataBase db) throws SQLException{
		this(db.host,db.port,db.database,db.user,db.password) ;
	}
	
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
	 * 试着连接
	 * @return 连接是否成功
	 */
	public boolean tryConn(){
		return initConn(host, port, database, user, password);
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
	
	public static class DataBase{
		private final String database ;
		private final String user ;
		private final String password ;
		private final String host ;
		private final int port ;
		
		public DataBase(String database, String user,String password, String host ,int port){
			this.database = database ;
			this.user = user ;
			this.password = password ;
			this.host = host ;
			this.port = port ;
		}
		
		/**
		 * 因为user,password,port通常以不变的方式访问数据库
		 * 允许只输入database和host连接数据库
		 * @param database
		 */
		public DataBase(String database, String host){
			this(database, "root", "kernel", host, 3306) ;
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
