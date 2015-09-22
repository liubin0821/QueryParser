package com.myhexin.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.phrase.PhraseParser;

public class RunAllQuery {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(RunAllQuery.class.getName());
	
	private static void runAllQuery(String fileName,Map<String,String> syntacticMap){
		File file = new File(fileName);
        BufferedReader reader = null;
        Map<String,Integer> syntacticItemMap = new HashMap<String, Integer>();
        for(Entry<String, String> entry : syntacticMap.entrySet()){
        	syntacticItemMap.put(entry.getKey(), 0);
        }
        try {
            reader = new BufferedReader(new FileReader(file));
            Connection conn = Util.getConnection();
            if(conn == null){
            	logger_.error("connnect to db failed");
            	return;
            }
//          filtSyntacticItemMap(conn,syntacticMap,syntacticItemMap);
            int id=0;
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	id++;
//            	if(id<1229000)	continue;
            	if(syntacticItemMap.size()==0){
            		logger_.info("run all query finish!");
            		break;
            	}
            	if(conn == null)
            		conn = Util.getConnection();
            	String[] strs = tempString.split("_###_");
            	if(strs.length==3 && strs[2]!=null && strs[2].length()>0){
//            		System.out.println(id+"\t"+"q:"+strs[2]);
            		if(id%1000 == 0)
            			logger_.info("id:"+id+"\tsyntacticItemMap size:"+syntacticItemMap.size());
            		try{
            			runQueryTimeLimit(strs[2],syntacticMap,syntacticItemMap,conn);
            		}catch(Exception e){
            			logger_.error("run query exception:"+e.toString()+", query:"+strs[2]);
            		}
            	}
            }
            reader.close();
        } catch (Exception e1) {
        	logger_.error("exception:"+e1.toString());
		} finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                	logger_.error("exception:"+e1.toString());
                }
            }
        }
	}
	
	@SuppressWarnings("deprecation")
	private static void runQueryTimeLimit(String query,Map<String,String> syntacticMap,
			Map<String,Integer> syntacticItemMap,Connection conn){
		try {   
//			long startTime = System.currentTimeMillis();
			MyThread myThread = new MyThread(query,syntacticMap,syntacticItemMap,conn);
			Thread thread = new Thread(myThread);
			thread.start();
            thread.join(30*1000);
            if(thread.isAlive()){
            	thread.interrupt();
            }
            thread.stop();
//          long endTime = System.currentTimeMillis();
//          System.out.println("finish:"+(endTime-startTime)/1000+"s"+"\tq:"+query);
        } catch (InterruptedException e) {   
            logger_.error("run query limit time exception:"+e.toString());
        }
	}
	
	public static void main(String[] args) {
		runAllQuery(Util.QUERY_FILE,getSyntacticMap());
	}
	
	public static Map<String,String> getSyntacticMap(){
		try {
			return getSyntacticMapFromXml(readFile(Util.SYN_FILE));
		} catch (Exception e) {
			logger_.error("get syn from file exception:"+e.toString());
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String,String> getSyntacticMapFromXml(String xml) throws Exception {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
		Element root = doc.getRootElement();
		List<Element> childNodes = root.elements();
		Map<String,String> syntacticMap = new HashMap<String, String>();
		for (Element e : childNodes) {	//SyntacticPattern
			String id = e.attributeValue("id");
			String description = e.elementText("Description");
			Element semanticBind = e.element("SemanticBind");
			List<Element> semanticBindNodes = semanticBind.elements();
			for(Element node : semanticBindNodes){
				if(node.getName().equals("SemanticBindTo"))
					id = id + "-" + node.attributeValue("id");
			}
			if(syntacticMap.containsKey(id))
				logger_.error("id duplicated:"+id);
			else
				syntacticMap.put(id, description);
		}
		return syntacticMap;
	}
	
	private static String readFile(String fileName){
		StringBuilder str = new StringBuilder();
		File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null)
            	str.append(tempString);
            reader.close();
        } catch (IOException e) {
            logger_.error("read file exception:"+e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                	logger_.error("close file exception:"+e1.toString());
                }
            }
        }
		return str.toString();
	}
	
	/*public static void filtSyntacticItemMap(Connection conn,Map<String,String> syntacticMap,Map<String, Integer> syntacticItemMap) throws SQLException {
		String sql = "SELECT COUNT(*) num,syAndSmId FROM mytestdb.`syntacticdb2` GROUP BY syAndSmId ORDER BY num DESC;";
		PreparedStatement pst = conn.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		while(rs.next()){
			String id = rs.getString("syAndSmId");
			int num = rs.getInt("num");
			if(num >= 10){
				syntacticMap.remove(id);
				syntacticItemMap.remove(id);
			}else{
				syntacticItemMap.put(id, num);
			}
		}
		rs.close();
		pst.close();
		System.out.println("map size:"+syntacticMap.size());
	}*/

}

