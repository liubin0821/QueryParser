/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package bench.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bench.db.DBBenchManager;
import bench.CommandHandler;
import bench.BenchManager;
import bench.CommandResult;
import bench.ParserAgent;

import com.google.gson.Gson;

/**
 * the Class BenchServlet 
 * 提供回归测试集的问句添加、删除、相似浏览（按照结构、指标查询）。
 */
public class BenchJsonServlet extends HttpServlet{
	/** */
	private static final long serialVersionUID = 1L;
	
	private CommandHandler commandHandler = null;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		final String database = "ontoask";
		String parseConf = getServletContext().getRealPath("/conf/qparser.conf");
		try {
			ParserAgent.init(parseConf);
	    	BenchManager benchManager = new DBBenchManager(database);
	    	commandHandler = new CommandHandler(benchManager);
//	    	benchManager.updateOldPatternDataBase("oldPatternList.txt") ;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void destroy(){
		commandHandler.close();
	}
	
	@Override
    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
    	final String encoding = "utf-8";
    	httpResponse.setCharacterEncoding(encoding);   
		httpResponse.setContentType("text/html; charset="+encoding);
    	try {
    		PrintWriter writer = httpResponse.getWriter();
    		httpRequest.setCharacterEncoding(encoding);
    		String oper = new String(httpRequest.getParameter("oper").getBytes("ISO-8859-1"), "utf-8");

//    		System.out.println(String.format("----%s-----", oper));
			String text = new String(httpRequest.getParameter("text").getBytes("ISO-8859-1"), "utf-8").trim();
//			System.out.println(text);
			
    		String command = oper + " " + text;
    		CommandResult commandResult = commandHandler.handleCommand(command);
        	if(!"download".equals(oper)){
        		commandResult.sort();
        	}
    		String jsonResult = new Gson().toJson(commandResult);
    		writer.println(jsonResult);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    @Override
    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
    	System.out.println("-------doPost------");
    	final String encoding = "utf-8";
    	httpResponse.setCharacterEncoding(encoding);   
		httpResponse.setContentType("text/html; charset="+encoding);
    	try {
    		PrintWriter writer = httpResponse.getWriter();
    		httpRequest.setCharacterEncoding(encoding);
    		
    		String oper = new String(httpRequest.getParameter("oper").getBytes("ISO-8859-1"), "utf-8");
    		String text = httpRequest.getParameter("text");
    		
    		if(oper.equals("submit")){
    			CommandResult postResult = new Gson().fromJson(text, CommandResult.class);
    			CommandResult commandResult = commandHandler.handleSave(postResult);
    			commandResult.sort();
    			String jsonResult = new Gson().toJson(commandResult);
        		writer.println(jsonResult);
    		}else{
    			doGet(httpRequest, httpResponse);
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
