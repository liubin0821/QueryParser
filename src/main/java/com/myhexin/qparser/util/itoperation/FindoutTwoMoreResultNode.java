package com.myhexin.qparser.util.itoperation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseParser;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;


public class FindoutTwoMoreResultNode {
	static PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
	static {
		   parser.setSplitWords("_&_");
	 }
	static class  TwoMoreResult{
		String id;
		String chunk;
		String result;
	}
	
	//看new_query表里总共有多少行
	private  long count( ) {
		long a = 0;
		String sql = "SELECT count(*)  FROM  new_query ";
		Connection conn = getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = (Statement) conn.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				a = (Long) resultSet.getObject(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeAll(conn, statement, resultSet);
		}
		return a;
		
	}
	
	//取到问句
	private  HashMap<String,String> getChunkQuery(int start, int size) {
		Map<String,String>  list=new  HashMap<String,String>();
		String sql = "SELECT  chunk,uid  FROM  new_query  LIMIT  "+ start + "," + size + "";
		Connection conn = getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = (Statement) conn.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String res=resultSet.getString("chunk");
				String uid=resultSet.getString("uid");
				list.put(uid, res);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeAll(conn, statement, resultSet);
		}
		return (HashMap<String, String>) list;
	}
	
	//解析
	private  ParseResult parse(String querys) {
			Query query = new Query(querys.toLowerCase());
			query.setType(Type.ALL);
			ParserAnnotation annotation = new ParserAnnotation();
	    	annotation.setQueryText(query.text);
	    	annotation.setQueryType(query.getType());
	    	annotation.setQuery(query);
	    	return parser.parse(annotation);
		}	
	
	//查看解析后的结果有没有多个解释
	private String getResult(List<String> lists){
	     if(lists.get(0).contains("_&_")){
	    	 return lists.get(0);
	     }
		 return null;
	}
	
	//写入到文件
	public void save(List<TwoMoreResult> result){
		FileWriter fileWriter=null;
		BufferedWriter write=null;
		try {
			fileWriter=new FileWriter(WRITEPATH);
		    write=new BufferedWriter(fileWriter);
			for(int i=0;i<result.size();i++){
				write.write(result.get(i).id+"_##_"+result.get(i).chunk+"_##_"+result.get(i).result);
				write.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				write.flush();
				fileWriter.flush();
				write.close();
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
	
 	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length<4) {
			System.out.println("url user password writepaths");
			System.exit(1);
		}
		CONN_URL = args[0];
		DATABASE_USERNAME = args[1];
		DATABASE_PASSWORD = args[2];
		WRITEPATH=args[4];
		
		System.out.println(CONN_URL );
		System.out.println(DATABASE_USERNAME );
		System.out.println(DATABASE_PASSWORD );
		System.out.println(WRITEPATH );
		
		FindoutTwoMoreResultNode fsis=new FindoutTwoMoreResultNode();
		ApplicationContextHelper.loadApplicationContext();
		long count = fsis.count();
		int step = 1000;
		int start = 0;
		int total = 0;
		System.out.println("开始。。。。。。。。。");
		while(total<count){
			HashMap<String,String> map = fsis.getChunkQuery(start, step);
			Iterator it=map.entrySet().iterator();
			List<TwoMoreResult> nuList = new ArrayList<TwoMoreResult>();
			while(it.hasNext()) {
				Entry ent=(Entry)it.next();
				String chunks=(String)ent.getValue();
				ParseResult pr= fsis.parse(chunks);       //解析
				List<String> list=pr.standardQueries;  
				String result=fsis.getResult(list);   //得到含_&_结果的result
				if(result!=null){
					TwoMoreResult res=new TwoMoreResult();
					res.id=(String) ent.getKey();
					res.result=result;
					res.chunk=chunks;
					nuList.add(res);
				}
			}
			fsis.save(nuList);
			start += map.size();
			total += map.size();
	        System.out.println("----------第"+total+"/" + count + "条----------------结束");
		}
		System.out.println("结束。。。。。。。。。");
	}
	
	private static String DATABASE_USERNAME="qnateam";
	private static String CLASS_DRIVER="com.mysql.jdbc.Driver";
	private static String DATABASE_PASSWORD="qnateam";
	private static String CONN_URL = "jdbc:mysql://192.168.23.52:3306/configFile?useUnicode=true&characterEncoding=utf-8";
	private static String WRITEPATH="c:\\dzc.txt";
	
	
	//连接
	private  Connection getConnection() {
		
		try {
			Class.forName(CLASS_DRIVER);
			return (Connection) DriverManager.getConnection(CONN_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
		} catch (Exception e) {
			System.out.println("Load JDBC Properties failed :  "+ e.getMessage());
			e.printStackTrace();
		} 
		  return null;
		
	}
	
	//关闭
	private   void closeAll(Connection connection, Statement statement, ResultSet resultSet) {
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
