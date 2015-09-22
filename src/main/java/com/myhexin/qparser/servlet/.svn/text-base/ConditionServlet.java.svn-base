package com.myhexin.qparser.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.util.ThreadMonitor;
import com.myhexin.qparser.util.condition.ConditionParser;
import com.myhexin.qparser.util.condition.model.BackTestCondAnnotation;
import com.myhexin.server.TemplateServlet;

/**
 * 此接口用于取得condition
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2014-11-26
 *
 */
public class ConditionServlet extends TemplateServlet{
	private static final long serialVersionUID = 1L;	
	//public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(BacktestCondServlet.class.getName());
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
    	long beforeTime = System.currentTimeMillis();
    	super.doGet(request,response);
    	String query = request.getParameter("q");
    	if(query==null) query = request.getParameter("query");
    	if(query==null) query = request.getParameter("question");
    	
    	String qType = request.getParameter("qType");
    	if(qType==null) qType = request.getParameter("qtype");
    	if(qType==null) qType = request.getParameter("type");
    	
    	Thread thread = Thread.currentThread();
    	monitor.addThread(thread, "ConditionServlet:" + query);
    	
    	String postDataStr = request.getParameter("postData");
    	String domain = request.getParameter("domain");
    	String isXml = request.getParameter("xml");
    	String chunk = request.getParameter("chunk");
    	PrintWriter out = null;
        try{
        	out = response.getWriter(); 
	      	List<BackTestCondAnnotation> annotationList = ConditionParser.compileToCond(query, qType, domain, postDataStr);
	       	long afterTime = System.currentTimeMillis();
    		long timeDistance = afterTime - beforeTime;
    		
    		String result = null;
    		if(isXml!=null && isXml.equals("1")) {
    	    	response.setContentType("text/xml; charset=utf-8");
    	    	result = ServletXmlUtil.getConditionXmlOutput(query, postDataStr, timeDistance, annotationList);
    		}else{
    			response.setContentType("text/plain; charset=utf-8");
    			result = ServletXmlUtil.getConditionOnly(annotationList);
    		}
    		if(result==null) result = Consts.STR_NULL_STR;
   	        out.println(result);
        }catch (Exception e) {
    		e.printStackTrace();
    	}finally {
    		if(out!=null ) {
    			out.flush();
    			out.close();
    		}
    		monitor.removeThread(thread);
    	}
        
    }
    

    
    
}
