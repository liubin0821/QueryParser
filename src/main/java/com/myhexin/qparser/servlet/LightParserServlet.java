package com.myhexin.qparser.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myhexin.qparser.util.ThreadMonitor;
import com.myhexin.qparser.util.lightparser.LightParser;
import com.myhexin.qparser.util.lightparser.LightParserServletParam;

public class LightParserServlet extends HttpServlet {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(LightParserServlet.class.getName());
	private static final long serialVersionUID = 1L;
	private ThreadMonitor monitor = ThreadMonitor.getInstance();
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //if(request.getParameter("qid")!=null)
        //	logger_.info("qid="+request.getParameter("qid"));
        

    	request.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=utf-8");

        
        LightParserServletParam requestParam = new LightParserServletParam(request);
        Thread thread = Thread.currentThread();
        monitor.addThread(thread, requestParam.getQuery());
        
        
        /*String query = request.getParameter("q");      
        String cmd = request.getParameter("cmd");
		String cmd_web = request.getParameter("cmd_web");
		String options = request.getParameter("options");
		String debug = request.getParameter("debug");
		String channel = request.getParameter("channel");*/
		
		
        /*if (query == null) query = "";
        if (cmd == null) cmd = "";
        if (cmd_web == null) cmd_web = "";
        if (options == null) options = "";*/
        
        PrintWriter os = null;
        try {
        	os = response.getWriter();
        	//处理q=*:*的情况, *:*的问句有占所有问句50%
            if(requestParam.isStarColonStar()) {
            	LightParser.createLightParserJson(null, os);
            }else
            	LightParser.createLightParserJson(requestParam, os);
        }catch (Exception e) {
        	LightParser.createLightParserJson(null, os);
            logger_.error(e.getMessage());
		}finally {
			if(os!=null) {
				os.close();
			}
			monitor.removeThread(thread);
		}
    }
}
