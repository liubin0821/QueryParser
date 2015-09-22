package com.myhexin.qparser.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myhexin.qparser.util.ThreadMonitor;
import com.myhexin.server.TemplateServlet;

/**相关问句接口*/
public class BacktestRelatedQueryServlet extends TemplateServlet{
	private static final long serialVersionUID = 1L;	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(BacktestRelatedQueryServlet.class.getName());
	private ThreadMonitor monitor = ThreadMonitor.getInstance();
	
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
    	String query = request.getParameter("query");
    	String qType = request.getParameter("qType");
    	String postDataStr = request.getParameter("postData");
    	String domain = request.getParameter("domain");
    	PrintWriter out = null;
        Thread thread = Thread.currentThread();
        monitor.addThread(thread, "BacktestRelatedQueryServlet:" + query);
    	
        try{
        	out = response.getWriter(); 
    	    String result=BacktestRelatedQueryUtil.compileToCondWithMoreInfo(query, qType, domain, postDataStr);
        	response.setContentType("text/plain; charset=utf-8");
        	out.println(result);
        }catch (Exception e) {
    		logger_.error(e.toString());
    	}finally {
    		if(out!=null ) {
    			out.flush();
    			out.close();
    		}
    		
    		monitor.removeThread(thread);
    	}
        
    } 
    
}
