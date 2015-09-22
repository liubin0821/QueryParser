package com.myhexin.qparser.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.server.TemplateServlet;

public class ParserPatternServlet extends TemplateServlet{
	private static final long serialVersionUID = 1L;
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ParserPatternServlet.class);
	private static PhraseParser parser;
	static{
		parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
	}
	
	 @Override
	    public void init() throws ServletException {
	        super.init();
	    }
	 
	    @Override
	    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    	doGet(request, response);
	    }

	    @Override
	    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    	super.doGet(request,response);
	    	
	    	String query=request.getParameter("q");
	    	if(query==null){
	    		query=request.getParameter("query");
	    	}
	    	String qType = request.getParameter("qType");
	    	if(qType==null){
	    		qType=request.getParameter("qtype");
	    	}
	    	StringBuilder buf = new StringBuilder();
	    	if(query!=null) {
	    		parser.setSplitWords("_&_");
	    		Query q = new Query(query.toLowerCase(), qType);
	    		ParseResult ret = null;
	            try {
	            	ParserAnnotation annotation = new ParserAnnotation();
	            	annotation.setQueryText(q.text);
	            	annotation.setQuery(q);
	            	annotation.setQueryType(q.getType());
	            	annotation.setWriteLog(true);
	            	
	    		    ret = parser.parse(annotation);
	            } catch(Exception e) {
	            	logger_.error(ExceptionUtil.getStackTrace(e));
	            }

	    		if (ret != null) {
	    			ArrayList<ArrayList<SemanticNode>> qList = ret.qlist;
	    			if(qList!=null && qList.size()>0) {
	    				ArrayList<SemanticNode> nodes = qList.get(0);
	    				/*List<FocusNode> fList = new ArrayList<FocusNode>();
	    				for(SemanticNode node : nodes) {
	    					if(node!=null && node.isIndexNode()) {
	    						FocusNode fNode = (FocusNode) node;
	    						ClassNodeFacade fn = fNode.getIndex();
	    						if(fn!=null) {
	    							buf.append(fn.getText()).append("_&_");
	    						}
	    					}
	    				}*/
	    				
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
	    		            }else if (sn.isIndexNode()) {
	    		            	FocusNode fNode = (FocusNode) sn;
	    						ClassNodeFacade fn = fNode.getIndex();
	    						if(fn!=null) {
	    							char c = buf.charAt(buf.length()-1);
	    							if(c!=' ') {
	    								buf.append(' ');
	    							}
	    							buf.append(fn.getText());
	    						}
	    		            }
	    				}
	    			}
	    			else{
			    		buf.append("解析出错!");
			    	}
	    		}else{
		    		buf.append("解析出错!");
		    	}
	    		
	    	}else{
	    		buf.append("请输入查询语句!");
	    	}
	    	
	    	//去掉最后一个_&_
	    	int idx = buf.lastIndexOf("_&_");
	    	String ret = null;
	    	if(idx>0 && idx==buf.length()-3) {
	    		ret = buf.substring(0, idx);
	    	}else{
	    		ret = buf.toString();
	    	}
	    	
	    	PrintWriter out = null;
	    	try{
		    	response.setContentType("text/plain; charset=utf-8");
		    	out = response.getWriter(); 
		    	out.println(ret);
	    	}catch(Exception e) {
	    		e.printStackTrace();
			}finally {
				if(out!=null ) {
					out.flush();
					out.close();
				}
			}
	    }
}
