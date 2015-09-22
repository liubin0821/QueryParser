package com.myhexin.qparser.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.util.ThreadMonitor;

public class ScoreParserServlet extends HttpServlet {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ScoreParserServlet.class.getName());
	private static final long serialVersionUID = 1L;
	private ThreadMonitor monitor = ThreadMonitor.getInstance();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //if(request.getParameter("qid")!=null)
        //	logger_.info("qid="+request.getParameter("qid"));
        

    	request.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html");

        String query = request.getParameter("q");
        query = query == null ? request.getParameter("query") : query;
        if (query == null) query = "";
        
        Thread thread = Thread.currentThread();
        monitor.addThread(thread, "ScoreParserServlet:" + query);
        
        try {
	        if (query != null && query.length() > 0 && query.length() <= Param.PARSED_MAX_CHARS) {
	        	int score = ScoreParser.parserQuery(query, null);
	        	OutputStream os = response.getOutputStream();
	            os.write(String.valueOf(score).getBytes("utf-8"));
	            os.close();
	        } else {
	        	// 日志1：问句超过50个字符不解析
	        	logger_.info("ScoreParser None parsed: len not in " + Param.PARSED_MAX_CHARS + "\tquery=" + query);
	        	OutputStream os = response.getOutputStream();
	            os.write("-1".getBytes("utf-8"));
	            os.close();
	        }
        } catch (Exception e) {
        	// 日志2：解析过程中出错，包一个try catch
        	logger_.info("ScoreParser None parsed: exception\tquery=" + query);
        	OutputStream os = response.getOutputStream();
            os.write("-1".getBytes("utf-8"));
            os.close();
        }finally {
        	monitor.removeThread(thread);
        }
    }
}
