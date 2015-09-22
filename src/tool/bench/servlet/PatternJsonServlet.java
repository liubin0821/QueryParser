/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package bench.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bench.ParserAgent;

import com.google.gson.Gson;
import com.myhexin.qparser.ParseLog;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.QueryParser;
import com.myhexin.qparser.util.StrStrPair;

/**
 * the Class PatternSeverlet 
 * 运营配置Pattern时，提供问句的匹配前分词、匹配Pattern、匹配后节点、解析树查询功能。
 */
public class PatternJsonServlet extends HttpServlet{
	/** */
	private static final long serialVersionUID = 1L;
	private QueryParser queryParser;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		if(ParserAgent.queryParser == null){
			String conf = getServletContext().getRealPath("/conf/qparser.conf");
			ParserAgent.queryParser = QueryParser.getParser(conf);
		}
		queryParser = ParserAgent.queryParser;
	}
	
	@Override
    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
    	doCommon(httpRequest, httpResponse);
    }

    @Override
    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        doCommon(httpRequest, httpResponse) ;
    }
    
    protected void doCommon(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
    	final String encoding = "utf-8";
    	httpResponse.setCharacterEncoding(encoding);   
		httpResponse.setContentType("text/html; charset="+encoding);    
		
    	try {
    		PrintWriter writer = httpResponse.getWriter();
    		httpRequest.setCharacterEncoding(encoding);
    		String queryText = httpRequest.getParameter("text");
    		HashMap<String, String> jsonMap = new HashMap<String, String>();
    		
    		if(queryText == null){
    			jsonMap.put("result", "error");
    		}else{
    			queryText = new String(queryText.getBytes("8859_1"), "GB2312");
    			Query query = new Query(queryText);
        		queryParser.parse(query);
        		
        		ArrayList<StrStrPair> pairs = query.getLog().getTransWords();
        		String patterns = query.getLog().getMsg(ParseLog.LOG_PATTERN);
        		String treeString = query.getLog().getMsg(ParseLog.LOG_TREE);
        		query.getLog().getTransWords();
        		if(patterns.equals("")){
        			patterns = "该问句，没有任何Pattern匹配";
        		}
        		StringBuilder trans = new StringBuilder();
        		for(StrStrPair pair : pairs){
        			trans.append(pair.first);
        			trans.append(" ≈ ");
        			trans.append(pair.second);
        			trans.append("\n");
        		}
        		
        		jsonMap.put("result", "ok");
        		jsonMap.put("query", queryText);
        		jsonMap.put("trans", trans.toString().trim());
        		jsonMap.put("pattern", patterns);
        		jsonMap.put("tree", treeString);
    		}
    		
    		String jsonResult = new Gson().toJson(jsonMap);
    		writer.println(jsonResult);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
