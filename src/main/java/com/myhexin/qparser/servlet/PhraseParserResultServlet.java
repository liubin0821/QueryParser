package com.myhexin.qparser.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.PhraseParserFactory;
import com.myhexin.qparser.phrase.PhraseParserUtil;
import com.myhexin.qparser.util.condition.ConditionBuilderUtil;
import com.myhexin.qparser.util.condition.ConditionParser;
import com.myhexin.server.TemplateServlet;

/**
 * 此类用于直接查看json字符串构建的结果
 * @author admin
 *
 */
public class PhraseParserResultServlet extends TemplateServlet{
	
	private static final long serialVersionUID = 1L;	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserResultServlet.class.getName());
    private static PhraseParser parser = PhraseParserFactory.createPhraseParser(PhraseParserFactory.ParserName_Condition);
    @Override
    public void init() throws ServletException {
        super.init();
    }
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	super.doGet(request,response);
    	
        String query = request.getParameter("q");
        String qType = request.getParameter("qType");
        String domain = request.getParameter("domain");
        String postDataStr = request.getParameter("postData");
        
        query = query == null ? request.getParameter("question") : query;
        if(qType==null) qType= "ALL";
        
        String backtestTimeStr = ConditionParser.getBackTestTimeFromJson(postDataStr);
        Calendar backtestTime = ConditionParser.getBackTestTime(backtestTimeStr);
        
        Map<String, String> result = new HashMap<String, String>();
        result.put("query", query!=null?query:"");
        
        String score = null;
        String ids = null;
        String index_names = null;
        String standard_query = null;
        String msg = null;
        if (query != null && query.trim().length()>0) {
        	ParseResult pr = PhraseParserUtil.parse(parser, query, qType, domain, backtestTime);
        	score = String.valueOf(pr.getScore());
        	ids = pr.getSyntacticSemanticIds();
        	index_names = pr.getIndexNames();
        	//result.put("score", String.valueOf(pr.getScore()) );
        	//result.put("syntacticSemanticIds", pr.getSyntacticSemanticIds());
        	//result.put("index_names", pr.getIndexNames());
        	if(pr.getStandardQueries()!=null && pr.getStandardQueries().size()>0) {
        		standard_query = pr.getStandardQueries().get(0);
        	}
		}else{
			msg =  "query is null";
			//result.put("msg", "query is null");
		}
        result.put("query", query!=null?query:"");
        result.put("score", score!=null?score:"");
        result.put("syntacticSemanticIds", ids!=null?ids:"");
        result.put("index_names", index_names!=null?index_names:"");
        result.put("standard_query", standard_query!=null?standard_query:"");
        result.put("msg", msg!=null?msg:"");
        
        PrintWriter out = response.getWriter(); 
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String s= ConditionBuilderUtil.unicodeEsc2Unicode(gson.toJson(result));
		out.println(s);
        out.close();
    }
}
