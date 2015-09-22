package com.myhexin.qparser.server.suggest;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IfindLocalSuggestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static IndicatorLocalSort aIndicatorLocalSort_;

	static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
			.getLogger(IfindSuggestionServlet.class.getName());

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		aIndicatorLocalSort_ = new IndicatorLocalSort();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("GBK");
		response.setContentType("text/html;charset=gbk");
		
		String rawSentence = new String(request.getParameter("q").getBytes(
				"ISO-8859-1"), "UTF-8");
		String updateIndex = "false";
		
		try{
		    updateIndex = new String(request.getParameter("upindex").getBytes(
				"ISO-8859-1"), "UTF-8");
		}catch( Exception e ){
		}
		
		if( (null != updateIndex) && updateIndex.equals("true") ){
			IndicatorIndex aIndicatorIndex = new IndicatorIndex();
			if( ! aIndicatorIndex.yyMakeIndIndex() ){
				logger_.error("update index failed");
				response.getWriter().print("");
				return;
			}else{
				logger_.info("update index succeed");
			}
		}
		
		rawSentence = rawSentence.trim();
		
		String resStr = aIndicatorLocalSort_.getResStr(rawSentence);
		String anstring = "";
		
		if( null != resStr ){
			anstring = resStr;
		}
		
		response.getWriter().print(anstring);
	}
}
