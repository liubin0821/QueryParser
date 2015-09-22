package com.myhexin.qparser.servlet.op;

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

public class SampleRuner{
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(SampleRuner.class.getName());
	static PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
	static {
		parser.setSplitWords("_&_");
	}
	
	private ParseResult qunQuery(String str) {
		Query query = new Query(str.toLowerCase());
		query.setType(Type.ALL);
		ParserAnnotation annotation = new ParserAnnotation();
    	annotation.getNodes();
    	annotation.setQueryType( query.getType());
    	annotation.setQuery(query);
    	return parser.parse(annotation);
	}
	
	private String findSyntacticSemanticInfo(ArrayList<SemanticNode> nodes) {
		StringBuilder buf = new StringBuilder();
		//找到句式语义
		for (SemanticNode sn : nodes) {
            if (sn.type == NodeType.BOUNDARY) {
                BoundaryNode bn = (BoundaryNode) sn;
                if (bn.isStart()) {
                    String syntacticPatternId = bn.getSyntacticPatternId();
                    buf.append(syntacticPatternId).append(":");
                    if (BoundaryNode.getImplicitPattern(syntacticPatternId) == null) {
            		    SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(syntacticPatternId);
            		    String semanticPatternId = syntPtn.getSemanticBind().getId();
            		    semanticPatternId = semanticPatternId!=null ? semanticPatternId : syntPtn.getSemanticBind().getSemanticBindToIds();
            		    String[] temps = semanticPatternId.split("&");
            		    if(temps!=null && temps.length>0) {
            		    	for (int i=0;i<temps.length-1;i++) {
	            		    	buf.append(temps[i]).append(",");
	            		    }
            		    	buf.append(temps[temps.length-1]).append(' ');
            		    }
                    }
                }else {
                	buf.append("_&_");
                }
            }
		}
		if(buf.length()>3 && buf.charAt(buf.length()-1)=='_' && buf.charAt(buf.length()-2)=='&' && buf.charAt(buf.length()-3)=='_') {
			return buf.substring(0, buf.length()-3);
		}
		return buf.toString();
	}
	
	public List<Rt> runQueries(List<String[]> queries) {
		List<Rt> list = new ArrayList<Rt>();
		for(String[] q : queries) {
			long start = System.currentTimeMillis();
			ParseResult pr = qunQuery(q[1]);
			if(pr!=null && pr.qlist!=null && pr.qlist.size()>0) {
				String syntSemanticInfo = null;
				int score = 0;
				ArrayList<SemanticNode> nodes = pr.qlist.get(0);
				syntSemanticInfo = findSyntacticSemanticInfo(nodes);
				if(pr.standardQueriesScore!=null && pr.standardQueriesScore.size()>0 ) {
					score = pr.standardQueriesScore.get(0);
				}
				
				String stdQuery = null;
				if(pr.standardQueries!=null && pr.standardQueries.size()>0) {
					stdQuery = pr.standardQueries.get(0);
				}
				
				Rt rt = new Rt();
				rt.id = Integer.parseInt(q[0]);
				rt.query = q[1];
				rt.syntSemanticInfo = syntSemanticInfo;
				rt.score = score;
				rt.stdQuery = stdQuery;
				long end = System.currentTimeMillis();
				rt.used_time = (int)(end-start);
				list.add(rt);
			}
		}
		return list;
	}
	

	private OpDbSampleQuery db = new OpDbSampleQuery();
	
	protected void runSingle(String idStr ){
		
		int id = Integer.parseInt(idStr);
		String query = db.getQuery(id);
		List<String[]> list = new ArrayList<String[]>(1);
		list.add(new String[]{idStr, query});
		List<Rt> rList = runQueries(list);
		
		if(rList!=null)
			db.updateResult(rList);
	}
	
	protected void runBatch(String batch,String start) {
		
		List<Rt> rList = null;
		if(batch!=null && start!=null) {
			try{
			int startInt = Integer.parseInt(start);
			List<String[]> qs = db.getSampleQueries(startInt*SampleQueryViewServlet.batchSize, SampleQueryViewServlet.batchSize);
			System.out.println("query size= " + qs.size());
			rList = runQueries(qs);
			for(Rt r : rList) {
				System.out.println(r.id + "," + r.query);
			}
			System.out.println("rList size= " + rList.size());
			System.out.println("start= " + start);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		
		if(rList!=null)
		{
			db.updateResult(rList);
		}
	}
}
