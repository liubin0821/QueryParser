package com.myhexin.qparser.util.itoperation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.PhraseParserFactory;
import com.myhexin.qparser.phrase.PhraseParserUtil;
import com.myhexin.qparser.util.condition.ConditionBuilderUtil;


public class FindoutUnknowTypeNode {
	private static String stop_after_plugin="Date_Sequence"; //"Match_Index_and_Keywords";
	//private static int batchSize = 1000;
	private static Pattern pa=Pattern.compile("^[,.;!\\s]{1}$");
	private static Pattern pa2=Pattern.compile("^(年|月|日|周|季度|元|个|只|天|倍|也|位)$");
	private static PhraseParser parser = PhraseParserFactory.createPhraseParser(PhraseParserFactory.ParserName_Condition);
    static class Unknow{
		Integer id;
		String text;
		String query;
		String nodes;
		
		public String toString() {
			return text + "__&&__" + nodes  + "__&&__"  +query;
		}
	}
    private static boolean isUnknow(String text) {
    	if(pa.matcher(text).matches()) {
    		return false;
    	}
    	
    	if(pa2.matcher(text).matches()) {
    		return false;
    	}
    	
    	for(Pattern p : configPatternList) {
    		if(p.matcher(text).matches()) {
    			return false;
    		}
    	}
    	
    	for(String s : textSkipList) {
    		if(s.equals(text)) {
    			return false;
    		}
    	}
    	
    	return true;
    }
	
    private static List<Pattern> configPatternList = new ArrayList<Pattern>();
    private static void readRegexList() throws IOException {
    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("./regex.txt")));
		String s = null;
		while( (s=br.readLine())!=null) {
			try{
				configPatternList.add(Pattern.compile(s));
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		br.close();
    }
    
    
    private static List<String> textSkipList = new ArrayList<String>();
    private static void readSkipTextList() throws IOException {
    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("./skip.txt")));
		String s = null;
		while( (s=br.readLine())!=null) {
			try{
				textSkipList.add(s);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		br.close();
    }
    
	public static void main(String args[]) throws Exception{
		ApplicationContextHelper.loadApplicationContext();
		
		//读取正则
		readRegexList();
		readSkipTextList();
		
		
		System.out.println("开始...");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
		String s = null;
		
		PrintWriter out = new PrintWriter(new FileOutputStream(args[1]));
		long i=0;
		while( (s=br.readLine())!=null) {
	    	ParseResult pr= parse(s,stop_after_plugin);
	    	   
	    	   List<Unknow> subList=null;
	    	   if(pr.qlist!=null){
	    		   subList=getUnknowText(s, pr.qlist);
	    		   for(Unknow un : subList) {
	    			   out.println(un.toString());
	    		   }
	    	   }
	    	   
	    	   if(i++%10000 ==0) {
					System.out.println(i +" queries parsed");
				}
	    }
		br.close();
		out.close();
	    System.out.println("结束...");
	}
	
	private static List<Unknow> getUnknowText(String query, ArrayList<ArrayList<SemanticNode>>  listList){
		List<Unknow> tests=new ArrayList<Unknow>();
		for(ArrayList<SemanticNode> list:listList){
			for(int i=0;i<list.size();i++){
				String nodestr = nodes(list);
				if(list.get(i).getType()==NodeType.UNKNOWN){
					String text = list.get(i).getText();
					if(isUnknow(text))
					{
						Unknow nk=new Unknow();
 		    		   	nk.text=text;
 		    		   	nk.query = query;
 		    		   	nk.nodes = nodestr;
 		    		   	tests.add(nk); 
					}
				}
			}
		}
		return tests;
	}
	
	
	private static String nodes(ArrayList<SemanticNode> nodes) {
		List<String> tests=new ArrayList<String>();
		for(SemanticNode node:nodes){
			if(node.getType() == NodeType.ENV) continue;
			if(node.getText()==null || node.getText().trim().length()==0) continue;
			tests.add(node.getText());
		}
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String s= ConditionBuilderUtil.unicodeEsc2Unicode(gson.toJson(tests));
		return s;
	}
	
	/*private Map<Integer,String> getText(int start,int size){
		 HashMap<Integer,String> hm=new HashMap<Integer, String>();
		 String sql="select ID,query from sampling_queries  LIMIT  "+ start + "," + size + "";
		 Connection conn=getConnection();
		 ResultSet set=null;
		 Statement stmt=null;
		 try {
			 stmt=(Statement) conn.createStatement();
			 set=stmt.executeQuery(sql);
			 while (set.next()) {
					int id=set.getInt("ID");
					String qu=set.getString("query");
					hm.put(id, qu);
				}
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 closeAll(conn,stmt,set);
		 }
		 return hm;
	}*/
	
	private static ParseResult parse(String query, String stop_after_plugin){
		/*Query query = new Query(querys.toLowerCase());
		query.setType(Type.ALL);
    	return parser.parse(query,stop_after_plugin);*/
    	return PhraseParserUtil.parse(parser, query.toLowerCase(), "ALL", null, null, stop_after_plugin);
	}
	
	//private static Connection conn_;
	//ALTER TABLE sampling_unknown_text ADD COLUMN original_query VARCHAR(1000) NULL
	/*private void save(List<Unknow> nuknowList){
	  
	   String sql="insert ignore into sampling_unknown_text(ID,txt, original_query) values (?,?, ?)";
	   PreparedStatement pstmt=null;
	   try {
		   if(conn_==null)
		   {
			   conn_=getConnection();
			   conn_.setAutoCommit(false);
		   }
		
		pstmt=(PreparedStatement) conn_.prepareStatement(sql);
		for(Unknow nk:nuknowList){
			pstmt.setInt(1,nk.id);
			pstmt.setString(2, nk.text);
			pstmt.setString(3, nk.query);
			pstmt.addBatch();
		}
		 pstmt.executeBatch();
		 conn_.commit();
	} catch (SQLException e) {
		 e.printStackTrace();
	}
	   
   }*/
   
   /*private Connection getConnection(){
	    try {
			DataSource ds = ApplicationContextHelper.getBean("dataSource");
			return ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    return null; 
	   
   }*/
   
   /*private void closeAll(Connection connection, Statement statement, ResultSet resultSet){
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
   }*/
}
