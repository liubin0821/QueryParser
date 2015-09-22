package com.myhexin.qparser.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.PhraseParserFactory;
import com.myhexin.qparser.phrase.PhraseParserUtil;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.util.condition.ConditionBuilderUtil;
import com.myhexin.server.TemplateServlet;

public class DateParserServlet extends TemplateServlet{
	private static final long serialVersionUID = 1L;	
	//public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(BacktestCondServlet.class.getName());
    
    @Override
    public void init() throws ServletException {
        super.init();
    }
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doGet(request, response);
    }
    
    private final static PhraseParser parser = PhraseParserFactory.createPhraseParser(PhraseParserFactory.ParserName_Condition);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String query = request.getParameter("q");
    	String qType = request.getParameter("qType");
    	
    	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    	
    	ParseResult pr = PhraseParserUtil.parse(parser, query, qType, null);
    	if(pr!=null && pr.qlist!=null && pr.qlist.size()>0) {
    		ArrayList<SemanticNode> nodes = pr.qlist.get(0);
    		for(SemanticNode node : nodes) {
    			if(node.isDateNode()) {
    				Map<String, String> s = dateNodeToString((DateNode) node);
    				if(s!=null) list.add(s);
    			}
    		}
    	}
    	response.setContentType("text/plain; charset=utf-8");
    	Gson gson = new GsonBuilder().create();
		String json= ConditionBuilderUtil.unicodeEsc2Unicode(gson.toJson(list));
		PrintWriter out = response.getWriter();
		out.println(json);
		out.close();
    }

    
    private Map<String, String> dateNodeToString(DateNode node) {
    	if(node!=null) {
    		Map<String, String> retMap = new HashMap<String, String>();
    		retMap.put("text", node.getText());
    		String fromStr=null;
    		String toStr= null;
    		if(node.getDateinfo()!=null) {
    			DateInfoNode from = node.getDateinfo().getFrom();
    			DateInfoNode to = node.getDateinfo().getTo();
    			
    			if(from!=null) {
    				fromStr = from.toString("-");
    				toStr = to.toString("-");
    			}
    		}
    		if(fromStr==null) fromStr = Consts.STR_BLANK;
    		if(toStr==null) toStr = Consts.STR_BLANK;
    		retMap.put("from", fromStr);
    		retMap.put("to", toStr);
    		return retMap;
    	}
    	return null;
    }
}
