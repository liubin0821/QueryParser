package com.myhexin.qparser.ml;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement.SyntElemType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.KeywordGroup;
import com.myhexin.qparser.util.GsonUtil;

public class NodesSemanticInfoBuilder {

	/*private static String DATABASE_USERNAME=null;
	private static String CLASS_DRIVER= "com.mysql.jdbc.Driver";;
	private static String DATABASE_PASSWORD=null;
	private static String CONN_URL = null;*/
	static PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
	static {
		   parser.setSplitWords("_&_");
	}
	
	public static List<NodesSemanticInfo> getChunkQueries() {
		Connection con = null;
		
		List<NodesSemanticInfo> nodes = new ArrayList<NodesSemanticInfo>();
		
		try{
			con = getC();
			PreparedStatement pstmt = con.prepareStatement("select chunk from queryChunk.query LIMIT 10000");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				String s = rs.getString(1);
				if(s!=null) {
					String[] chunks = s.split("_&_");
					if(chunks!=null && chunks.length>0) {
						for(String s1 : chunks) {
							NodesSemanticInfo info = new NodesSemanticInfo();
							info.setQuery(s1.trim());
							nodes.add(info);
						}
						
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		
		return nodes;
	}
	
	
	public static List<NodesSemanticInfo> getNodes(List<NodesSemanticInfo> nodes) {
		for(NodesSemanticInfo info : nodes) {
			Query query = new Query(info.getQuery().toLowerCase());
			query.setType(Type.ALL);
			ParserAnnotation annotation = new ParserAnnotation();
	    	annotation.setQueryText(query.text);
	    	annotation.setQueryType(query.getType());
	    	annotation.setQuery(query);
	    	ParseResult pr = parser.parse(annotation, "Remove_Some_Condition_Node");
	    	if(pr!=null && pr.qlist!=null && pr.qlist.size()>0) {
	    		info.setNodes(pr.qlist.get(0));
	    	}
		}
		return nodes;
	}
	
	
	public static List<NodesSemanticInfo> getSyntactSemantic(List<NodesSemanticInfo> nodes) {
		for(NodesSemanticInfo info : nodes) {
			Query query = new Query(info.getQuery().toLowerCase());
			query.setType(Type.ALL);
			ParserAnnotation annotation = new ParserAnnotation();
	    	annotation.setQueryText(query.text);
	    	annotation.setQueryType(query.getType());
	    	annotation.setQuery(query);
	    	ParseResult pr = parser.parse(annotation);
	    	if(pr!=null && pr.qlist!=null && pr.qlist.size()>0) {
	    		ArrayList<SemanticNode> snodes = pr.qlist.get(0);
	    		int x=0;
				for (SemanticNode sn : snodes) {
					if(x==0) {
						info.setScore(sn.getScore());
					}
					x++;
					if (sn.type == NodeType.BOUNDARY) {
						BoundaryNode bn = (BoundaryNode) sn;
						if (bn.isStart()) {
							String syntacticPatternId = bn.getSyntacticPatternId();
							info.setSyntId(syntacticPatternId);
							if (BoundaryNode.getImplicitPattern(syntacticPatternId) == null) {
								//句式
			                    SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(syntacticPatternId);
			                    info.setSyntactDesc(syntPtn.getDescription());
			                    int max = syntPtn.getSyntacticElementMax();
			                    
			                    StringBuilder syntDesc = new StringBuilder();
			                    StringBuilder syntDesc2 = new StringBuilder();
			                    for(int i=1;i<max;i++) {
			                    	SyntacticElement elem = syntPtn.getSyntacticElement(i);
			                    	if(elem.getType()==SyntElemType.ARGUMENT) {
			                    		syntDesc.append(elem.getArgumentType());
			                    		syntDesc2.append(elem.getArgumentType());
			                    	}else if(elem.getType()==SyntElemType.KEYWORD){
			                    		syntDesc.append(elem.getType());
			                    		String kg = elem.getKeywordGroup();
			                    		KeywordGroup kgName = PhraseInfo.getKeywordGroup(kg);
			                    		syntDesc2.append(kgName.getDescription());
			                    	}else{
			                    		syntDesc.append(elem.getArgumentType());
			                    		syntDesc2.append(elem.getArgumentType());
			                    	}
			                    	
			                    	if(i<max-1) {
			                    		syntDesc.append(',');
			                    		syntDesc2.append(',');
			                    	}
			                    }
			                    info.setSyntactDesc2(syntDesc.toString());
			                    info.setSyntactDesc3(syntDesc2.toString());
			                    
			                    //语义
			                    SemanticBind bind= syntPtn.getSemanticBind();
			                    ArrayList<SemanticBindTo> list = bind.getSemanticBindTos();
			                    for(SemanticBindTo bindTo : list) {
			                    	String semanticPatternId = String.valueOf(bindTo.getBindToId());
				                    SemanticPattern semanticPattern = PhraseInfo.getSemanticPattern(semanticPatternId);
				                    if(semanticPattern==null){
				                    	System.out.println("SemanticPattern of id=" + semanticPatternId + " is Null");
				                    	continue;
				                    }
				                    String chDesc = semanticPattern.getChineseRepresentation();
				                    int argCount = semanticPattern.getSemanticArgumentCount();
				                    String desc2 = new String(chDesc);
				                    for(int j=0;j<argCount;j++) {
				                    	SemanticArgument semanticArg = semanticPattern.getSemanticArgument(j+1, true);
				                    	if(desc2!=null && semanticArg!=null) desc2 = desc2.replaceAll(String.valueOf(j+1), String.valueOf(semanticArg.getType()));
				                    }
				                    info.setSemanticDesc(chDesc);
				                   	StringBuilder buf = new StringBuilder();
				                   	for(int n=0;n<desc2.length();n++) {
				                   		char c = desc2.charAt(n);
				                   		if(c=='$')continue;
				                   		
				                   		buf.append(c);
				                   	}
				                    info.setSemanticDesc2(buf.toString());
				                    
				                    break;
			                    }
							}else{
								info.setSyntactDesc(syntacticPatternId);
							}
			            }
					}
				}
	    	}
		}
		return nodes;
		
	}
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length<3) {
			System.out.println("url user password");
			System.exit(1);
		}
		
		/*CONN_URL = args[0];
		DATABASE_USERNAME = args[1];
		DATABASE_PASSWORD = args[2];*/

		ApplicationContextHelper.loadApplicationContext();
		
		List<NodesSemanticInfo> nodes= getChunkQueries();
		nodes =  getNodes(nodes);
		nodes = getSyntactSemantic(nodes);
		
		save(nodes);
	}
	
	
	static class NodeInfo{
		int index;
		String text;
		String type;
		
		public NodeInfo(int i,String text, String type) {
			this.index = i;
			this.text = text;
			this.type  = type;
		}
	}
	
	private static void save(List<NodesSemanticInfo> nodes) {
		Connection con = null;
		try{
			con = getC();
			con.setAutoCommit(false);
			PreparedStatement pstmt = con.prepareStatement("INSERT INTO configFile.query_parser_ml(query, synt_desc1,synt_desc2,semantic_desc1,semantic_desc2,nodes_json,score,synt_id,semantic_id) values(?,?,?,?,?,?,?,?,?)");
			for(NodesSemanticInfo info : nodes) {
				pstmt.setString(1, info.getQuery());
				pstmt.setString(2, info.getSyntactDesc2());
				pstmt.setString(3, info.getSyntactDesc3());
				
				pstmt.setString(4, info.getSemanticDesc2());
				pstmt.setString(5, null);
				
				Gson gson = new GsonBuilder().create();
				List<NodeInfo> infos = new ArrayList<NodeInfo>();
				
				int i=0;
				for(SemanticNode node : info.getNodes()) {
					if(node.getType() == NodeType.ENV) continue;
					infos.add(new NodeInfo(i, node.getText(), String.valueOf(node.getType())) );
					i++;
				}
				
				
				String json = GsonUtil.unicodeEsc2Unicode(gson.toJson(infos));
				
				pstmt.setString(6, json);
				pstmt.setInt(7, info.getScore());
				pstmt.setString(8, info.getSyntId());
				pstmt.setString(9, info.getSemanticId());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			con.commit();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	private static Connection getC() {
		/*try {
			Class.forName(CLASS_DRIVER);
			return DriverManager.getConnection(CONN_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
		} catch (Exception e) {
			System.out.println("Load JDBC Properties failed :  "+ e.getMessage());
			e.printStackTrace();
		} 
		return null;*/
		try {
			DataSource dataSource = (DataSource)ApplicationContextHelper.getBean("dataSource");
			return dataSource.getConnection();
		}catch (Exception e) {
			System.out.println("Load JDBC Properties failed :  "+ e.getMessage());
			e.printStackTrace();
		} 
		return null;
	}

}