class MyThread implements Runnable{
	
	String query;
	Map<String,String> syntacticMap;
	Map<String,Integer> syntacticItemMap;
	Connection conn;
	String sql;
	
	public MyThread(String query,Map<String,String> syntacticMap,
			Map<String,Integer> syntacticItemMap,Connection conn){
		this.query = query;
		this.syntacticMap = syntacticMap;
		this.syntacticItemMap = syntacticItemMap;
		this.conn = conn;
	}

	@Override
	public void run() {
		runQuery(query, Util.parser, syntacticMap, syntacticItemMap, conn);
	}
	
	@SuppressWarnings("deprecation")
	public void runQuery(String query,PhraseParser parser,Map<String,String> syntacticMap,
			Map<String,Integer> syntacticItemMap,Connection conn){
		Query q = new Query(query.toLowerCase());
		ParseResult pr = parser.parse(q);
		if(pr == null)
			return;
		int size = pr.standardQueriesScore.size();
		for(int i=0;i<size;i++){
			int score = pr.standardQueriesScore.get(i);
			if(score != 100)	continue;
			List<String> syn = pr.standardQueriesSyntacticSemanticIds.get(i);	//句式
//			if(syn.size()==1 && !syn.get(0).contains("STR_INSTANCE") && !syn.get(0).contains("FREE_VAL")){
			for(int j=0;j<syn.size();j++){
				String synStr = syn.get(j);
				synStr = synStr.replace("|", "-");
				synStr = synStr.replace("&", "-");
				if(!syntacticItemMap.containsKey(synStr))
					continue;
				String result = pr.standardQueries.get(i);
				SyntacticItem synItem = new SyntacticItem(synStr, syntacticMap.get(synStr), query, result);
				try {
					insert(conn, synItem);
				} catch (SQLException e) {
					if(conn == null){
						conn = Util.getConnection();
						try {
							insert(conn, synItem);
						} catch (SQLException e1) {
//							logger_.error("sql exception:"+e1.toString());
						}
					}
				}
				int synItemListSize = syntacticItemMap.get(synStr);
				synItemListSize++;
				syntacticItemMap.put(synStr, synItemListSize);
				if(synItemListSize >= 10){
					syntacticItemMap.remove(synStr);
					syntacticMap.remove(synStr);
				}
			}
		}
	}
	
	public void insert(Connection conn,SyntacticItem synItem) throws SQLException{
		PreparedStatement pst = conn.prepareStatement(Util.SQL);
		pst.setString(1, synItem.getSyAndSmId());
		pst.setString(2, synItem.getDescription());
		pst.setString(3, synItem.getQuery());
		pst.setString(4, synItem.getResult());
		pst.executeUpdate();
		pst.close();
	}
}

class Util{
	static final String SQL = "insert into syntacticdb(syAndSmId,description,query,result) values(?,?,?,?)";
	static final String QUERY_FILE = "queryAll.txt";
	static final String SYN_FILE = "data/stock/stock_phrase_syntactic.xml"; 
	static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/mytestdb";
	static final String DB_USR = "root";
	static final String DB_PWD = "123456";
	static PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
	
	public static Connection getConnection() {
		try {
			Class.forName(Util.DB_DRIVER);
			Connection conn = DriverManager.getConnection(Util.DB_URL, Util.DB_USR, Util.DB_PWD);
			return conn;
		} catch (Exception e) {
			return null;
		}
	}
}

class SyntacticItem{
	private String syAndSmId;
	private String description;
	private String query;
	private String result;
	public SyntacticItem(String syAndSmId,String description,String query,String result){
		this.syAndSmId = syAndSmId;
		this.description = description;
		this.query = query;
		this.result = result;
	}
	@Override
	public String toString() {
		return String.format("syAndSmId:%s, description:%s, query:%s, result:%s", syAndSmId,description,query,result);
	}
	public String getSyAndSmId() {
		return syAndSmId;
	}
	public void setSyAndSmId(String syAndSmId) {
		this.syAndSmId = syAndSmId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}