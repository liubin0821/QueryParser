package com.myhexin.qparser.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.myhexin.server.TemplateServlet;

/**
 * 该服务用于得到分钟线解析结果
 * 
 * @author 柴华   chaihua@myhexin.com
 * @Version 创建时间 2015-05-07
 *
 */
public class MinuteDataBackTestServlet extends TemplateServlet{
	private static final long serialVersionUID = 1L;	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(MinuteDataBackTestServlet.class.getName());
	//private ThreadMonitor monitor = ThreadMonitor.getInstance();
	private static String ret_json = null;
	static{
		List<String> hard_code_json= new ArrayList<String>();
		hard_code_json.add("not suppoered.");
		ret_json = new Gson().toJson(ret_json);
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
    	
    	//2015-09-10 陈红说这个接口不再使用了
    	//所以去掉
    	//TODO 发布上线后没问题，就删掉相关代码
    	
    	//String query = request.getParameter("query");
    	//Thread thread = Thread.currentThread();
    	//monitor.addThread(thread, "MinuteDataBackTestServlet:" + query);
    	PrintWriter out = null;
    	try{
        	out = response.getWriter(); 
        	//List<BackTestMinuteCondition> conditionList = MinuteDataBackTestQueryParser.parseMinuteDataQuery(query);
        	//String jsonResult = new Gson().toJson(conditionList);
        	response.setContentType("text/plain; charset=utf-8");
        	out.println(ret_json);
        }catch (Exception e) {
    		logger_.error(e.toString());
    	}finally {
    		if(out!=null ) {
    			out.flush();
    			out.close();
    		}
    		
    		//monitor.removeThread(thread);
    	}
        
    }
}
