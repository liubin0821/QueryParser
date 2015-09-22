package com.myhexin.qparser.util.itoperation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;



public class FindoutSyntactIdSemanticId {
	private static org.slf4j.Logger logger_=org.slf4j.LoggerFactory.getLogger(FindoutSyntactIdSemanticId.class.getName());
	private static final String className = "com.mysql.jdbc.Driver";
	private static Connection conn = null;//getConnection();
	
	static PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
	static {
		   parser.setSplitWords("_&_");
	 }
	
	static class Id{
		String uid;
		String syntacticId;
		String semanticId;
	}
	
	private static  void closeAll(Connection connection, Statement statement, ResultSet resultSet) {
		try {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
			if (statement != null && !statement.isClosed()){
				statement.close();
			}
			/*if (connection != null && !connection.isClosed()) {
				connection.close();
			}*/
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	static class RunParser extends Thread {
		private String fileName;

		
		
		
		public RunParser(String name) {
			this.fileName = name;
		}
		
		//解析
		private  ParseResult parse(String querys) {
				Query query = new Query(querys.toLowerCase());
				query.setType(Type.ALL);
				ParserAnnotation annotation = new ParserAnnotation();
		    	annotation.setQueryText( query.text);
		    	annotation.setQueryType(query.getType());
		    	annotation.setQuery(query);
		    	return parser.parse(annotation);
			}	
		
		
		
		public void run() {
			try{
			PrintWriter out = new PrintWriter(new FileOutputStream("completed_" + fileName));
			
			int total = 0;
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String s = null;
			while( (s=br.readLine())!=null) {
				int idx = s.indexOf(SEP);
				if(idx>0) {
					String uid = s.substring(0, idx);
					String q = s.substring(idx+ SEP.length());
					ParseResult pr= parse(q);
					String syntacticId = pr.getSyntacitcIds();
					String semanticId = pr.getSemanticIds();
					out.println(uid + SEP + syntacticId + SEP + semanticId);
					total++;
				}
			}
			System.out.println("ALL Parse completed : " + total + "rows");
			out.close();
			br.close();
			
			//更新数据库
			br = new BufferedReader(new InputStreamReader(new FileInputStream("completed_" + fileName)));
			s = null;
			int stotal = 0;
			List<Id> idList = new ArrayList<Id>();
			while( (s=br.readLine())!=null) {
				int idx1 = s.indexOf(SEP);
				int idx2 = s.indexOf(SEP, idx1+SEP.length());
				if(idx1>0 && idx2>0 && idx2>idx1) {
					String uid = s.substring(0, idx1);
					String sId = s.substring(idx1+SEP.length(), idx2);
					String seId = s.substring(idx2+SEP.length());
					Id id = new Id();
					id.syntacticId = sId;
					id.semanticId = seId;
					id.uid=uid;
					idList.add(id);
					stotal++;
					if(idList.size()>batchSize) {
						save(idList);
						idList.clear();
						System.out.println("Update : " + stotal + "/" +total + "rows");
					}
				}
			}
			System.out.println("Update : " + stotal + "/" +total + "rows");
			}catch(Exception e){e.printStackTrace();}
		}
		

		
		//batch update database
		private  void save(List<Id> idList) {
			
			Connection conn1 = getConnection(true);
			try {
				conn1.setAutoCommit(false);
				PreparedStatement pstmt = conn1.prepareStatement("update new_query set syntacticIds=?,semanticIds=? where uid=? ");
				for(Id rt : idList) {
					pstmt.setString(1, rt.syntacticId);
					pstmt.setString(2, rt.semanticId);
					pstmt.setString(3, rt.uid);
					pstmt.addBatch();
				}
				pstmt.executeBatch();
				conn1.commit();
				//conn.setAutoCommit(true);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					conn1.setAutoCommit(true);
					conn1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
			
		}
	}
	
	
	//看new_query表里总共有多少行
	/*private  long count( ) {
		long a = 0;
		String sql = "SELECT count(*)  FROM  new_query ";
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
		
	}*/
	
	private final static String chunk_file = "_chunk_query.txt";
	/*private final static String chunk_file_completed = "chunk_query_completed.txt";
	//取到问句
	private  HashMap<String,String> getChunkQuery(int start, int size) throws Exception {
		
		Map<String,String>  list=new  HashMap<String,String>();
		String sql = "SELECT  chunk,uid  FROM  new_query where syntacticIds=''  LIMIT " + size ;
		//Connection conn = getConnection();
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
	}*/
	
	
	
	private final static String SEP = "_#&#_";
	private  List<String> saveChunkQuery() throws Exception {
		List<String> fileaNames = new ArrayList<String>();
		int x = 1;
		String name = "A" + x + chunk_file;
		fileaNames.add(name);
		
		PrintWriter pr = new PrintWriter(new FileOutputStream(name));
		//Map<String,String>  list=new  HashMap<String,String>();
		String sql = "SELECT  chunk,uid  FROM  new_query";
		//Connection conn = getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = (Statement) conn.createStatement();
			resultSet = statement.executeQuery(sql);
			
			int total =0;
			while (resultSet.next()) {
				String res=resultSet.getString("chunk");
				String uid=resultSet.getString("uid");
				//list.put(uid, res);
				pr.println(uid + SEP + res);
				
				if(total++ > 400000) {
					pr.close();
					total = 0;
					name = "A" + (x++) + chunk_file;
					fileaNames.add(name);
					pr = new PrintWriter(new FileOutputStream(name));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeAll(conn, statement, resultSet);
			pr.close();
		}
		//return (HashMap<String, String>) list;
		System.out.println("save chunk file completed: " + fileaNames);
		return fileaNames;
	}
	

	
	
	
	/*public static void parseByDb(FindoutSyntactIdSemanticId fsis, int batchSize) throws Exception {
		long count = fsis.count();
		//int step = 1000;
		int start = 0;
		int total = 0;
		System.out.println("开始。。。。。。。。。");
		while(total<count){
			HashMap<String,String> map = fsis.getChunkQuery(start, batchSize);
			List<Id> idList = new ArrayList<Id>();
			Iterator it=map.entrySet().iterator();
			while(it.hasNext()) {
				Entry ent=(Entry)it.next();
				ParseResult pr= fsis.parse((String)ent.getValue());
				//System.out.println("dddddd"+pr);
				Id id = new Id();
				id.syntacticId = fsis.getSyntacitcIds(pr);
				//System.out.println("dddd"+fsis.getSyntacitcIds(pr));
				id.semanticId = fsis.getSemanticIds(pr);
				//System.out.println("ddddaa"+fsis.getSemanticIds(pr));
				id.uid=(String) ent.getKey();
				idList.add(id);
			}
			fsis.save(idList);
			//start += map.size();
			total += map.size();
	        System.out.println("----------第"+total+"/" + count + "条----------------结束");
	        logger_.info("----------第"+total+"/" + count + "条----------------结束");
		}
		System.out.println("结束。。。。。。。。。");
		logger_.info("结束。。。。。。。。。");
	}*/
	
	public void parseByFile() throws Exception {
		List<String> fileNames = saveChunkQuery();
		System.out.println("开始解析");
		for(String s : fileNames) {
			RunParser p = new RunParser(s);
			p.start();
		}
		/*PrintWriter out = new PrintWriter(new FileOutputStream(chunk_file_completed));
		
		int total = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(chunk_file)));
		String s = null;
		while( (s=br.readLine())!=null) {
			int idx = s.indexOf(SEP);
			if(idx>0) {
				String uid = s.substring(0, idx);
				String q = s.substring(idx+ SEP.length());
				ParseResult pr= parse(q);
				String syntacticId = getSyntacitcIds(pr);
				String semanticId = getSemanticIds(pr);
				out.println(uid + SEP + syntacticId + SEP + semanticId);
				total++;
			}
		}
		System.out.println("ALL Parse completed : " + total + "rows");
		out.close();
		br.close();*/
		
		
	}
	
 	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if(args.length<4) {
			System.out.println("url user password batchSize");
			System.exit(1);
		}
		
		CONN_URL = args[0];
		DATABASE_USERNAME = args[1];
		DATABASE_PASSWORD = args[2];
		batchSize = Integer.parseInt(args[3]);
		
		System.out.println(CONN_URL );
		System.out.println(DATABASE_USERNAME );
		System.out.println(DATABASE_PASSWORD );
		conn = getConnection(true);
		// TODO Auto-generated method stub
		FindoutSyntactIdSemanticId fsis=new FindoutSyntactIdSemanticId();
		ApplicationContextHelper.loadApplicationContext();
		fsis.parseByFile();
		
		//System.exit(1);
	}
	
	private static String DATABASE_USERNAME="wc";
	private static String CLASS_DRIVER=className;
	private static String DATABASE_PASSWORD="123456";
	private static String CONN_URL = "jdbc:mysql://192.168.207.8:3306/new_chunkquery?useUnicode=true&characterEncoding=utf-8";
	private static int batchSize;
	private static Connection getConnection(boolean createNew) {
		if(createNew) {
			return getC();
		}else{
			if(conn==null) {
				conn =  getC();
				return conn;
			}
		}
		 return null;
	}
	
	
	private static Connection getC() {
		try {
			Class.forName(CLASS_DRIVER);
			return DriverManager.getConnection(CONN_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
		} catch (Exception e) {
			System.out.println("Load JDBC Properties failed :  "+ e.getMessage());
			e.printStackTrace();
		} 
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
